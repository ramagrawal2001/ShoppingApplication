package com.example.shoppingapplication.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapplication.MainActivity;
import com.example.shoppingapplication.R;
import com.example.shoppingapplication.adapters.ProductAdapter;
import com.example.shoppingapplication.models.Product;
import com.example.shoppingapplication.viewmodels.ProductsViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class ProductGridScreenFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private RecyclerView recyclerView;
    private ProductsViewModel viewModel;
    private ProgressDialog loadingDialog;
    private Toast errorMessage;

    private int spanCount = 2;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 3;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 2;
        }

        // Update the span count of the GridLayoutManager
        ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(spanCount);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_grid_screen, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        Spinner spinner = view.findViewById(R.id.category_dropdown);


        // Initialize the ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(ProductsViewModel.class);

        // Observe the LiveData from the ViewModel
        viewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                handleProductsList(products);
            } else {
                showError(getString(R.string.product_list_empty));
            }
        });

        viewModel.getRequestStatusLiveData().observe(getViewLifecycleOwner(), requestStatus -> {
            if (requestStatus == ProductsViewModel.RequestStatus.IN_PROGRESS) {
                showSpinner();
            } else {
                hideSpinner();
            }

            if (requestStatus == ProductsViewModel.RequestStatus.FAILED) {
                showError(getString(R.string.failed_to_fetch_product));
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                viewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
                    if (products != null) {
                        List<Product> selectedProducts = new ArrayList<>();
                        if (selectedOption.equals("All")) {
                            selectedProducts = products;
                        } else if (selectedOption.equals("Suggested categories")) {
                            SharedPreferences prefs = requireActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
                            String str = prefs.getString("likedProducts", "");
                            if (!str.equals("")) {
                                HashSet<String> hashSet = new HashSet<>(Arrays.asList(str.split(",")));
                                HashSet<String> categoryHashSet = new HashSet<>();
                                for (Product p : products) {
                                    if (hashSet.contains(Integer.toString(p.getId()))) {
                                        categoryHashSet.add(p.getCategory());
                                    }
                                }
                                for (Product p : products) {
                                    if (categoryHashSet.contains(p.getCategory())) {
                                        selectedProducts.add(p);
                                    }
                                }
                            }
                        } else {
                            for (Product p : products) {
                                if (p.getCategory().equals(selectedOption)) {
                                    selectedProducts.add(p);
                                }
                            }
                        }
                        handleProductsList(selectedProducts);
                    } else {
                        showError(getString(R.string.product_list_empty));
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        return view;
    }

    private void handleProductsList(List<Product> products) {
        ProductAdapter adapter = new ProductAdapter(products);
        adapter.setOnProductClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void cancelToast() {
        if (errorMessage != null) {
            errorMessage.cancel();
        }
    }

    private void showSpinner() {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(requireContext());
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.setTitle("Fetching products");
            loadingDialog.setMessage("Please wait...");
            loadingDialog.setIndeterminate(true);
            loadingDialog.setCanceledOnTouchOutside(false);
        }
        loadingDialog.show();
    }

    private void hideSpinner() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void showError(String message) {
        hideSpinner();

        if (errorMessage != null) {
            errorMessage.cancel();
        }

        errorMessage = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT);
        errorMessage.show();
    }

    @Override
    public void onStop() {
        super.onStop();

        hideSpinner();
        cancelToast();
    }

    private int dpToPx(int dp) {
        Resources resources = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
    }

    @Override
    public void onProductClick(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("product_id", id);
        ProductDetailScreenFragment productDetailScreenFragment = new ProductDetailScreenFragment();
        productDetailScreenFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction().replace(R.id.frame_layout, productDetailScreenFragment).addToBackStack(null).commit();
    }

    @Override
    public void onLikeProductClick(int productId) {
        viewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            for (Product p : products) {
                if (p.getId() == productId) {
                    SharedPreferences prefs = requireActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
                    String str = prefs.getString("likedProducts", "");
                    HashSet<String> hashSet;
                    if (p.isLiked()) {
                        hashSet = new HashSet<>(Arrays.asList(str.split(",")));
                        hashSet.remove(Integer.toString(p.getId()));
                    } else {
                        if (str.equals("")) {
                            hashSet = new HashSet<>();
                            hashSet.add(Integer.toString(p.getId()));
                        } else {
                            hashSet = new HashSet<>(Arrays.asList(str.split(",")));
                            hashSet.add(Integer.toString(p.getId()));
                        }
                    }
                    String hashSetString = TextUtils.join(",", hashSet);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("likedProducts", hashSetString);
                    editor.apply();
                    p.setLiked(!p.isLiked());
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Access the checkoutItem menu item from the activity
        MainActivity activity = (MainActivity) getActivity();
        MenuItem checkoutItem = MainActivity.checkoutItem;

        // Do something with the checkoutItem, for example show it
        if (checkoutItem != null) checkoutItem.setVisible(true);
    }

    private static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {

                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }
}
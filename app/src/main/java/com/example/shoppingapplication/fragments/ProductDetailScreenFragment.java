package com.example.shoppingapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shoppingapplication.MainActivity;
import com.example.shoppingapplication.R;
import com.example.shoppingapplication.models.Product;
import com.example.shoppingapplication.viewmodels.ProductsViewModel;
import com.squareup.picasso.Picasso;

public class ProductDetailScreenFragment extends Fragment {

    private ProductsViewModel viewModel;

    private int currentPosition = 0;
    private int currentId = 0;

    private TextView productTitle;
    private TextView productDescription;
    private TextView productPrice;
    private TextView cartCount;
    private EditText cartQuantity;
    private Button addToCart;
    private Button removeCart;
    private ImageView productImage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_detail_screen, container, false);

        productTitle = view.findViewById(R.id.product_title);
        productDescription = view.findViewById(R.id.product_description);
        productPrice = view.findViewById(R.id.product_price);
        productImage = view.findViewById(R.id.product_image);
        cartCount = view.findViewById(R.id.cart_count);
        cartQuantity = view.findViewById(R.id.quantity_edittext);
        addToCart = view.findViewById(R.id.add_to_cart_button);
        removeCart = view.findViewById(R.id.remove_button);


        currentId = getArguments().getInt("product_id");
        // Initialize the ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(ProductsViewModel.class);

        // Observe the LiveData from the ViewModel
        viewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getId() == currentId) {
                    currentPosition = i;
                    break;
                }
            }
            productTitle.setText(products.get(currentPosition).getTitle());
            productPrice.setText("Rs " + Double.toString(products.get(currentPosition).getPrice()));
            productDescription.setText(products.get(currentPosition).getDescription());
            Picasso.get().load(products.get(currentPosition).getImage()).into(productImage);
            cartCount.setText("Cart "+Integer.toString(products.get(currentPosition).getCartCount()));

            addToCart.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    String cartQuantityString = cartQuantity.getText().toString();
                    if (cartQuantityString.isEmpty()) {
                        Toast.makeText(getContext(), "Enter some items to add in cart", Toast.LENGTH_SHORT).show();
                    } else {
                        int currentCartCount = products.get(currentPosition).getCartCount() + Integer.parseInt(cartQuantityString);
                        if (currentCartCount > products.get(currentPosition).getRating().getCount()) {
                            Toast.makeText(getContext(), "Not enough products available", Toast.LENGTH_SHORT).show();
                        } else {
                            Product pro = products.get(currentPosition);
                            pro.setCartCount(currentCartCount);
                            viewModel.updateProduct(pro);
                            cartCount.setText("Cart "+Integer.toString(currentCartCount));
                        }

                    }

                }
            });
            removeCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (products.get(currentPosition).getCartCount() > 0) {
                        Product pro = products.get(currentPosition);
                        pro.setCartCount(0);
                        viewModel.updateProduct(pro);
                        cartCount.setText("Rs 0");
                    }
                }
            });
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Access the checkoutItem menu item from the activity
        MenuItem checkoutItem = MainActivity.checkoutItem;

        // Do something with the checkoutItem, for example show it
        if (checkoutItem != null) checkoutItem.setVisible(true);
    }
}
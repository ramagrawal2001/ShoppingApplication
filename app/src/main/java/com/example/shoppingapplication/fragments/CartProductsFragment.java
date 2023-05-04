package com.example.shoppingapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapplication.R;
import com.example.shoppingapplication.adapters.CartAdapter;
import com.example.shoppingapplication.models.Product;
import com.example.shoppingapplication.viewmodels.ProductsViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartProductsFragment extends Fragment implements CartAdapter.OnCartItemDeleteClickListener {

    private RecyclerView recyclerView;
    private TextView totalAmount;
    private Button buyButton;
    private CartAdapter cartAdapter;
    private ProductsViewModel viewModel;
    private final List<Product> cartProducts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_products, container, false);
        totalAmount = view.findViewById(R.id.total_amount);
        buyButton = view.findViewById(R.id.buy_button);
        recyclerView = view.findViewById(R.id.cart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartProducts);
        cartAdapter.setOnCartItemDeleteClickListener(this);
        recyclerView.setAdapter(cartAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProductsViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initially load the cart products from the ViewModel
        viewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            updateCartProducts();
        });
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the cart list
                for (Product p : cartProducts) {
                    Product.Rating rating = p.getRating();
                    rating.setCount(p.getRating().getCount() - p.getCartCount());
                    p.setRating(rating);
                    p.setCartCount(0);
                    viewModel.updateProduct(p);
                }
                cartProducts.clear();
                cartAdapter.notifyDataSetChanged();
                totalAmount.setText(R.string.total_amount_0);

            }
        });
    }

    private void updateCartProducts() {
        List<Product> products = viewModel.getProductsLiveData().getValue();
        if (products != null) {
            cartProducts.clear();
            for (Product p : products) {
                if (p.getCartCount() > 0) {
                    cartProducts.add(p);
                }
            }
            cartAdapter.notifyDataSetChanged();
        }
        double total = 0.0;
        for (Product p : cartProducts) {
            total += p.getPrice() * p.getCartCount();
        }
        totalAmount.setText(String.format(getString(R.string.total_product_in_float), total));
    }

    @Override
    public void onDeleteCartClick(int productId) {
        for (int i = 0; i < cartProducts.size(); i++) {
            if (cartProducts.get(i).getId() == productId) {
                Product p = cartProducts.get(i);
                cartProducts.remove(i);
                p.setCartCount(0);
                viewModel.updateProduct(p);
                cartAdapter.notifyItemRemoved(i);
                double newTotal = 0;
                for (Product pro : cartProducts) {
                    newTotal += pro.getPrice() * pro.getCartCount();
                }
                totalAmount.setText(String.format("%.2f", newTotal));
                break;
            }
        }
    }
}

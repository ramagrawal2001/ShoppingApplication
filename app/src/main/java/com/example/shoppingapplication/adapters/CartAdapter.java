package com.example.shoppingapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapplication.R;
import com.example.shoppingapplication.models.Product;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private static List<Product> products;
    private OnCartItemDeleteClickListener onCartItemDeleteClickListener;

    public CartAdapter(List<Product> products) {
        CartAdapter.products = products;
    }

    public void setOnCartItemDeleteClickListener(OnCartItemDeleteClickListener onCartItemDeleteClickListener) {
        this.onCartItemDeleteClickListener = onCartItemDeleteClickListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartAdapter.CartViewHolder(view, onCartItemDeleteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface OnCartItemDeleteClickListener {
        void onDeleteCartClick(int productId);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleView;
        private final TextView priceView;
        private final TextView cartCount;
        private final ImageButton deleteButton;

        public CartViewHolder(@NonNull View itemView, OnCartItemDeleteClickListener onCartItemDeleteClickListener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.cart_item_title);
            priceView = itemView.findViewById(R.id.cart_item_price);
            cartCount = itemView.findViewById(R.id.cart_item_quantity);
            deleteButton = itemView.findViewById(R.id.cart_item_delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCartItemDeleteClickListener != null) {
                        onCartItemDeleteClickListener.onDeleteCartClick(products.get(getAdapterPosition()).getId());
                    }
                }
            });
        }

        public void bind(Product product) {
            titleView.setText(product.getTitle());
            priceView.setText(Double.toString(product.getPrice()));
            cartCount.setText(Integer.toString(product.getCartCount()));
        }
    }
}

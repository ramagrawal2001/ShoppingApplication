package com.example.shoppingapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapplication.R;
import com.example.shoppingapplication.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private static List<Product> products;

    private OnProductClickListener onProductClickListener;

    public ProductAdapter(List<Product> products) {
        ProductAdapter.products = products;
    }

    public void setOnProductClickListener(OnProductClickListener onProductClickListener) {
        this.onProductClickListener = onProductClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_grid_item, parent, false);
        return new ProductViewHolder(view, onProductClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface OnProductClickListener {
        void onProductClick(int productId);

        void onLikeProductClick(int productId);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleView;
        private final TextView priceView;
        private final ImageView imageView;
        private final TextView cartCount;
        private final ImageButton likeButton;

        public ProductViewHolder(@NonNull View itemView, OnProductClickListener onProductClickListener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.item_title);
            priceView = itemView.findViewById(R.id.item_price);
            imageView = itemView.findViewById(R.id.item_image);
            cartCount = itemView.findViewById(R.id.cart_count);
            likeButton = itemView.findViewById(R.id.like_button);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onProductClickListener != null) {
                        onProductClickListener.onProductClick(products.get(getAdapterPosition()).getId());
                    }
                }
            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onProductClickListener != null) {
                        onProductClickListener.onLikeProductClick(products.get(getAdapterPosition()).getId());
                    }
                }
            });

        }


        public void bind(Product product) {
            titleView.setText(product.getTitle());
            priceView.setText(""+Double.toString(product.getPrice()));
            Picasso.get().load(product.getImage()).into(imageView);
            cartCount.setText(Integer.toString(product.getCartCount()));
            if (product.isLiked()) {
                likeButton.setImageResource(R.drawable.heart_filled);
            } else {
                likeButton.setImageResource(R.drawable.heart_outlined);
            }
        }
    }
}

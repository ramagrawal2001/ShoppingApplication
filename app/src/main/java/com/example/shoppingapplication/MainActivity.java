package com.example.shoppingapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppingapplication.fragments.CartProductsFragment;
import com.example.shoppingapplication.fragments.ProductGridScreenFragment;

public class MainActivity extends AppCompatActivity {
    public static MenuItem checkoutItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProductGridScreenFragment productGridScreenFragment = new ProductGridScreenFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, productGridScreenFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        checkoutItem = menu.findItem(R.id.checkout); // Store a reference to the menu item
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.checkout) {
            CartProductsFragment cartProductsFragment = new CartProductsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, cartProductsFragment).addToBackStack(null).commit();
            // Hide the menu button
            item.setVisible(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
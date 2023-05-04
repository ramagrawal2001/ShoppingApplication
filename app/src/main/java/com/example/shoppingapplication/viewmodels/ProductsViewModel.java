package com.example.shoppingapplication.viewmodels;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.shoppingapplication.database.ProductDatabaseHelper;
import com.example.shoppingapplication.models.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ProductsViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
    private final MutableLiveData<RequestStatus> requestStatusLiveData = new MutableLiveData<>();

    private final RequestQueue queue;

    public ProductsViewModel(@NonNull Application application) {
        super(application);
        queue = Volley.newRequestQueue(application);
        requestStatusLiveData.postValue(RequestStatus.IN_PROGRESS);
        loadData();
    }

    public LiveData<List<Product>> getProductsLiveData() {
        return productsLiveData;
    }

    public LiveData<RequestStatus> getRequestStatusLiveData() {
        return requestStatusLiveData;
    }


    private void loadData() {
        String url = "https://fakestoreapi.com/products";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    List<Product> products = new ArrayList<>();
                    String dbPath = getApplication().getDatabasePath("products.db").getPath();
                    File dbFile = new File(dbPath);
                    ProductDatabaseHelper productDatabaseHelper = new ProductDatabaseHelper(getApplication().getApplicationContext());
                    if (dbFile.exists()) {
                        products = productDatabaseHelper.getAllProducts();
                        for (int i = 0; i < products.size(); i++) {
                            if (products.get(i).getRating().getCount() <= 0) {
                                JSONObject productObject = response.getJSONObject(i);
                                Product.Rating r = products.get(i).getRating();
                                r.setCount(productObject.getJSONObject("rating").getInt("count"));
                                products.get(i).setRating(r);
                                productDatabaseHelper.updateProduct(products.get(i));
                            }
                        }
                    } else {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject productObject = response.getJSONObject(i);
                            Product product = new Product();
                            product.setId(productObject.getInt("id"));
                            product.setTitle(productObject.getString("title"));
                            product.setPrice(productObject.getDouble("price"));
                            product.setDescription(productObject.getString("description"));
                            product.setCategory(productObject.getString("category"));
                            product.setImage(productObject.getString("image"));
                            JSONObject ratingObject = productObject.getJSONObject("rating");
                            Product.Rating rating = new Product.Rating();
                            rating.setRate(ratingObject.getDouble("rate"));
                            rating.setCount(ratingObject.getInt("count"));
                            product.setRating(rating);
                            products.add(product);
                            productDatabaseHelper.insertProduct(product);
                        }

                    }
                    SharedPreferences prefs = getApplication().getSharedPreferences("myPrefs", MODE_PRIVATE);
                    String str = prefs.getString("likedProducts", "");
                    if (!str.equals("")) {
                        HashSet<String> hashSet = new HashSet<>(Arrays.asList(str.split(",")));
                        for (Product p : products) {
                            if (hashSet.contains(Integer.toString(p.getId()))) {
                                p.setLiked(true);
                            }
                        }
                    }
                    productsLiveData.postValue(products);
                    requestStatusLiveData.postValue(RequestStatus.SUCCEEDED);
                } catch (JSONException e) {
                    e.printStackTrace();
                    requestStatusLiveData.postValue(RequestStatus.FAILED);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                requestStatusLiveData.postValue(RequestStatus.FAILED);
            }
        });

        queue.add(jsonArrayRequest);
    }

    public void updateProduct(Product product) {
        List<Product> productList = productsLiveData.getValue();
        if (productList == null) {
            return;
        }

        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId() == product.getId()) {
                productList.set(i, product);
                productsLiveData.postValue(productList);
                break;
            }
        }

        // Update the product in the database
        ProductDatabaseHelper productDatabaseHelper = new ProductDatabaseHelper(getApplication().getApplicationContext());
        productDatabaseHelper.updateProduct(product);
    }

    public enum RequestStatus {
        IN_PROGRESS, FAILED, SUCCEEDED
    }
}

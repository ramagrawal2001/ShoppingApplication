package com.example.shoppingapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.shoppingapplication.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_RATING_COUNT = "rating_count";
    private static final String COLUMN_CART_COUNT = "cart_count";
    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_PRICE + " REAL," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_CATEGORY + " TEXT," +
                COLUMN_IMAGE + " TEXT," +
                COLUMN_RATING + " REAL," +
                COLUMN_RATING_COUNT + " INTEGER," +
                COLUMN_CART_COUNT + " INTEGER" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void updateProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, product.getTitle());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_CATEGORY, product.getCategory());
        values.put(COLUMN_IMAGE, product.getImage());
        values.put(COLUMN_CART_COUNT, product.getCartCount());
        values.put(COLUMN_RATING, product.getRating().getRate());
        values.put(COLUMN_RATING_COUNT, product.getRating().getCount());

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(product.getId())};

        db.update(TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    public void insertProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, product.getId());
        values.put(COLUMN_TITLE, product.getTitle());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_CATEGORY, product.getCategory());
        values.put(COLUMN_IMAGE, product.getImage());
        values.put(COLUMN_CART_COUNT, product.getCartCount());
        values.put(COLUMN_RATING, product.getRating().getRate());
        values.put(COLUMN_RATING_COUNT, product.getRating().getCount());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

        public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {COLUMN_ID, COLUMN_TITLE, COLUMN_PRICE, COLUMN_DESCRIPTION, COLUMN_CATEGORY, COLUMN_IMAGE, COLUMN_RATING, COLUMN_RATING_COUNT, COLUMN_CART_COUNT};

        Cursor cursor = db.query("products", columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(Math.max(cursor.getColumnIndex(COLUMN_ID), 0));
            String title = cursor.getString(Math.max(cursor.getColumnIndex(COLUMN_TITLE), 0));
            double price = cursor.getDouble(Math.max(cursor.getColumnIndex(COLUMN_PRICE), 0));
            String description = cursor.getString(Math.max(cursor.getColumnIndex(COLUMN_DESCRIPTION), 0));
            String category = cursor.getString(Math.max(cursor.getColumnIndex(COLUMN_CATEGORY), 0));
            String image = cursor.getString(Math.max(cursor.getColumnIndex(COLUMN_IMAGE), 0));
            double rating = cursor.getDouble(Math.max(cursor.getColumnIndex(COLUMN_RATING), 0));
            int ratingCount = cursor.getInt(Math.max(cursor.getColumnIndex(COLUMN_RATING_COUNT), 0));
            int cartCount = cursor.getInt(Math.max(cursor.getColumnIndex(COLUMN_CART_COUNT), 0));

            Product product = new Product();
            product.setId(id);
            product.setTitle(title);
            product.setPrice(price);
            product.setDescription(description);
            product.setCategory(category);
            product.setImage(image);
            Product.Rating productRating = new Product.Rating();
            productRating.setRate(rating);
            productRating.setCount(ratingCount);
            product.setRating(productRating);
            product.setCartCount(cartCount);

            products.add(product);
        }

        cursor.close();
        db.close();

        return products;
    }
}

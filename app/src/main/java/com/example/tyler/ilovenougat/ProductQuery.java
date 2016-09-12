package com.example.tyler.ilovenougat;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.JsonReader;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler Collison on 9/9/2016.
 */
public class ProductQuery extends Activity {

    // store the last started product display thread
    private AsyncDisplayProductsQuery runningTask = new AsyncDisplayProductsQuery();

    /**
     * Gets and displays the products from @url using @adapter
     *
     * @param url points to Zappos/6pm standard JSON document for product query
     * @param adapter product recycler view adapter
     *
     * @requires @url points to a valid Zappos/6pm products query JSON and adapter is not null
     *
     * @ensures products from @url are stored in @adapter
     */
    public void displayProductsQuery (String url, ProductViewAdapter adapter) {
        // stop any running thread
        runningTask.stop();
        // async query and display the products at url
        AsyncDisplayProductsQuery query = new AsyncDisplayProductsQuery();
        Object[] parameters = {url, adapter};
        query.execute(parameters);
        // store the new thread
        runningTask = query;
    }

    /**
     * Gets and displays the cheapest corresponding product for @product if one exists, otherwise
     *             displays a default message
     *
     * @param url points to Zappos/6pm standard JSON document for product query
     * @param product the product to find a cheaper match for
     * @param view used to find a suitable parent view for the snackbar
     *
     * @requires @url points to a valid Zappos/6pm products query JSON, @product is not null, and @view is not null
     *
     * @ensures the snackbar either displays the cheapest product at @url corresponding to @product or a default message
     */
    public void displayCheapestMatch(String url, Product product, View view) {
        // async get and display the corresponding product in the snackbar
        AsyncGetCheapestProductQuery query = new AsyncGetCheapestProductQuery();
        Object[] parameters = {url, product, view};
        query.execute(parameters);
    }

    private class AsyncDisplayProductsQuery extends AsyncTask<Object, Void, Void> {

        // flag for determining whether the thread should be halted
        private volatile boolean stop = false;

        /**
         * Async get and display all products from the url using the adapter
         *
         * @param objects takes parameters String url and ProductViewAdapter adapter in order
         * @return always null
         *
         * @requires url points to a valid Zappos/6pm products query JSON and adapter is not null
         *
         * @ensures products from url are stored in @dapter
         */
        @Override
        protected Void doInBackground(Object...objects) {
            final ProductViewAdapter adapter = (ProductViewAdapter) objects[1];

            // halt the thread if the stop flag is true
            if (stop) {return null;}

            // get the products from the url
            final List<Product> productList = productsQuery(objects[0].toString());

            // halt the thread if the stop flag is true
            if (stop) {return null;}

            // update the adapter on the UI thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.assignProductList(productList);
                    adapter.notifyDataSetChanged();
                }
            });
            return null;
        }

        public void stop () {
            stop = true;
        }
    }

    private class AsyncGetCheapestProductQuery extends AsyncTask <Object, Void, Void> {

        /**
         * Async finds the cheapest product at url corresponding to compareProduct and displays it
         * if cheaper than compareProduct
         *
         * @param objects takes parameters String url, Product compareProduct, and View view in order
         * @return always null
         *
         * @requires url points to a valid Zappos/6pm products query JSON, compareProduct is not null, and view is not null
         *
         * @ensures the cheapest product corresponding to compareProduct at url is displayed in the snackbar if it exists, otherwise displays a default message
         */
        @Override
        protected Void doInBackground(Object...objects) {
            List<Product> products = productsQuery(objects[0].toString());
            Product compareProduct = (Product) objects[1];
            View view = (View)objects[2];
            // get the cheapest matching product
            Product cheapestMatch = findCheapestMatch(products, compareProduct);
            // Determine whether the cheapest match is an empty product
            if (!cheapestMatch.isEmpty()) {
                Snackbar.make(view, "Available from " + cheapestMatch.getAttribute("price") +
                    " at 6pm.com!", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(view, "Best price on Zappos.com!", Snackbar.LENGTH_LONG).show();
            }
            return null;
        }
    }

    /**
     * Gets the cheapest product cheaper than @productForComparison in @products.
     *
     * @param products list of Products
     * @param productForComparison product to compare other product prices to
     * @return if a cheaper product is found, findCheapestMatch is the cheapest match. Otherwise, isCheaperProduct is an empty product.
     *
     * @requires @products is not null and @productForComparison has attribute price
     *
     * @ensures findCheapestMatch.price < @productForComparison.price and findCheapestMatch is in @products or findCheapestMatch is the empty product.
     */
    private Product findCheapestMatch (List<Product> products, Product productForComparison) {
        Product result = new Product();
        float price = Float.parseFloat(productForComparison.getAttribute("price")
                .replace("$", ""));
        // Compare products until a match is found
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (productForComparison.isSameProduct(p)) {
                float newPrice = Float.parseFloat(p.getAttribute("price").replace("$", ""));
                if (newPrice < price) {
                    result = p;
                    price = newPrice;
                }
            }
        }
        return result;
    }

    /**
     * Reads the Zappos/6pm JSON result from @jsonURL and returns a list of corresponding products.
     *
     * @param jsonURL points to a valid Zappos/6pm products query JSON
     * @return the list of products corresponding to the result of the jsonURL query
     *
     * @requires @jsonURL points to a valid Zappos/6pm products query JSON
     *
     * @ensures productsQuery is a list of products corresponding to the result of @jsonURL query.
     */
    private List<Product> productsQuery(String jsonURL) {
        List<Product> result = new ArrayList<>();
        try {
            // Connect to the JSON document
            URL url = new URL(jsonURL);
            URLConnection conn = url.openConnection();
            conn.connect();
            // Begin reading the JSON document
            InputStream urlInputStream = conn.getInputStream();
            InputStreamReader urlInputReader = new InputStreamReader(urlInputStream);
            JsonReader jsonReader = new JsonReader(urlInputReader);
            jsonReader.beginObject();
            // Skip everything before the results
            while (!jsonReader.nextName().equals("results")) {
                jsonReader.skipValue();
            }
            // Read and display the results
            result = readProductArray(jsonReader);
            // Skip everything to the end of the JSON object
            while (jsonReader.hasNext()) {
                jsonReader.skipValue();
            }
            jsonReader.endObject();
            // Close the JSON reader
            jsonReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Reads every object in a JSON array
     *
     * @param reader the JSON input reader
     * @return a list of products corresponding to the products in the JSON array
     *
     * @requires @reader is at a JSON array and the array represents the results of a Zappos REST request
     *
     * @ensures readProductArray is a list containing every product in the JSON array
     */
    private List<Product> readProductArray(JsonReader reader) {
        List<Product> productArray = new ArrayList<>();
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                // read the JSON object and add it to the product array
                productArray.add(readProduct(reader));
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return productArray;
    }

    /**
     * Gets a JSON object as a product
     *
     * @param reader the JSON input reader
     * @return the JSON object at the reader as a product
     *
     * @requires @reader is at a JSON object and the JSON object represents a Zappos product
     *
     * @ensures readProduct is a product corresponding to the JSON object at @reader
     */
    private Product readProduct(JsonReader reader) {
        Product product = new Product();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                // add the JSON string as an attribute of the product
                String name = reader.nextName();
                String value = reader.nextString();
                product.addAttribute(name, value);
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return product;
    }

}

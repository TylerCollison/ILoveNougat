package com.example.tyler.ilovenougat;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    // Store the information to access the Zappos API
    private final String ZAPPOS_ENDPOINT = "https://api.zappos.com/Search?term=%3C";
    private final String ZAPPOS_KEY = "%3E&key=b743e26728e16b81da139182bb2094357c31d331";

    // UI elements
    private TextView searchInput;

    // the product recycler view and its adapter
    static private ProductViewAdapter productViewAdapter = new ProductViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect to the UI elements
        RecyclerView productView = (RecyclerView)findViewById(R.id.productView);
        searchInput = (TextView)findViewById(R.id.searchInput);
        Button searchButton = (Button)findViewById(R.id.searchButton);

        // setup the product view layout manager
        RecyclerView.LayoutManager productViewLayoutManager = new LinearLayoutManager(this);
        productView.setLayoutManager(productViewLayoutManager);

        // setup the product view adapter
        productViewAdapter.registerActivity(this);
        productView.setAdapter(productViewAdapter);

        // setup product query
        final ProductQuery query = new ProductQuery();
        query.registerActivity(this);

        // query and assign products to the product view on editor finish button
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                query.displayProductsQuery(ZAPPOS_ENDPOINT + textView.getText().toString() +
                        ZAPPOS_KEY, productViewAdapter);
                return false;
            }
        });

        // query and assign products to the product view on search button click
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query.displayProductsQuery(ZAPPOS_ENDPOINT + searchInput.getText().toString() +
                        ZAPPOS_KEY, productViewAdapter);
            }
        });
    }
}

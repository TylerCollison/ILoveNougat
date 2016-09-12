package com.example.tyler.ilovenougat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class ProductView extends AppCompatActivity {

    // the url for the product being displayed
    private String productURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        // get the product url from the intent data
        productURL = getIntent().getDataString();
        // connect to the UI
        Button emailShare = (Button)findViewById(R.id.shareEmail);
        Button smsShare = (Button)findViewById(R.id.shareSMS);
        WebView webView = (WebView)findViewById(R.id.webView);
        // setup the web view
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http:/m." + productURL.substring(11));
        // set share button onClick listeners
        emailShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailShare();
            }
        });
        smsShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsShare();
            }
        });

    }

    /**
     * Opens an email application with the productURL in its content.
     */
    private void emailShare () {
        // setup the email intent with subject line and productURL as link
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Check this out on Zappos!");
        emailIntent.putExtra(Intent.EXTRA_TEXT   , productURL);
        // start the email activity
        startActivity(Intent.createChooser(emailIntent, "Send Mail"));
    }

    /**
     * Opens an SMS application with the productURL in its content.
     */
    private void smsShare () {
        // setup the SMS intent with the productURL in its content
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("sms:"));
        smsIntent.putExtra("sms_body", productURL);
        // start the SMS activity
        startActivity(smsIntent);
    }
}

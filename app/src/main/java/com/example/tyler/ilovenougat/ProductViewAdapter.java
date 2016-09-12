package com.example.tyler.ilovenougat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler Collison on 9/7/2016.
 */
public class ProductViewAdapter extends RecyclerView.Adapter<ProductViewAdapter.ViewHolder> {

    // Store the information to access the 6pm API
    private final String SIX_PM_ENDPOINT = "https://api.6pm.com/Search?term=%3C";
    private final String SIX_PM_KEY = "%3E&key=524f01b7e2906210f7bb61dcbe1bfea26eb722eb";

    // Store the context of the activity managing this adapter
    private Context activityContext;
    // Store the image streamer
    private ImageStream streamer = new ImageStream();
    // model for ProductViewAdapter
    private List<Product> products = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView brandname;
        ImageView productImage;
        TextView originalPrice;
        TextView price;
        TextView productName;
        Button viewButton;
        Button compareButton;

        public ViewHolder(final View itemView) {
            super(itemView);

            // Attach the ViewHolder to the UI
            brandname = (TextView) itemView.findViewById(R.id.brandname);
            productImage = (ImageView) itemView.findViewById(R.id.productImage);
            originalPrice = (TextView) itemView.findViewById(R.id.originalPrice);
            price = (TextView) itemView.findViewById(R.id.price);
            productName = (TextView) itemView.findViewById(R.id.productName);
            viewButton = (Button) itemView.findViewById(R.id.viewButton);
            compareButton = (Button) itemView.findViewById(R.id.compareButton);
            // set the viewButton onClick listener to open the product view
            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openProductView(products.get(getAdapterPosition()).getAttribute("productUrl"));
                }
            });
            // set the itemView onClick listener to display the cheaper 6pm product
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProductQuery query = new ProductQuery();
                    query.displayCheapestMatch(SIX_PM_ENDPOINT +
                            products.get(getAdapterPosition()).getAttribute("productName") +
                            SIX_PM_KEY, products.get(getAdapterPosition()), view);
                }
            });
            compareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductQuery query = new ProductQuery();
                    query.displayCheapestMatch(SIX_PM_ENDPOINT +
                            products.get(getAdapterPosition()).getAttribute("productName") +
                            SIX_PM_KEY, products.get(getAdapterPosition()), v);
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // set the product card as the layout for recycler view items
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Bind the product data to the ViewHolder
        holder.brandname.setText(products.get(position).getAttribute("brandName"));
        holder.originalPrice.setText(products.get(position).getAttribute("originalPrice"));
        holder.price.setText(products.get(position).getAttribute("price"));
        holder.productName.setText(products.get(position).getAttribute("productName"));

        // Determine whether the original and current prices are the same
        if (holder.price.getText().toString().equals(holder.originalPrice.getText().toString())) {
            // if the same, hide the current price
            holder.price.setVisibility(View.GONE);
        } else {
            // otherwise, show both prices
            holder.price.setVisibility(View.VISIBLE);
        }

        // Bind the product thumbnail URL to the ViewHolder ImageView
        String url = products.get(position).getAttribute("thumbnailImageUrl");
        streamer.streamToImageView(url, holder.productImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    /**
     * @param newProducts list of products to be added to this adapter
     *
     * Adds the given list of new products to this adapter.
     *
     * @requires newProducts is not null
     *
     * @ensures this adapter.model contains @newProducts
     */
    public void assignProductList (List<Product> newProducts) {
        products.clear();
        streamer.cancelAll();
        products.addAll(newProducts);
    }

    /**
     * @param activity the activity that manages this adapter
     *
     * Registers the @activity with this adapter
     *
     * @requires activity is not null
     *
     * @ensures @activity is registered with this adapter
     */
    public void registerActivity (Context activity) {
        activityContext = activity;
    }

    /**
     * @param productURL the url of the product to be displayed in the product view
     *
     * Opens the product view activity for the product corresponding to @productURL
     */
    private void openProductView (String productURL) {
        Intent productViewIntent = new Intent(activityContext, ProductView.class);
        // store the productURL as intent data
        Uri data = Uri.parse(productURL);
        productViewIntent.setData(data);
        // open the product view activity
        activityContext.startActivity(productViewIntent);
    }

}

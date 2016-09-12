package com.example.tyler.ilovenougat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tyler Collison on 9/9/2016.
 */
public class Product {
    // product model
    private Map<String, String> data = new HashMap<>();

    /**
     * Adds an attribute to this product
     *
     * @param name the attribute name
     * @param value the attribute value
     *
     * @requires @name and @value are not null
     *
     * @ensures attribute of name @name and value @value is added to this product
     */
    public void addAttribute (String name, String value) {
        data.put(name, value);
    }

    /**
     * Gets the attribute corresponding to @name
     *
     * @param name the attribute name
     * @return the value of @name or null if the product does not have attribute @name
     *
     * @requires @name is not null
     *
     * @ensures getAttribute is either null or the value of the attribute of name @name
     */
    public String getAttribute (String name) {
        return data.get(name);
    }

    /**
     * Compares this product to @otherProduct
     *
     * @param otherProduct the product to compare this to
     * @return whether this is the same product as @otherProduct
     *
     * @requires otherProduct is not null and has attribute productID
     * @ensures isSameProduct is true if the productIDs of this and @otherProduct match, otherwise
     *          false
     */
    public boolean isSameProduct (Product otherProduct) {
        return this.getAttribute("productId").equals(otherProduct.getAttribute("productId"));
    }

    /**
     * @return true if this product has no attributes, false otherwise
     */
    public boolean isEmpty () {
        return data.isEmpty();
    }
}

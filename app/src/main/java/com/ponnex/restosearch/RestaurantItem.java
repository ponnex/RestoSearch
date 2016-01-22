package com.ponnex.restosearch;

import android.content.Context;

/**
 * Created by ponne on 1/23/2016.
 */
public class RestaurantItem {

    private String mName;
    private String mImage;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }
}

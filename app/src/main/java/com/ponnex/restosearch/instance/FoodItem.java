package com.ponnex.restosearch.instance;

/**
 * Created by ponne on 1/23/2016.
 */
public class FoodItem {

    private String mName;
    private String mImage;
    private String mDesc;
    private String mPrice;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        this.mDesc = desc;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        this.mPrice = price;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }
}
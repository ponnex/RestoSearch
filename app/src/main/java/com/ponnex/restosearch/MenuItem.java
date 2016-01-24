package com.ponnex.restosearch;

/**
 * Created by ponne on 1/23/2016.
 */
public class MenuItem {

    private String mName;
    private String mImage;
    private String mDesc;

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

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }
}

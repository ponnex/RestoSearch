package com.ponnex.restosearch.instance;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by ponne on 1/23/2016.
 */
public class RestaurantItem {

    private String mName;
    private String mImage;
    private String mDesc;
    private String mAddress;
    private String mId;
    private ParseGeoPoint mPoint;
    private ParseObject mAdmin;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

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

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String add) {
        this.mAddress = add;
    }

    public ParseGeoPoint getCoord() {
        return mPoint;
    }

    public ParseGeoPoint setCoord(ParseGeoPoint point) {
        return this.mPoint = point;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

}

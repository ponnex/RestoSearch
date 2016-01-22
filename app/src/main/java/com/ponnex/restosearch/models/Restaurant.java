package com.ponnex.restosearch.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by ponnex on 1/19/2016.
 */
@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {

    public Restaurant() {}

    public String getRestoName() {
        return  getString("resName");
    }

    public void setRestoName(String name) {
        put("resName", name);
    }

    public String getDescription() {
        return  getString("resDescription");
    }

    public void setDescription(String description) {
        put("resDescription", description);
    }

    public String getAddress() {
        return getString("resAddress");
    }

    public void setAddress(String address) {
        put("resAddress", address);
    }

    public ParseGeoPoint getCoordinates() {
        return getParseGeoPoint("resCoordinates");
    }

    public void setCoordinates(ParseGeoPoint point){
        put("resCoordinates", point);
    }

    public String getImage() {
        return getParseFile("resImage").getUrl();
    }

    public void setImage(ParseFile image){
        put("resImage", image);
    }

}

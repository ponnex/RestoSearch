package com.ponnex.restosearch.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by ponnex on 1/19/2016.
 */
@ParseClassName("Food")
public class Food extends ParseObject {

    public Food() {}

    public String getFoodName() {
        return getString("resName");
    }

    public void setFoodName(String foodName) {
        put("resName", foodName);
        put("resName_search", foodName.toLowerCase().replaceAll("/[^a-zA-Z ]/g",""));
    }

    public String getFoodDescription() {
        return getString("resDescription");
    }

    public void setFoodDescription(String foodDescription) {
        put("resDescription", foodDescription);
    }

    public String getFoodPrice() {
        return getString("resFoodPrice");
    }

    public void setFoodPrice(String foodPrice) {
        put("resPrice", foodPrice);
    }

    public String getFoodImageUrl() {
        return getParseFile("resImage").getUrl();
    }

    public void setFoodImageUrl(ParseFile foodImage) {
        put("resImage", foodImage);
    }
}

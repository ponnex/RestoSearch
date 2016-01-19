package com.ponnex.restosearch.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by ponnex on 1/19/2016.
 */
@ParseClassName("Menu")
public class Menu extends ParseObject {

    public Menu() {}

    public String getFoodName() {
        return getString("resFoodName");
    }

    public void setFoodName(String foodName) {
        put("resFoodName", foodName);
    }

    public String getFoodDescription() {
        return getString("resFoodDescription");
    }

    public void setFoodDescription(String foodDescription) {
        put("resFoodDescription", foodDescription);
    }

    public String getFoodPrice() {
        return getString("resFoodPrice");
    }

    public void setFoodPrice(String foodPrice) {
        put("resFoodPrice", foodPrice);
    }

    public ParseFile getFoodImage() {
        return getParseFile("resFoodImage");
    }

    public void setFoodImage(String foodImage) {
        put("resFoodImage", foodImage);
    }
}

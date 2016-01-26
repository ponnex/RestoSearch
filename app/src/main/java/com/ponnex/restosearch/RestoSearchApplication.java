package com.ponnex.restosearch;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.ponnex.restosearch.models.Food;
import com.ponnex.restosearch.models.Restaurant;

/**
 * Created by ponnex on 1/8/2016.
 */
public class RestoSearchApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // Register Subclass
        ParseObject.registerSubclass(Food.class);
        ParseObject.registerSubclass(Restaurant.class);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);

        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}

package com.ponnex.restosearch.api;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.ponnex.restosearch.events.RestaurantRefreshedEvent;
import com.ponnex.restosearch.models.Menu;
import com.ponnex.restosearch.models.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by ponnex on 1/19/2016.
 */
public class DataManager {

    private List<Restaurant> allRestaurant = new ArrayList<Restaurant>();
    static DataManager singletonInstance;
    Handler handler = new Handler();

    ParseQuery<Menu> inflightQuery;
    AsyncHttpClient client = new AsyncHttpClient();
    public static final int BID_FETCH_INTERVAL = 3000;
    public static final int RETRY_INTERVAL = 60000;
    public static final String QUERY_ALL = "ALL";
    Context context;

    public DataManager() {}

    public static DataManager getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new DataManager();
        }
        return singletonInstance;
    }

    public void refreshBidsNow(final Runnable after) {
        Log.i("TEST", "Refreshing bids...");
        EventBus.getDefault().post(new RestaurantRefreshedEvent());

        if (after != null)
            after.run();
    }

    public void fetchAllRestaurant() {
        fetchAllRestaurant(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new RestaurantRefreshedEvent());
            }
        });
    }


    public void fetchAllRestaurant(final Runnable after) {
        Log.i("TEST", "Fetching all resturants.");
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.addAscendingOrder("resName");
        query.findInBackground(new FindCallback<Restaurant>() {
            @Override
            public void done(List<Restaurant> objects, ParseException e) {
                Log.i("TEST", "Restaurant Fetched");
                if (objects == null) {
                    //Toast or snackbar here saying that there is some error at the moment
                } else {
                    allRestaurant.clear();
                    allRestaurant.addAll(objects);

                    after.run();
                }
            }
        });
    }

    public List<Restaurant> getItemsMatchingQuery(String query, Activity context) {
        if (query.equals(QUERY_ALL)) {
            return allRestaurant;
        }
        else {
            ArrayList<String> queryWords = new ArrayList<String>();
            queryWords.addAll(Arrays.asList(query.split(" ")));

            ArrayList<Restaurant> results = new ArrayList<Restaurant>();
            for (Restaurant item : allRestaurant) {
                for (String word : queryWords) {
                    if (word.length() > 1 &&
                            (item.getRestoName().toLowerCase().contains(word.toLowerCase()) || item.getRestoName().toLowerCase().contains(word.toLowerCase()) ||
                                    item.getDescription().toLowerCase().contains(word.toLowerCase())))
                        results.add(item);
                }
            }

            return results;
        }
    }

    public Restaurant getItemForId(String id) {
        for (Restaurant item : allRestaurant) {
            if (item.getObjectId().equals(id))
                return item;
        }

        return null;
    }

    public void beginRestaurantCoverage(Context context) {
        Log.i("TEST", "Beginning coverage...");
        this.context = context;

        if (allRestaurant.size() > 0)
            refreshRestaurant.run();
        else
            fetchAllRestaurant(refreshRestaurant);
    }

    private Runnable refreshRestaurant = new Runnable() {
        @Override
        public void run() {

            // Post an emergency retry runnable
            handler.removeCallbacks(retryRefreshBids);
            handler.postDelayed(retryRefreshBids, RETRY_INTERVAL);

            // Don't bother if we don't have the items yet
            if (allRestaurant.size() == 0) {
                handler.removeCallbacks(refreshRestaurant);
                handler.postDelayed(refreshRestaurant, BID_FETCH_INTERVAL);
                return;
            }

            fetchAllRestaurant();
        }
    };

    private Runnable retryRefreshBids = new Runnable() {
        @Override
        public void run() {
            Log.i("TEST", "Retrying...");

            if (inflightQuery != null) {
                Log.i("TEST", "Cancelling inflight query.");
                inflightQuery.cancel();
                inflightQuery = null;
            }

            handler.removeCallbacks(refreshRestaurant);
            handler.removeCallbacks(retryRefreshBids);
            refreshRestaurant.run();
        }
    };

    public void endRestaurantCoverage() {
        Log.i("TEST", "Ending coverage...");
        handler.removeCallbacks(refreshRestaurant);
    }

    public static abstract class restaurantCallback {
        public abstract void restaurantResult(boolean placed);
    }

}

package com.ponnex.restosearch.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.multidex.MultiDex;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.ponnex.restosearch.R;
import com.ponnex.restosearch.ui.adapter.RestaurantAdapter;
import com.ponnex.restosearch.instance.RestaurantItem;
import com.ponnex.restosearch.models.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AppBarLayout.OnOffsetChangedListener {

    private RestaurantAdapter mAdapter;
    private TextView emptyStateTextView;
    private AppBarLayout appBarLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<RestaurantItem> mRestaurant = new ArrayList<>();
    private boolean ascending = true;
    private Menu menu;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appBarLayout = (AppBarLayout)findViewById(R.id.appbar_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ascending) {
                    sortAscending();
                } else {
                    sortDescending();
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.restoList);
        mRecyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        emptyStateTextView = (TextView)findViewById(R.id.empty_state);

        mAdapter = new RestaurantAdapter(mRestaurant, R.layout.activity_resto_card, this);
        mRecyclerView.setAdapter(mAdapter);

        if(savedInstanceState != null ) {
            if (savedInstanceState.getBoolean("sortDrawable")){
                ascending = true;
                sortAscending();
            } else {
                ascending = false;
                sortDescending();
            }
        } else {
            updateData();
        }
    }

    public void updateData(){
        clearData();
        removeEmptyState();
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.findInBackground(new FindCallback<Restaurant>() {

            @Override
            public void done(List<Restaurant> restaurants, ParseException error) {
                if(error == null) {
                    if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                        emptyStateTextView.setVisibility(View.GONE);
                    }
                    if (restaurants != null) {
                        for (Restaurant restaurant : restaurants) {
                            RestaurantItem currentRestaurant = new RestaurantItem();
                            currentRestaurant.setId(restaurant.getObjectId());
                            currentRestaurant.setName(restaurant.getRestoName());
                            currentRestaurant.setDesc(restaurant.getDescription());
                            currentRestaurant.setAddress(restaurant.getAddress());
                            currentRestaurant.setImage(restaurant.getRestoImageUrl());
                            currentRestaurant.setCoord(restaurant.getCoordinates());
                            mRestaurant.add(currentRestaurant);
                        }
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    emptyState();
                }
            }
        });
    }

    public void sortAscending(){
        clearData();
        removeEmptyState();
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.orderByAscending("resName");
        query.findInBackground(new FindCallback<Restaurant>() {

            @Override
            public void done(List<Restaurant> restaurants, ParseException error) {
                if (error == null) {
                    if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                        emptyStateTextView.setVisibility(View.GONE);
                    }
                    if (restaurants != null) {
                        mRestaurant.clear();
                        for (Restaurant restaurant : restaurants) {
                            RestaurantItem currentRestaurant = new RestaurantItem();
                            currentRestaurant.setName(restaurant.getRestoName());
                            currentRestaurant.setDesc(restaurant.getDescription());
                            currentRestaurant.setAddress(restaurant.getAddress());
                            currentRestaurant.setImage(restaurant.getRestoImageUrl());
                            currentRestaurant.setCoord(restaurant.getCoordinates());
                            mRestaurant.add(currentRestaurant);
                        }
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    emptyState();
                }
            }
        });
    }

    public void sortDescending(){
        clearData();
        removeEmptyState();
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.orderByDescending("resName");
        query.findInBackground(new FindCallback<Restaurant>() {

            @Override
            public void done(List<Restaurant> restaurants, ParseException error) {
                if (error == null) {
                    if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                        emptyStateTextView.setVisibility(View.GONE);
                    }
                    if (restaurants != null) {
                        mRestaurant.clear();
                        for (Restaurant restaurant : restaurants) {
                            RestaurantItem currentRestaurant = new RestaurantItem();
                            currentRestaurant.setName(restaurant.getRestoName());
                            currentRestaurant.setDesc(restaurant.getDescription());
                            currentRestaurant.setAddress(restaurant.getAddress());
                            currentRestaurant.setImage(restaurant.getRestoImageUrl());
                            currentRestaurant.setCoord(restaurant.getCoordinates());
                            mRestaurant.add(currentRestaurant);
                        }
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    emptyState();
                }
            }
        });
    }

    public void itemSearch(String newText){
        clearData();
        removeEmptyState();
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.whereContains("resName_search", newText.toLowerCase().replaceAll("/[^a-zA-Z ]/g", ""));
        query.findInBackground(new FindCallback<Restaurant>() {

            @Override
            public void done(List<Restaurant> restaurants, ParseException error) {
                if (error == null) {
                    if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                        emptyStateTextView.setVisibility(View.GONE);
                    }
                    if (restaurants != null) {
                        mRestaurant.clear();
                        for (Restaurant restaurant : restaurants) {
                            RestaurantItem currentRestaurant = new RestaurantItem();
                            currentRestaurant.setName(restaurant.getRestoName());
                            currentRestaurant.setDesc(restaurant.getDescription());
                            currentRestaurant.setAddress(restaurant.getAddress());
                            currentRestaurant.setImage(restaurant.getRestoImageUrl());
                            currentRestaurant.setCoord(restaurant.getCoordinates());
                            mRestaurant.add(currentRestaurant);
                        }
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if (mRestaurant == null) {
                        Toast.makeText(getApplicationContext(), "List is Empty", Toast.LENGTH_LONG).show();
                    }
                } else {
                    emptyState();
                }
            }
        });
    }

    public void clearData() {
        mRestaurant.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void emptyState() {
        mRestaurant.clear();
        mAdapter.notifyDataSetChanged();
        emptyStateTextView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    public void removeEmptyState(){
        if (emptyStateTextView.getVisibility() == View.VISIBLE) {
            emptyStateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (ascending) {
            menu.getItem(3).setIcon(R.drawable.ic_sort_ascending);
        } else {
            menu.getItem(3).setIcon(R.drawable.ic_sort_descending);
        }

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            if (ascending) {
                menu.getItem(3).setIcon(R.drawable.ic_sort_descending);
                swipeRefreshLayout.setRefreshing(true);
                sortDescending();
                ascending = false;
            } else {
                menu.getItem(3).setIcon(R.drawable.ic_sort_ascending);
                swipeRefreshLayout.setRefreshing(true);
                sortAscending();
                ascending = true;
            }
            return true;
        } else if (id == R.id.action_account) {
            Intent intent = new Intent(this, LoginThroughFBActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        itemSearch(newText);
        swipeRefreshLayout.setRefreshing(true);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        //The Refresh must be only active when the offset is zero :
        swipeRefreshLayout.setEnabled(i == 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (ascending) {
            savedInstanceState.putBoolean("sortDrawable", true);
        } else {
            savedInstanceState.putBoolean("sortDrawable", false);
        }
    }
}

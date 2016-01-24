package com.ponnex.restosearch;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.ponnex.restosearch.models.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private RecyclerView mRecyclerView;

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    RestaurantAdapter mAdapter;

    SearchView searchView;

    TextView emptyStateTextView;

    ProgressBar progressBar;

    private List<RestaurantItem> mRestaurant = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.restoList);
        mRecyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        emptyStateTextView = (TextView)findViewById(R.id.empty_state);

        progressBar = (ProgressBar)findViewById(R.id.loading_resto);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAdapter = new RestaurantAdapter(mRestaurant, R.layout.activity_card_resto, this);
        mRecyclerView.setAdapter(mAdapter);

        updateData();
    }


    public void updateData(){
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.findInBackground(new FindCallback<Restaurant>() {

            @Override
            public void done(List<Restaurant> restaurants, ParseException error) {
                if(error == null) {
                    if (emptyStateTextView.getVisibility() == View.VISIBLE || progressBar.getVisibility() == View.VISIBLE) {
                        emptyStateTextView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                    if (restaurants != null) {
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
                    }
                } else {
                    emptyState();
                }
            }
        });
    }

    public void sortAscending(){
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.orderByAscending("resName");
        query.findInBackground(new FindCallback<Restaurant>() {

            @Override
            public void done(List<Restaurant> restaurants, ParseException error) {
                if (error == null) {
                    if (emptyStateTextView.getVisibility() == View.VISIBLE || progressBar.getVisibility() == View.VISIBLE) {
                        emptyStateTextView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.GONE);
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
                    }
                } else {
                    emptyState();
                }
            }
        });
    }

    public void sortDescending(){
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.orderByDescending("resName");
        query.findInBackground(new FindCallback<Restaurant>() {

            @Override
            public void done(List<Restaurant> restaurants, ParseException error) {
                if (error == null) {
                    if (emptyStateTextView.getVisibility() == View.VISIBLE || progressBar.getVisibility() == View.VISIBLE) {
                        emptyStateTextView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.GONE);
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
                    }
                } else {
                    emptyState();
                }
            }
        });
    }

    public void itemSearch(String newText){
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.whereContains("resName_search", newText.toLowerCase().replaceAll("/[^a-zA-Z ]/g", ""));
        query.findInBackground(new FindCallback<Restaurant>() {

            @Override
            public void done(List<Restaurant> restaurants, ParseException error) {
                if (error == null) {
                    if (emptyStateTextView.getVisibility() == View.VISIBLE || progressBar.getVisibility() == View.VISIBLE) {
                        emptyStateTextView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.GONE);
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
                    }
                } else {
                    emptyState();
                }
            }
        });
    }

    public void emptyState() {
        mRestaurant.clear();
        mAdapter.notifyDataSetChanged();
        emptyStateTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
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
        if (id == R.id.action_ascending) {
            progressBar.setVisibility(View.VISIBLE);
            sortAscending();
            return true;
        } else if (id == R.id.action_descending) {
            progressBar.setVisibility(View.VISIBLE);
            sortDescending();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        itemSearch(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

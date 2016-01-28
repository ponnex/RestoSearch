package com.ponnex.restosearch.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.ponnex.restosearch.R;
import com.ponnex.restosearch.instance.FoodItem;
import com.ponnex.restosearch.models.Food;
import com.ponnex.restosearch.ui.adapter.FoodAdapter;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ponnex on 1/12/2016.
 */
public class RestoActivity extends AppCompatActivity implements RoutingListener, AppBarLayout.OnOffsetChangedListener, SearchView.OnQueryTextListener {

    public static final String EXTRA_URL = "image_url";
    public static final String EXTRA_NAME = "resto_name";
    public static final String EXTRA_DESC = "resto_desc";
    public static final String EXTRA_ADD = "resto_add";
    public static final String EXTRA_LAT = "coord_lat";
    public static final String EXTRA_LONG = "coord_long";
    public static final String EXTRA_ID = "objectId";
    public static String imageUrl;
    public static String restoName;
    public static String restoDesc;
    public static String restoAdd;
    public static String coordLat;
    public static String coordLong;
    public static String restoId;
    protected LatLng start;
    protected LatLng end;
    private double duration;
    private double distance;
    private TextView restoTextDuration;
    private TextView restoTextDistance;
    private TextView emptyStateTextView;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private FoodAdapter mAdapter;
    private Menu menu;
    private boolean ascending = true;

    private SwipeRefreshLayout swipeRefreshLayout;

    private List<FoodItem> mMenu = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto);

        Intent intent = getIntent();
        imageUrl = intent.getStringExtra(EXTRA_URL);
        restoName = intent.getStringExtra(EXTRA_NAME);
        coordLat = intent.getStringExtra(EXTRA_LAT);
        coordLong = intent.getStringExtra(EXTRA_LONG);
        restoDesc = intent.getStringExtra(EXTRA_DESC);
        restoAdd = intent.getStringExtra(EXTRA_ADD);
        restoId = intent.getStringExtra(EXTRA_ID);

        restoTextDuration = (TextView)findViewById(R.id.restoDuration);
        restoTextDistance= (TextView)findViewById(R.id.restoDistance);

        TextView restoTextDesc = (TextView)findViewById(R.id.restoDesc);
        restoTextDesc.setText(RestoActivity.restoDesc);

        TextView restoTextAdd = (TextView)findViewById(R.id.restoAddress);
        restoTextAdd.setText(RestoActivity.restoAdd);

        appBarLayout = (AppBarLayout)findViewById(R.id.appbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestoActivity.this, MapsActivity.class);
                intent.putExtra("coord_long", coordLong);
                intent.putExtra("coord_lat", coordLat);
                intent.putExtra("resto_name", restoName);
                startActivity(intent);
            }
        });

        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.menuList);
        mRecyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        emptyStateTextView = (TextView)findViewById(R.id.empty_state);

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

        mAdapter = new FoodAdapter(mMenu, R.layout.card_food, this);
        mRecyclerView.setAdapter(mAdapter);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(restoName);

        start = new LatLng(8.451690, 124.624713);
        end = new LatLng(Double.parseDouble(RestoActivity.coordLat), Double.parseDouble(RestoActivity.coordLong));

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(start, end)
                .build();
        routing.execute();

        loadBackdrop();
        updateData();

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

    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //The Refresh must be only active when the offset is zero :
        if(verticalOffset == 0) {
            fab.show();
        } else {
            fab.hide();
        }
        swipeRefreshLayout.setEnabled(verticalOffset == 0);
    }


    public void updateData(){
        clearData();
        removeEmptyState();
        ParseQuery<ParseObject> objectId = ParseQuery.getQuery("Restaurant");
        objectId.getInBackground(RestoActivity.restoId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    removeEmptyState();
                    ParseQuery<Food> query = ParseQuery.getQuery(Food.class);
                    query.whereEqualTo("resPointer", object);
                    query.findInBackground(new FindCallback<Food>() {

                        @Override
                        public void done(List<Food> menus, ParseException error) {
                            if (error == null) {
                                if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                                    emptyStateTextView.setVisibility(View.GONE);
                                }
                                if (menus != null) {
                                    mMenu.clear();
                                    for (Food menu : menus) {
                                        FoodItem currentMenu = new FoodItem();
                                        currentMenu.setName(menu.getFoodName());
                                        currentMenu.setDesc(menu.getFoodDescription());
                                        currentMenu.setPrice(menu.getFoodPrice());
                                        currentMenu.setImage(menu.getFoodImageUrl());
                                        mMenu.add(currentMenu);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            } else {
                                emptyState();
                            }
                        }
                    });
                } else {
                    emptyState();
                }
            }
        });
    }

    public void sortAscending(){
        clearData();
        removeEmptyState();
        ParseQuery<ParseObject> objectId = ParseQuery.getQuery("Restaurant");
        objectId.getInBackground(RestoActivity.restoId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    removeEmptyState();
                    ParseQuery<Food> query = ParseQuery.getQuery(Food.class);
                    query.whereEqualTo("resPointer", object);
                    query.orderByAscending("resName");
                    query.findInBackground(new FindCallback<Food>() {

                        @Override
                        public void done(List<Food> menus, ParseException error) {
                            if (error == null) {
                                if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                                    emptyStateTextView.setVisibility(View.GONE);
                                }
                                if (menus != null) {
                                    mMenu.clear();
                                    for (Food menu : menus) {
                                        FoodItem currentMenu = new FoodItem();
                                        currentMenu.setName(menu.getFoodName());
                                        currentMenu.setDesc(menu.getFoodDescription());
                                        currentMenu.setPrice(menu.getFoodPrice());
                                        currentMenu.setImage(menu.getFoodImageUrl());
                                        mMenu.add(currentMenu);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            } else {
                                emptyState();
                            }
                        }
                    });
                } else {
                    emptyState();
                }
            }
        });
    }

    public void sortDescending(){
        clearData();
        removeEmptyState();
        ParseQuery<ParseObject> objectId = ParseQuery.getQuery("Restaurant");
        objectId.getInBackground(RestoActivity.restoId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    removeEmptyState();
                    ParseQuery<Food> query = ParseQuery.getQuery(Food.class);
                    query.whereEqualTo("resPointer", object);
                    query.orderByDescending("resName");
                    query.findInBackground(new FindCallback<Food>() {

                        @Override
                        public void done(List<Food> menus, ParseException error) {
                            if (error == null) {
                                if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                                    emptyStateTextView.setVisibility(View.GONE);
                                }
                                if (menus != null) {
                                    mMenu.clear();
                                    for (Food menu : menus) {
                                        FoodItem currentMenu = new FoodItem();
                                        currentMenu.setName(menu.getFoodName());
                                        currentMenu.setDesc(menu.getFoodDescription());
                                        currentMenu.setPrice(menu.getFoodPrice());
                                        currentMenu.setImage(menu.getFoodImageUrl());
                                        mMenu.add(currentMenu);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            } else {
                                emptyState();
                            }
                        }
                    });
                } else {
                    emptyState();
                }
            }
        });
    }

    public void itemSearch(final String newText){
        clearData();
        removeEmptyState();
        ParseQuery<ParseObject> objectId = ParseQuery.getQuery("Restaurant");
        objectId.getInBackground(RestoActivity.restoId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    removeEmptyState();
                    ParseQuery<Food> query = ParseQuery.getQuery(Food.class);
                    query.whereEqualTo("resPointer", object);
                    query.whereContains("resName_search", newText.toLowerCase().replaceAll("/[^a-zA-Z ]/g", ""));
                    query.findInBackground(new FindCallback<Food>() {

                        @Override
                        public void done(List<Food> menus, ParseException error) {
                            if (error == null) {
                                if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                                    emptyStateTextView.setVisibility(View.GONE);
                                }
                                if (menus != null) {
                                    mMenu.clear();
                                    for (Food menu : menus) {
                                        FoodItem currentMenu = new FoodItem();
                                        currentMenu.setName(menu.getFoodName());
                                        currentMenu.setDesc(menu.getFoodDescription());
                                        currentMenu.setPrice(menu.getFoodPrice());
                                        currentMenu.setImage(menu.getFoodImageUrl());
                                        mMenu.add(currentMenu);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            } else {
                                emptyState();
                            }
                        }
                    });
                } else {
                    emptyState();
                }
            }
        });
    }

    public void clearData() {
        mMenu.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void emptyState() {
        mMenu.clear();
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
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        for (int i = 0; i <route.size(); i++) {
            //Toast.makeText(getActivity(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
            duration = ((double)route.get(i).getDurationValue()) / 60;
            distance = ((double)route.get(i).getDistanceValue()) / 1000;
        }
        restoTextDuration.setText((int) Math.round(duration) + " min");
        restoTextDistance.setText(round(distance, 1) + " km");
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingCancelled() {
        Log.i("RestoActivity", "Routing was cancelled.");
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Picasso.with(this).load(imageUrl).error(R.drawable.cheese_1).into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (ascending) {
            menu.getItem(1).setIcon(R.drawable.ic_sort_ascending);
        } else {
            menu.getItem(1).setIcon(R.drawable.ic_sort_descending);
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
                menu.getItem(1).setIcon(R.drawable.ic_sort_descending);
                swipeRefreshLayout.setRefreshing(true);
                sortDescending();
                ascending = false;
            } else {
                menu.getItem(1).setIcon(R.drawable.ic_sort_ascending);
                swipeRefreshLayout.setRefreshing(true);
                sortAscending();
                ascending = true;
            }
            return true;
        } else if (id == R.id.action_account) {
            Intent intent = new Intent(this, LoginFacebookActivity.class);
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
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (ascending) {
            savedInstanceState.putBoolean("sortDrawable", true);
        } else {
            savedInstanceState.putBoolean("sortDrawable", false);
        }
    }

}

package com.ponnex.restosearch.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ponnex.restosearch.R;
import com.ponnex.restosearch.instance.MenuItem;
import com.ponnex.restosearch.models.Menu;
import com.ponnex.restosearch.ui.adapter.MenuAdapter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ponnex on 1/12/2016.
 */
public class MenuActivity extends AppCompatActivity implements RoutingListener, AppBarLayout.OnOffsetChangedListener, SearchView.OnQueryTextListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String EXTRA_URL = "image_url", EXTRA_NAME = "resto_name", EXTRA_DESC = "resto_desc", EXTRA_ADD = "resto_add", EXTRA_LAT = "coord_lat", EXTRA_LONG = "coord_long", EXTRA_ID = "objectId";
    public static String imageUrl, restoName ,restoDesc, restoAdd, coordLat, coordLong, restoId;
    private TextView restoTextDuration, restoTextDistance, emptyStateTextView, restoTextDesc, restoTextAdd;
    private List<MenuItem> mMenu = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private GoogleApiClient mGoogleApiClient;
    private CollapsingToolbarLayout collapsingToolbar;
    private EditText editDescription, editAddress, editRestaurant;
    private static final int RESULT_LOAD_IMAGE = 1;
    private RecyclerView mRecyclerView;
    private double duration, distance;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private boolean ascending = true;
    protected Location mLastLocation;
    private MenuAdapter mAdapter;
    private Toolbar toolbar;
    protected LatLng start;
    protected LatLng end;
    private android.view.Menu menu;
    private ImageView changeRestoImage, imageView;
    private Bitmap bmp;
    private Uri selectedImage;
    private Boolean loadImageSuccess = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        imageUrl = intent.getStringExtra(EXTRA_URL);
        restoName = intent.getStringExtra(EXTRA_NAME);
        coordLat = intent.getStringExtra(EXTRA_LAT);
        coordLong = intent.getStringExtra(EXTRA_LONG);
        restoDesc = intent.getStringExtra(EXTRA_DESC);
        restoAdd = intent.getStringExtra(EXTRA_ADD);
        restoId = intent.getStringExtra(EXTRA_ID);

        imageView = (ImageView) findViewById(R.id.backdrop);

        changeRestoImage = (ImageView)findViewById(R.id.change_image);
        changeRestoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        restoTextDuration = (TextView)findViewById(R.id.restoDuration);
        restoTextDistance = (TextView)findViewById(R.id.restoDistance);

        restoTextDesc = (TextView)findViewById(R.id.restoDesc);
        restoTextDesc.setText(restoDesc);

        restoTextAdd = (TextView)findViewById(R.id.restoAddress);
        restoTextAdd.setText(restoAdd);

        appBarLayout = (AppBarLayout)findViewById(R.id.appbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
                intent.putExtra("coord_long", coordLong);
                intent.putExtra("coord_lat", coordLat);
                intent.putExtra("resto_name", restoName);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.menuList);
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

        mAdapter = new MenuAdapter(mMenu, R.layout.activity_menu_card, this);
        mRecyclerView.setAdapter(mAdapter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editDescription = (EditText)findViewById(R.id.edit_description);
        editAddress = (EditText)findViewById(R.id.edit_address);
        editRestaurant = (EditText)findViewById(R.id.edit_restaurant);

        collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(restoName);

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        loadBackdrop();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            try {
                bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Picasso.with(getApplicationContext()).load(selectedImage).error(R.drawable.cheese_1).into(imageView);

                loadImageSuccess = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
            }

        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            end =  new LatLng(Double.parseDouble(coordLat), Double.parseDouble(coordLong));

            //start routing
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)
                    .withListener(this)
                    .waypoints(start, end)
                    .build();
            routing.execute();
        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(50000); //5 minutes
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        for (int i = 0; i <route.size(); i++) {
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
        Log.i("MenuActivity", "Routing was cancelled.");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //The Refresh must be only active when the offset is zero :
        if(verticalOffset == 0) {
            fab.show();
        } else {
            fab.hide();
            new LinearLayoutManager(this).scrollToPositionWithOffset(1, 0);
        }
        swipeRefreshLayout.setEnabled(verticalOffset == 0);
    }

    public void updateData(){
        clearData();
        removeEmptyState();
        ParseQuery<ParseObject> objectId = ParseQuery.getQuery("Restaurant");
        objectId.getInBackground(restoId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    removeEmptyState();
                    ParseQuery<Menu> query = ParseQuery.getQuery(Menu.class);
                    query.whereEqualTo("resPointer", object);
                    query.findInBackground(new FindCallback<Menu>() {

                        @Override
                        public void done(List<Menu> menus, ParseException error) {
                            if (error == null) {
                                if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                                    emptyStateTextView.setVisibility(View.GONE);
                                }
                                if (menus != null) {
                                    mMenu.clear();
                                    for (Menu menu : menus) {
                                        MenuItem currentMenu = new MenuItem();
                                        currentMenu.setId(menu.getObjectId());
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
        objectId.getInBackground(restoId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    removeEmptyState();
                    ParseQuery<Menu> query = ParseQuery.getQuery(Menu.class);
                    query.whereEqualTo("resPointer", object);
                    query.orderByAscending("resName");
                    query.findInBackground(new FindCallback<Menu>() {

                        @Override
                        public void done(List<Menu> menus, ParseException error) {
                            if (error == null) {
                                if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                                    emptyStateTextView.setVisibility(View.GONE);
                                }
                                if (menus != null) {
                                    mMenu.clear();
                                    for (Menu menu : menus) {
                                        MenuItem currentMenu = new MenuItem();
                                        currentMenu.setId(menu.getObjectId());
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
        objectId.getInBackground(restoId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    removeEmptyState();
                    ParseQuery<Menu> query = ParseQuery.getQuery(Menu.class);
                    query.whereEqualTo("resPointer", object);
                    query.orderByDescending("resName");
                    query.findInBackground(new FindCallback<Menu>() {

                        @Override
                        public void done(List<Menu> menus, ParseException error) {
                            if (error == null) {
                                if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                                    emptyStateTextView.setVisibility(View.GONE);
                                }
                                if (menus != null) {
                                    mMenu.clear();
                                    for (Menu menu : menus) {
                                        MenuItem currentMenu = new MenuItem();
                                        currentMenu.setId(menu.getObjectId());
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
        objectId.getInBackground(restoId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    removeEmptyState();
                    ParseQuery<Menu> query = ParseQuery.getQuery(Menu.class);
                    query.whereEqualTo("resPointer", object);
                    query.whereContains("resName_search", newText.toLowerCase().replaceAll("/[^a-zA-Z ]/g", ""));
                    query.findInBackground(new FindCallback<Menu>() {

                        @Override
                        public void done(List<Menu> menus, ParseException error) {
                            if (error == null) {
                                if (emptyStateTextView.getVisibility() == View.VISIBLE) {
                                    emptyStateTextView.setVisibility(View.GONE);
                                }
                                if (menus != null) {
                                    mMenu.clear();
                                    for (Menu menu : menus) {
                                        MenuItem currentMenu = new MenuItem();
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

    private void loadBackdrop() {
        Picasso.with(this).load(imageUrl).error(R.drawable.cheese_1).into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(final android.view.Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (ascending) {
            menu.getItem(3).setIcon(R.drawable.ic_sort_ascending);
        } else {
            menu.getItem(3).setIcon(R.drawable.ic_sort_descending);
        }

        menu.getItem(4).setVisible(false);

        if (ParseUser.getCurrentUser() != null) {
            final ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
            query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject object, ParseException e) {
                    if (e == null) {
                        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Restaurant");
                        query1.whereEqualTo("resAdmin", object);
                        query1.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> admins, ParseException e) {
                                if (admins != null) {
                                    for (ParseObject admin : admins) {
                                        if (admin.getObjectId().equals(restoId)) {
                                            menu.getItem(1).setVisible(true);
                                        }
                                    }
                                } else {
                                    //user is not an admin
                                }
                            }
                        });
                    } else {
                        //there is no user logged in
                    }
                }
            });
        }

        menu.getItem(0).setOnMenuItemClickListener(new android.view.MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_saveResto) {
                    editDescription.setVisibility(View.GONE);
                    editAddress.setVisibility(View.GONE);
                    editRestaurant.setVisibility(View.GONE);
                    restoTextDesc.setVisibility(View.VISIBLE);
                    restoTextAdd.setVisibility(View.VISIBLE);
                    changeRestoImage.setVisibility(View.GONE);
                    collapsingToolbar.setTitleEnabled(true);
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(true);

                    if (editDescription.getText().toString().equals(restoDesc) && editAddress.getText().toString().equals(restoAdd) && editRestaurant.getText().toString().equals(restoName) && loadImageSuccess) {
                        Snackbar.make(findViewById(android.R.id.content),"Are you sure you've changed something?", Snackbar.LENGTH_LONG).show();
                    }

                    if (bmp != null && loadImageSuccess) {

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        final ParseFile newImage = new ParseFile(selectedImage.getLastPathSegment() + ".jpg", stream.toByteArray());

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
                        query.getInBackground(restoId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getApplicationContext(), "Uploading...", Toast.LENGTH_LONG).show();
                                    object.put("resImage", newImage);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_LONG).show();
                                                Picasso.with(getApplicationContext()).load(selectedImage).error(R.drawable.cheese_1).into(imageView);
                                                loadImageSuccess = false;
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }

                    if (editDescription.getText().length() > 0 && !editDescription.getText().toString().equals(restoDesc)) {

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
                        query.getInBackground(restoId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    object.put("resDescription", editDescription.getText().toString());
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(getApplicationContext(), "Description Saved", Toast.LENGTH_LONG).show();
                                                restoDesc = editDescription.getText().toString();
                                                restoTextDesc.setText(restoDesc);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } if (editAddress.getText().length() > 0 && !editAddress.getText().toString().equals(restoAdd)) {

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
                        query.getInBackground(restoId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    object.put("resAddress", editAddress.getText().toString());
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(getApplicationContext(), "Address Saved", Toast.LENGTH_LONG).show();
                                                restoAdd = editAddress.getText().toString();
                                                restoTextAdd.setText(restoAdd);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } if (editRestaurant.getText().length() > 0 && !editRestaurant.getText().toString().equals(restoName)) {

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Restaurant");
                        query.getInBackground(restoId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    object.put("resName", editRestaurant.getText().toString());
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(getApplicationContext(), "Name Saved", Toast.LENGTH_LONG).show();
                                                restoName = editRestaurant.getText().toString();
                                                collapsingToolbar.setTitle(restoName);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                }
                return false;
            }
        });

        menu.getItem(1).setOnMenuItemClickListener(new android.view.MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(android.view.MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_editResto) {
                    editDescription.setVisibility(View.VISIBLE);
                    editDescription.setText(restoDesc);
                    editAddress.setVisibility(View.VISIBLE);
                    editAddress.setText(restoAdd);
                    editRestaurant.setVisibility(View.VISIBLE);
                    editRestaurant.setText(restoName);
                    restoTextDesc.setVisibility(View.GONE);
                    restoTextAdd.setVisibility(View.GONE);
                    changeRestoImage.setVisibility(View.VISIBLE);
                    collapsingToolbar.setTitleEnabled(false);
                    menu.getItem(0).setVisible(true);
                    menu.getItem(1).setVisible(false);
                    appBarLayout.setExpanded(true, true);
                }
                return false;
            }
        });

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                }
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                toolbar.setBackgroundColor(Color.TRANSPARENT);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
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
    public void onBackPressed() {
        if (menu.getItem(0).isVisible()) {
            editDescription.setVisibility(View.GONE);
            editAddress.setVisibility(View.GONE);
            editRestaurant.setVisibility(View.GONE);
            changeRestoImage.setVisibility(View.GONE);
            restoTextDesc.setVisibility(View.VISIBLE);
            restoTextAdd.setVisibility(View.VISIBLE);
            collapsingToolbar.setTitleEnabled(true);
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            Picasso.with(this).load(imageUrl).error(R.drawable.cheese_1).into(imageView);
            Snackbar.make(findViewById(android.R.id.content),"Changes not saved.", Snackbar.LENGTH_LONG).show();
        } else {
            finish();
        }
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

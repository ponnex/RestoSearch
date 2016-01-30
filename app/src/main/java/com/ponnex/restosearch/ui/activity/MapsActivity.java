package com.ponnex.restosearch.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ponnex.restosearch.R;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    protected LatLng start;
    protected LatLng end;
    protected Location mLastLocation;
    private Marker mCurrLocation;
    private Marker mRestoLocation;
    private LatLngBounds bounds;

    public static final String EXTRA_LAT = "coord_lat";
    public static final String EXTRA_LONG = "coord_long";
    public static final String EXTRA_NAME = "resto_name";
    private String restoName;
    private boolean bothMarkersShowing = true;

    private int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;

    private ArrayList<Polyline> polylines;

    private int[] colors = new int[]{R.color.colorPrimaryDark ,R.color.colorPrimary, R.color.colorPrimary, R.color.colorAccent, R.color.colorControlHighlight};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= 23) {
            requestAccountPermission();
        } else {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        Intent intent = getIntent();
        restoName = intent.getStringExtra(EXTRA_NAME);
        String coordLat = intent.getStringExtra(EXTRA_LAT);
        String coordLong = intent.getStringExtra(EXTRA_LONG);
        end = new LatLng(Double.parseDouble(coordLat), Double.parseDouble(coordLong));

        polylines = new ArrayList<>();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(restoName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void requestAccountPermission() {

        SharedPreferences mPreferences = getSharedPreferences("configuration", MODE_PRIVATE);
        boolean firstTimeAccount = mPreferences.getBoolean("firstTimeAccount", true);

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // 2. Asked before, and the user said "no"
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            if(firstTimeAccount) {
                // 1. first time, never asked
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTimeAccount", false);
                editor.commit();

                // Account permission has not been granted, request it directly.
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            }else{
                // 3. If you asked a couple of times before, and the user has said "no, and stop asking"

                // Your code
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(end, 16));
        mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(mCurrLocation)) {
            zoomInOutCurrent();
        }

        if (marker.equals(mRestoLocation)) {
            zoomInOutResto();
        }
        return true;
    }

    public void zoomInOutCurrent() {
        if (bothMarkersShowing) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 16));
            bothMarkersShowing = false;
        } else {
            CameraUpdate bothMarkers = CameraUpdateFactory.newLatLngBounds(bounds, 70);
            mMap.animateCamera(bothMarkers);
            bothMarkersShowing = true;
        }
    }

    public void zoomInOutResto() {
        if (bothMarkersShowing) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(end, 16));
            bothMarkersShowing = false;
        } else {
            CameraUpdate bothMarkers = CameraUpdateFactory.newLatLngBounds(bounds, 70);
            mMap.animateCamera(bothMarkers);
            bothMarkersShowing = true;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
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
            //place marker at current position
            mMap.clear();

            start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(start);
            builder.include(end);
            bounds = builder.build();

            CameraUpdate bothMarkers = CameraUpdateFactory.newLatLngBounds(bounds, 70);
            mMap.animateCamera(bothMarkers);

            //start routing
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)
                    .withListener(this)
                    .waypoints(start, end)
                    .build();
            routing.execute();
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000); //5 seconds
        mLocationRequest.setFastestInterval(1000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        //remove previous current location Marker
        if (mCurrLocation != null) {
            mCurrLocation.remove();

        }

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
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();

        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % colors.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ContextCompat.getColor(this, colors[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }

        // Start marker
        mCurrLocation = mMap.addMarker(new MarkerOptions()
        .position(start)
        .title("You")
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue)));

        // End marker
        mRestoLocation = mMap.addMarker(new MarkerOptions()
                .position(end)
                .title(restoName)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green)));
        mRestoLocation.showInfoWindow();

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        //show view or toast for the error
    }

    @Override
    public void onRoutingCancelled() {
        Log.i("MapsActivity", "Routing was cancelled.");
    }
}

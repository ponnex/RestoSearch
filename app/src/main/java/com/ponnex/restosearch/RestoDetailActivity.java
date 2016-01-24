package com.ponnex.restosearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by ponnex on 1/12/2016.
 */
public class RestoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "image_url";
    public static final String EXTRA_NAME = "resto_name";
    public static final String EXTRA_DESC = "resto_desc";
    public static final String EXTRA_ADD = "resto_add";
    public static final String EXTRA_LAT = "coord_lat";
    public static final String EXTRA_LONG = "coord_long";
    String imageUrl;
    Bundle bundle;
    private String restoName;
    private String coordLat;
    private String coordLong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        imageUrl = intent.getStringExtra(EXTRA_URL);
        restoName = intent.getStringExtra(EXTRA_NAME);
        String restoDesc = intent.getStringExtra(EXTRA_DESC);
        String restoAdd = intent.getStringExtra(EXTRA_ADD);
        coordLat = intent.getStringExtra(EXTRA_LAT);
        coordLong = intent.getStringExtra(EXTRA_LONG);

        TextView restoTextDesc = (TextView)findViewById(R.id.restoDesc);
        restoTextDesc.setText(restoDesc);

        TextView restoTextAdd = (TextView)findViewById(R.id.restoAddress);
        restoTextAdd.setText(restoAdd);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestoDetailActivity.this, MapsActivity.class);
                intent.putExtra("coord_long", coordLong);
                intent.putExtra("coord_lat", coordLat);
                intent.putExtra("resto_name", restoName);
                startActivity(intent);
            }
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(restoName);

        Intent imageUrlIntent = getIntent();
        bundle = imageUrlIntent.getExtras();

        loadBackdrop();
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        if(bundle != null) {
            Picasso.with(this).load(imageUrl).error(R.drawable.cheese_1).into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}

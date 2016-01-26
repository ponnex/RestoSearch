package com.ponnex.restosearch.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.ponnex.restosearch.R;
import com.ponnex.restosearch.ui.fragment.InfoFragment;
import com.ponnex.restosearch.ui.fragment.FoodFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ponnex on 1/12/2016.
 */
public class RestoActivity extends AppCompatActivity {

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

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        imageUrl = intent.getStringExtra(EXTRA_URL);
        restoName = intent.getStringExtra(EXTRA_NAME);
        coordLat = intent.getStringExtra(EXTRA_LAT);
        coordLong = intent.getStringExtra(EXTRA_LONG);
        restoDesc = intent.getStringExtra(EXTRA_DESC);
        restoAdd = intent.getStringExtra(EXTRA_ADD);
        restoId = intent.getStringExtra(EXTRA_ID);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(restoName);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        loadBackdrop();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new InfoFragment(), "Info");
        adapter.addFragment(new FoodFragment(), "Menu");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Picasso.with(this).load(imageUrl).error(R.drawable.cheese_1).into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}

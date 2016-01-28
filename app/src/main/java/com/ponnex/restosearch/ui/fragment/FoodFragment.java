package com.ponnex.restosearch.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.ponnex.restosearch.instance.FoodItem;
import com.ponnex.restosearch.ui.adapter.FoodAdapter;
import com.ponnex.restosearch.R;
import com.ponnex.restosearch.models.Food;
import com.ponnex.restosearch.ui.activity.RestoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ponne on 1/24/2016.
 */
public class FoodFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private RecyclerView mRecyclerView;

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    FoodAdapter mAdapter;

    TextView emptyStateTextView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private List<FoodItem> mMenu = new ArrayList<>();

    private boolean ascending = true;

    public FoodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_menu_resto, container, false);
        final FragmentActivity fragmentActivity = getActivity();
        // Inflate the layout for this fragment
        mRecyclerView = (RecyclerView)view.findViewById(R.id.menuList);
        mRecyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(gaggeredGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        emptyStateTextView = (TextView)view.findViewById(R.id.empty_state);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*
                if (ascending) {
                    sortAscending();
                } else {
                    sortDescending();
                }
                */
                updateData();
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        mAdapter = new FoodAdapter(mMenu, R.layout.card_food, fragmentActivity);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //The Refresh must be only active when the offset is zero :
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
}
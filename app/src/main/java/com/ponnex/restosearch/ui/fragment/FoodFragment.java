package com.ponnex.restosearch.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
public class FoodFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    FoodAdapter mAdapter;

    TextView emptyStateTextView;

    ProgressBar progressBar;

    private List<FoodItem> mMenu = new ArrayList<>();

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

        progressBar = (ProgressBar)view.findViewById(R.id.loading_menu);

        mAdapter = new FoodAdapter(mMenu, R.layout.card_food, fragmentActivity);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }


    public void updateData(){
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
                                if (emptyStateTextView.getVisibility() == View.VISIBLE || progressBar.getVisibility() == View.VISIBLE) {
                                    emptyStateTextView.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.GONE);
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

    public void emptyState() {
        mMenu.clear();
        mAdapter.notifyDataSetChanged();
        emptyStateTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public void removeEmptyState(){
        if (emptyStateTextView.getVisibility() == View.VISIBLE) {
            emptyStateTextView.setVisibility(View.GONE);
        }
    }
}
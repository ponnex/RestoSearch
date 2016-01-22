package com.ponnex.restosearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ponnex on 1/22/2016.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private Context mContext;
    private int rowLayout;
    private List<RestaurantItem> mRestaurant;

    public RestaurantAdapter (List<RestaurantItem> restaurants, int rowLayout, Context context) {
        this.mContext = context;
        this.rowLayout = rowLayout;
        this.mRestaurant = restaurants;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        RestaurantItem resto = mRestaurant.get(position);
        viewHolder.restoName.setText(resto.getName());
        //viewHolder.countryImage.setImageDrawable(mContext.getDrawable(resto.getImageResourceId(mContext)));
    }

    @Override
    public int getItemCount() {
        return mRestaurant == null ? 0 : mRestaurant.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView restoName;
        //public ImageView restoImage;

        public ViewHolder(View itemView) {
            super(itemView);
            restoName = (TextView) itemView.findViewById(R.id.restoName);
            //restoImage = (ImageView)itemView.findViewById(R.id.restoImage);
        }

    }
}
package com.ponnex.restosearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ponnex.restosearch.ui.activity.RestoActivity;
import com.squareup.picasso.Picasso;

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
        final RestaurantItem resto = mRestaurant.get(position);
        viewHolder.restoName.setText(resto.getName());
        viewHolder.restoDesc.setText(resto.getDesc());
        viewHolder.restoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RestoActivity.class);
                intent.putExtra("coord_long", String.valueOf(resto.getCoord().getLongitude()));
                intent.putExtra("coord_lat", String.valueOf(resto.getCoord().getLatitude()));
                intent.putExtra("resto_name", resto.getName());
                intent.putExtra("resto_desc", resto.getDesc());
                intent.putExtra("resto_add", resto.getAddress());
                intent.putExtra("image_url", resto.getImage());
                mContext.startActivity(intent);
            }
        });
        Picasso.with(mContext).load(resto.getImage()).into(viewHolder.restoImage);
    }

    @Override
    public int getItemCount() {
        return mRestaurant == null ? 0 : mRestaurant.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView restoName;
        public TextView restoDesc;
        public ImageView restoImage;
        public CardView restoCard;

        public ViewHolder(View itemView) {
            super(itemView);
            restoCard = (CardView) itemView.findViewById(R.id.carditem_container);
            restoName = (TextView) itemView.findViewById(R.id.restoName);
            restoDesc = (TextView) itemView.findViewById(R.id.restoDesc);
            restoImage = (ImageView)itemView.findViewById(R.id.restoImage);
        }
    }
}
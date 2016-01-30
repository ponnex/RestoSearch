package com.ponnex.restosearch.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ponnex.restosearch.R;
import com.ponnex.restosearch.instance.RestaurantItem;
import com.ponnex.restosearch.ui.activity.MenuActivity;
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
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final RestaurantItem restoItem = mRestaurant.get(position);
        viewHolder.restoName.setText(restoItem.getName());
        viewHolder.restoDesc.setText(restoItem.getDesc());
        viewHolder.restoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MenuActivity.class);
                intent.putExtra("coord_long", String.valueOf(restoItem.getCoord().getLongitude()));
                intent.putExtra("coord_lat", String.valueOf(restoItem.getCoord().getLatitude()));
                intent.putExtra("resto_name", restoItem.getName());
                intent.putExtra("resto_desc", restoItem.getDesc());
                intent.putExtra("resto_add", restoItem.getAddress());
                intent.putExtra("image_url", restoItem.getImage());
                intent.putExtra("objectId", restoItem.getId());
                mContext.startActivity(intent);
            }
        });
        Picasso.with(mContext).load(restoItem.getImage()).into(viewHolder.restoImage);
    }

    @Override
    public int getItemCount() {
        return mRestaurant == null ? 0 : mRestaurant.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView restoName;
        public TextView restoDesc;
        public ImageView restoImage;
        public ImageView editMenu;
        public CardView restoCard;

        public ViewHolder(View itemView) {
            super(itemView);
            restoCard = (CardView) itemView.findViewById(R.id.carditem_container);
            restoName = (TextView) itemView.findViewById(R.id.restoName);
            restoDesc = (TextView) itemView.findViewById(R.id.restoDesc);
            restoImage = (ImageView)itemView.findViewById(R.id.restoImage);
            editMenu = (ImageView)itemView.findViewById(R.id.editMenu);
        }
    }
}
package com.ponnex.restosearch.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ponnex.restosearch.instance.MenuItem;
import com.ponnex.restosearch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ponne on 1/25/2016.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private Context mContext;
    private int rowLayout;
    private List<MenuItem> mFood;

    public MenuAdapter(List<MenuItem> menus, int rowLayout, Context context) {
        this.mContext = context;
        this.rowLayout = rowLayout;
        this.mFood = menus;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final MenuItem food = mFood.get(position);
        viewHolder.foodName.setText(food.getName());
        viewHolder.foodDesc.setText(food.getDesc());
        viewHolder.foodPrice.setText("₱ " + food.getPrice());
                viewHolder.foodCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        Picasso.with(mContext).load(food.getImage()).into(viewHolder.foodImage);
    }

    @Override
    public int getItemCount() {
        return mFood == null ? 0 : mFood.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName;
        public TextView foodDesc;
        public TextView foodPrice;
        public ImageView foodImage;
        public CardView foodCard;

        public ViewHolder(View itemView) {
            super(itemView);
            foodCard = (CardView) itemView.findViewById(R.id.carditem_container);
            foodName = (TextView) itemView.findViewById(R.id.foodName);
            foodDesc = (TextView) itemView.findViewById(R.id.foodDesc);
            foodPrice = (TextView) itemView.findViewById(R.id.foodPrice);
            foodImage = (ImageView)itemView.findViewById(R.id.foodImage);
        }
    }
}

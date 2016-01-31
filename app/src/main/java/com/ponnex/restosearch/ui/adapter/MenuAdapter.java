package com.ponnex.restosearch.ui.adapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ponnex.restosearch.instance.MenuItem;
import com.ponnex.restosearch.R;
import com.ponnex.restosearch.ui.activity.MenuActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by ponne on 1/25/2016.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private Context mContext;
    private int rowLayout;
    private List<MenuItem> mFood;
    private static final int RESULT_LOAD_IMAGE_MENU = 2;
    private boolean loadImageSuccess = false;
    private Bitmap bmpMenu;
    private int itemPosition;
    private Uri selectedImage = null;

    public MenuAdapter(List<MenuItem> menus, int rowLayout, Context context) {
        this.mContext = context;
        this.rowLayout = rowLayout;
        this.mFood = menus;
    }

    public void setUri(Uri uri) {
        selectedImage = uri;
    }

    public void setBmp(Bitmap bitmap) {
        bmpMenu = bitmap;
    }

    public void setPosition(int position) {
        itemPosition = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        mContext = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final MenuItem food = mFood.get(position);
        viewHolder.foodName.setText(food.getName());
        viewHolder.foodDesc.setText(food.getDesc());
        viewHolder.foodPrice.setText("₱ " + food.getPrice());
        Picasso.with(mContext).load(food.getImage()).into(viewHolder.foodImage);

        if (selectedImage != null && position == itemPosition) {
            Picasso.with(mContext).load(selectedImage).into(viewHolder.foodImage);
        }

        if (bmpMenu != null) {
            loadImageSuccess = true;
        }

        viewHolder.editClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.editDesc.setVisibility(View.GONE);
                viewHolder.editName.setVisibility(View.GONE);
                viewHolder.editPrice.setVisibility(View.GONE);
                viewHolder.menuSave.setVisibility(View.GONE);
                viewHolder.changeImage.setVisibility(View.GONE);
                viewHolder.editClose.setVisibility(View.GONE);
                viewHolder.foodName.setVisibility(View.VISIBLE);
                viewHolder.foodDesc.setVisibility(View.VISIBLE);
                viewHolder.foodPrice.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(food.getImage()).into(viewHolder.foodImage);
                Toast.makeText(mContext, "Changes not saved.", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.changeImage.setTag(position);
        viewHolder.changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer taggedPosition = (Integer) v.getTag();
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.putExtra(MenuActivity.EXTRA_POSITION, taggedPosition);
                ((Activity) mContext).startActivityForResult(i, RESULT_LOAD_IMAGE_MENU);

                SharedPreferences isPosition = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = isPosition.edit();
                editor.putInt("item_position", taggedPosition);
                editor.apply();
            }
        });

        viewHolder.editMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.resto_card_popmenu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_editMenu) {
                            viewHolder.editDesc.setVisibility(View.VISIBLE);
                            viewHolder.editDesc.setText(food.getDesc());
                            viewHolder.editName.setVisibility(View.VISIBLE);
                            viewHolder.editName.setText(food.getName());
                            viewHolder.editPrice.setVisibility(View.VISIBLE);
                            viewHolder.editPrice.setText(food.getPrice());
                            viewHolder.menuSave.setVisibility(View.VISIBLE);
                            viewHolder.changeImage.setVisibility(View.VISIBLE);
                            viewHolder.editClose.setVisibility(View.VISIBLE);

                            viewHolder.foodName.setVisibility(View.GONE);
                            viewHolder.foodDesc.setVisibility(View.GONE);
                            viewHolder.foodPrice.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
            }
        });

        viewHolder.menuSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewHolder.editDesc.setVisibility(View.GONE);
                viewHolder.editName.setVisibility(View.GONE);
                viewHolder.editPrice.setVisibility(View.GONE);
                viewHolder.menuSave.setVisibility(View.GONE);
                viewHolder.changeImage.setVisibility(View.GONE);
                viewHolder.editClose.setVisibility(View.GONE);
                viewHolder.foodName.setVisibility(View.VISIBLE);
                viewHolder.foodDesc.setVisibility(View.VISIBLE);
                viewHolder.foodPrice.setVisibility(View.VISIBLE);

                if (viewHolder.editDesc.getText().toString().equals(food.getDesc()) && viewHolder.editName.getText().toString().equals(food.getName()) && viewHolder.editPrice.getText().toString().equals(food.getPrice()) && !loadImageSuccess) {
                    Toast.makeText(mContext, "Are you sure you've changed something?", Toast.LENGTH_LONG).show();
                }

                if (bmpMenu != null && loadImageSuccess) {

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmpMenu.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    final ParseFile newImage = new ParseFile(selectedImage.getLastPathSegment() + ".jpg", stream.toByteArray());

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Food");
                    query.getInBackground(food.getId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                Toast.makeText(mContext, "Uploading...", Toast.LENGTH_LONG).show();
                                object.put("resImage", newImage);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(mContext, "Image Saved", Toast.LENGTH_LONG).show();
                                            Picasso.with(mContext).load(selectedImage).error(R.drawable.cheese_1).into(viewHolder.foodImage);
                                            loadImageSuccess = false;
                                        } else {
                                            Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }

                if (viewHolder.editDesc.length() > 0 && !viewHolder.editDesc.getText().toString().equals(food.getDesc())) {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Food");
                    query.getInBackground(food.getId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                object.put("resDescription", viewHolder.editDesc.getText().toString());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(mContext, "Description Saved", Toast.LENGTH_SHORT).show();
                                            food.setDesc(viewHolder.editDesc.getText().toString());
                                            viewHolder.foodDesc.setText(viewHolder.editDesc.getText().toString());
                                        } else {
                                            Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }

                if (viewHolder.editName.length() > 0 && !viewHolder.editName.getText().toString().equals(food.getName())) {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Food");
                    query.getInBackground(food.getId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                object.put("resName", viewHolder.editName.getText().toString());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(mContext, "Name Saved", Toast.LENGTH_SHORT).show();
                                            food.setName(viewHolder.editName.getText().toString());
                                            viewHolder.foodName.setText(viewHolder.editName.getText().toString());
                                        } else {
                                            Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }

                if (viewHolder.editPrice.length() > 0 && !viewHolder.editPrice.getText().toString().equals(food.getPrice())) {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Food");
                    query.getInBackground(food.getId(), new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                object.put("resPrice", viewHolder.editPrice.getText().toString());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(mContext, "Price Saved", Toast.LENGTH_SHORT).show();
                                            food.setPrice(viewHolder.editPrice.getText().toString());
                                            viewHolder.foodPrice.setText("₱ " + viewHolder.editPrice.getText().toString());
                                        } else {
                                            Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(mContext, "Oops! Something went wrong. Please try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });

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
                                        if (admin.getObjectId().equals(MenuActivity.restoId)) {
                                            viewHolder.editMenu.setVisibility(View.VISIBLE);
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
        public ImageView editMenu;
        public ImageView menuSave;
        public ImageView changeImage;
        public ImageView editClose;
        public CardView foodCard;
        public EditText editName;
        public EditText editDesc;
        public EditText editPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            editName = (EditText)itemView.findViewById(R.id.edit_name);
            editDesc = (EditText)itemView.findViewById(R.id.edit_description);
            editPrice = (EditText)itemView.findViewById(R.id.edit_price);
            foodCard = (CardView)itemView.findViewById(R.id.carditem_container);
            foodName = (TextView)itemView.findViewById(R.id.foodName);
            foodDesc = (TextView)itemView.findViewById(R.id.foodDesc);
            foodPrice = (TextView)itemView.findViewById(R.id.foodPrice);
            foodImage = (ImageView)itemView.findViewById(R.id.foodImage);
            editMenu = (ImageView)itemView.findViewById(R.id.editMenu);
            menuSave = (ImageView)itemView.findViewById(R.id.menu_save);
            changeImage = (ImageView)itemView.findViewById(R.id.change_image);
            editClose = (ImageView)itemView.findViewById(R.id.edit_close);
        }
    }
}

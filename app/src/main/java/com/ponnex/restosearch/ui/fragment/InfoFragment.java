package com.ponnex.restosearch.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.model.LatLng;
import com.ponnex.restosearch.R;
import com.ponnex.restosearch.ui.activity.RestoActivity;

import java.util.ArrayList;

/**
 * Created by ponne on 1/24/2016.
 */
public class InfoFragment extends Fragment implements RoutingListener {

    protected LatLng start;
    protected LatLng end;

    private String duration;
    private String distance;

    private TextView restoTextDuration;

    private TextView restoTextDistance;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        start = new LatLng(8.451690, 124.624713);
        end = new LatLng(Double.parseDouble(RestoActivity.coordLat), Double.parseDouble(RestoActivity.coordLong));

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(start, end)
                .build();
        routing.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_resto, container, false);

        TextView restoTextDesc = (TextView)view.findViewById(R.id.restoDesc);
        restoTextDesc.setText(RestoActivity.restoDesc);

        TextView restoTextAdd = (TextView)view.findViewById(R.id.restoAddress);
        restoTextAdd.setText(RestoActivity.restoAdd);

        restoTextDuration = (TextView)view.findViewById(R.id.restoDuration);
        restoTextDistance= (TextView)view.findViewById(R.id.restoDistance);

        return view;
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        for (int i = 0; i <route.size(); i++) {
            //Toast.makeText(getActivity(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
            duration = String.valueOf(route.get(i).getDurationValue() / 60);
            distance = String.valueOf(route.get(i).getDurationValue());
        }
        restoTextDuration.setText(duration + " min");
        restoTextDistance.setText(distance + " km");
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingCancelled() {
        Log.i("RestoActivity", "Routing was cancelled.");
    }
}
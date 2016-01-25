package com.ponnex.restosearch.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ponnex.restosearch.R;
import com.ponnex.restosearch.ui.activity.RestoActivity;

/**
 * Created by ponne on 1/24/2016.
 */
public class InfoFragment extends Fragment {

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_resto, container, false);
        RestoActivity restoActivity = (RestoActivity) getActivity();

        TextView restoTextDesc = (TextView)view.findViewById(R.id.restoDesc);
        restoTextDesc.setText(restoActivity.getrestoDesc());

        TextView restoTextAdd = (TextView)view.findViewById(R.id.restoAddress);
        restoTextAdd.setText(restoActivity.getrestoAdd());
        return view;
    }
}
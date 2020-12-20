package com.example.iffath.icu.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.iffath.icu.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
    MaterialButton help,live,devices;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        help = view.findViewById(R.id.help_button);
        live = view.findViewById(R.id.live_feed);
        devices = view.findViewById(R.id.devices);
        return view;
    }
}
package com.example.iffath.icu.Fragment;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.work.Logger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.iffath.icu.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment implements View.OnClickListener {
    MaterialButton viewLiveBtn;
    TextView contact_count,camera_error,alert_count;
    ImageView connected;
    View v;
    int port = -1;
    String ipAddress = "";
    String cameraIP= "rtsp://112.135.254.157:8080/video";
    boolean connectionStatus = false;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        v = view;
        //hooks
        viewLiveBtn = view.findViewById(R.id.home_view_live_btn);
        camera_error = view.findViewById(R.id.home_camera_error);
        connected = view.findViewById(R.id.home_camera_indicator);
        contact_count = view.findViewById(R.id.home_contacts_count);
        alert_count = view.findViewById(R.id.home_alert_count);
        viewLiveBtn.setEnabled(false);

        extractPortAndIP();
        validateIP();

        viewLiveBtn.setOnClickListener(this);
        return view;
    }

    private void validateIP() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ipAddress, port), 1000);
                    socket.close();
                    connectionStatus = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    connectionStatus = false;
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            setErrorMessage();
        }
    }

    private void extractPortAndIP(){
        if(cameraIP!=null) {
            port = Integer.parseInt(cameraIP.substring(cameraIP.lastIndexOf(":") + 1, cameraIP.lastIndexOf("/")));
            if (cameraIP.contains("@")) {
                ipAddress = cameraIP.substring(cameraIP.indexOf("@")+1, cameraIP.lastIndexOf(":"));
            } else {
                ipAddress = cameraIP.substring(7, cameraIP.lastIndexOf(":"));
            }
        }

    }

    private void setErrorMessage(){
        viewLiveBtn.setEnabled(true);
        if(cameraIP==null) {
            camera_error.setText("You have not configured your IP camera to view surveillance. Please setup your device so we can help you");
        }else if(!connectionStatus){
            camera_error.setText("There was a problem with your connection please check if your device is connected to the network.");
        }else{
            camera_error.setVisibility(View.INVISIBLE);
            connected.setImageResource(R.drawable.connected);
        }
    }


    @Override
    public void onClick(View view) {
        if(cameraIP== null){
            Toasty.error(getContext(),"Please setup your device to view surveillance", Toasty.LENGTH_SHORT).show();
        }
        else if(!connectionStatus){
            Toasty.error(getContext(),"An error occurred.Please check you camera settings.",Toasty.LENGTH_SHORT).show();
        }
        else{
            NavDirections action = HomeFragmentDirections.actionNavigationHomeToSurveillanceFragment();
            Navigation.findNavController(v).navigate(action);
        }
    }
}
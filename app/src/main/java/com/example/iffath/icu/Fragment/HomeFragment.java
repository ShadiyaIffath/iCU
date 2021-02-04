package com.example.iffath.icu.Fragment;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
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
import android.widget.Toast;
import android.widget.VideoView;

import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.DTO.Response.HomeResponse;
import com.example.iffath.icu.Model.Camera;
import com.example.iffath.icu.Model.RTSP;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.AccountService;
import com.example.iffath.icu.Service.CameraService;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;

public class HomeFragment extends Fragment implements View.OnClickListener, ResponseCallback {
    ImageButton btnHomeSurveillance;
    TextView contact_count,home_camera_txt,alert_count,home_connection_text;
    ImageView connected;
    CardView home_alert_layout,home_contacts_layout;
    View view;

    AccountService accountService;
    SharedPreferenceManager preferenceManager;

    boolean connectionStatus,hasConnection,isArmed = false;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        preferenceManager = SharedPreferenceManager.getInstance(getContext());
        accountService = new AccountService(getContext());

        //hooks
        btnHomeSurveillance = view.findViewById(R.id.btnHomeSurveillance);
        home_camera_txt = view.findViewById(R.id.home_camera_txt);
        connected = view.findViewById(R.id.home_camera_indicator);
        home_connection_text = view.findViewById(R.id.home_connection_text);
        contact_count = view.findViewById(R.id.home_contacts_count);
        alert_count = view.findViewById(R.id.home_alert_count);
        home_contacts_layout = view.findViewById(R.id.home_contacts_layout);
        home_alert_layout = view.findViewById(R.id.home_alert_layout);

        if(preferenceManager.HasConnection()){
            btnHomeSurveillance.setEnabled(false);
            hasConnection = true;
            validateIP(preferenceManager.GetCamera().getRtsp_address());
        }
        setConnectionDetails();
        btnHomeSurveillance.setOnClickListener(this);
        accountService.HomeDetails(preferenceManager.GetLoggedInUserId(), this);
        return view;
    }

    @Override
    public void onClick(View view) {
        NavDirections action;
        if((hasConnection && !connectionStatus) || (!hasConnection && !connectionStatus)){
            action = HomeFragmentDirections.actionNavigationHomeToDeviceSetupFragment(true);
        }
        else{
             action = HomeFragmentDirections.actionNavigationHomeToSurveillanceFragment();
        }
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onSuccess(Response response) {
        HomeResponse homeResponse = (HomeResponse)response.body();
        if(homeResponse!= null) {
            preferenceManager.ArmDevice(homeResponse.isArmed());
            isArmed = homeResponse.isArmed();
            contact_count.setText(String.valueOf(homeResponse.getContacts()));
            alert_count.setText(String.valueOf(homeResponse.getBurglarAlerts()));
        }
    }

    @Override
    public void onError(String errorMessage) {
    }

    private void validateIP(String rtsp_address) {
        RTSP rtsp = extractPortAndIP(rtsp_address);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(rtsp.getIpAddress(), rtsp.getPort()), 1000);
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
        }
    }

    private RTSP extractPortAndIP(String rtspAddress) {
        int port = -1;
        String ipAddress = "";
        port = Integer.parseInt(rtspAddress.substring(rtspAddress.lastIndexOf(":") + 1, rtspAddress.lastIndexOf("/")));
        if (rtspAddress.contains("@")) {
            ipAddress = rtspAddress.substring(rtspAddress.indexOf("@") + 1, rtspAddress.lastIndexOf(":"));
        } else {
            ipAddress = rtspAddress.substring(7, rtspAddress.lastIndexOf(":"));
        }
        return new RTSP(ipAddress, port);
    }

    private void setConnectionDetails(){
        btnHomeSurveillance.setEnabled(true);
        if((hasConnection && !connectionStatus) || (!hasConnection && !connectionStatus)){
            home_connection_text.setText("Disconnected");
        }
        else{
            home_camera_txt.setText("View Surveillance");
            home_connection_text.setText("Connected");
            connected.setImageResource(R.drawable.circle);
        }
    }

}
package com.example.iffath.icu.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.Model.Camera;
import com.example.iffath.icu.Model.RTSP;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.CameraService;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.net.InetSocketAddress;
import java.net.Socket;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;

public class DeviceFragment extends Fragment implements View.OnClickListener {
    MaterialButton btnDeviceArm, btn_add_device;
    ImageButton btnDeleteDevice, btnDeviceEdit;
    TextView device_model_title, device_model_txt, device_rtsp_title, device_rtsp_txt, device_armed_title, device_armed_txt,no_device_text;
    ImageView device_connectivity,no_device_image;
    CardView layout;
    AppCompatImageView device_image;
    View view;

    int accountId;
    String no_device;
    boolean hasConnection,isConnected = false;
    CameraService cameraService;
    Camera camera;
    SharedPreferenceManager preferenceManager;
    ResponseCallback deleteCameraCallback,armDeviceCallback;

    public DeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_device, container, false);
        preferenceManager = SharedPreferenceManager.getInstance(getContext());
        cameraService = new CameraService(getContext());
        accountId = preferenceManager.GetLoggedInUserId();
        hasConnection = preferenceManager.HasConnection();
        no_device = getContext().getString(R.string.notification);
        createCallbacks();
        //hooks
        layout = view.findViewById(R.id.device_info);
        device_image = view.findViewById(R.id.device_image);

        btnDeleteDevice = view.findViewById(R.id.btnDeleteDevice);
        btnDeviceArm = view.findViewById(R.id.btnDeviceArm);
        btnDeviceEdit = view.findViewById(R.id.btnDeviceEdit);
        btn_add_device = view.findViewById(R.id.btn_add_device);

        device_model_title = view.findViewById(R.id.device_model_title);
        device_model_txt = view.findViewById(R.id.device_model_txt);
        device_rtsp_title = view.findViewById(R.id.device_rtsp_title);
        device_rtsp_txt = view.findViewById(R.id.device_rtsp_txt);
        device_armed_title = view.findViewById(R.id.device_armed_title);
        device_armed_txt = view.findViewById(R.id.device_armed_txt);
        no_device_text = view.findViewById(R.id.no_device_text);

        device_connectivity = view.findViewById(R.id.device_connectivity);
        no_device_image = view.findViewById(R.id.no_device_image);

        if(hasConnection) {
            loadCameraDetails();
            setDevice_connectivity();
            toggleVisibility(true);
        }else{
            toggleVisibility(false);
        }

        btn_add_device.setOnClickListener(this);
        btnDeviceEdit.setOnClickListener(this);
        btnDeleteDevice.setOnClickListener(this);
        btnDeviceArm.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnDeleteDevice:
                deleteDevice();
                break;

            case R.id.btnDeviceEdit:

            case R.id.btn_add_device:
                navigateToSetupFragment();
                break;

            case R.id.btnDeviceArm:
                armDevice();
                break;
        }
    }

    private void loadCameraDetails(){
        camera = preferenceManager.GetCamera();
        String armed = camera.isArmed() ? "True" : "False";
        device_armed_txt.setText(armed);
        device_model_txt.setText(camera.getModel());
        device_rtsp_txt.setText(camera.getRtsp_address());
    }

    private void navigateToSetupFragment(){
        NavDirections action = DeviceFragmentDirections.actionNavigationDevicesToDeviceSetupFragment(false);
        Navigation.findNavController(view).navigate(action);
    }

    private void setDevice_connectivity(){
        if(testConnection(camera.getRtsp_address())==1){
            device_connectivity.setImageResource(R.drawable.connected);
            isConnected = true;
            if(!camera.isArmed()){
                btnDeviceArm.setVisibility(View.VISIBLE);
            }
        }else{
            isConnected = false;
            device_connectivity.setImageResource(R.drawable.disconnected);
        }
    }

    private void createCallbacks(){
        deleteCameraCallback = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                Toasty.success(getContext(),"Your IP camera has been successfully removed", Toasty.LENGTH_SHORT).show();
                preferenceManager.clearDeviceSettings();

                device_armed_txt.setText("");
                device_model_txt.setText("");
                device_rtsp_txt.setText("");
                device_connectivity.setImageResource(R.drawable.disconnected);
                toggleVisibility(false);

                hasConnection = false;
                isConnected = false;
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
            }
        };

        armDeviceCallback = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                Toasty.success(getContext(),"You are secured", Toasty.LENGTH_SHORT).show();
                preferenceManager.ArmDevice(true);
                loadCameraDetails();
                btnDeviceArm.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void deleteDevice(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setTitle("Delete Device");
        mBuilder.setMessage("Are you sure you want to delete device details?");
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        cameraService.DeleteCamera(camera.getId(),deleteCameraCallback);
                    }
                });
        mBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog mAlertDialog = mBuilder.create();
        mAlertDialog.show();
    }

    private int testConnection(String rtsp_address){
        RTSP rtsp = extractPortAndIPAddress(rtsp_address);
        if(rtsp == null){
            return 0;
        }
        final int[] connectionStatus = {0};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(rtsp.getIpAddress(), rtsp.getPort()), 1000);
                    socket.close();
                    connectionStatus[0] = 1;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    connectionStatus[0] = -1;
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            connectionStatus[0] = -1;
        }
        return connectionStatus[0];
    }

    private RTSP extractPortAndIPAddress(String rtsp) {
        int port = -1;
        String ipAddress = "";
        port = Integer.parseInt(rtsp.substring(rtsp.lastIndexOf(":") + 1, rtsp.lastIndexOf("/")));
        if (rtsp.contains("@")) {
            ipAddress = rtsp.substring(rtsp.indexOf("@") + 1, rtsp.lastIndexOf(":"));
        } else {
            ipAddress = rtsp.substring(7, rtsp.lastIndexOf(":"));
        }
        return new RTSP(ipAddress, port);
    }

    private void armDevice(){
        if(preferenceManager.HasConnection() && isConnected){
            cameraService.ArmCamera(camera.getAccount_id(),armDeviceCallback);
        }else{
            Toasty.error(getContext(),"Please make sure your device is connected to the network.",Toasty.LENGTH_SHORT).show();
        }
    }

    private void toggleVisibility(boolean edit){
        if(edit){
            layout.setVisibility(View.VISIBLE);
            device_image.setVisibility(View.VISIBLE);
            btn_add_device.setVisibility(View.INVISIBLE);
            no_device_image.setVisibility(View.INVISIBLE);
            no_device_text.setVisibility(View.INVISIBLE);
            if(!camera.isArmed()){
                btnDeviceArm.setVisibility(View.VISIBLE);
            }
        }else{
            layout.setVisibility(View.INVISIBLE);
            device_image.setVisibility(View.INVISIBLE);
            Picasso.get()
                    .load(no_device)
                    .fit()
                    .centerCrop()
                    .into(no_device_image);
            no_device_image.setVisibility(View.VISIBLE);
            no_device_text.setVisibility(View.VISIBLE);
            btn_add_device.setVisibility(View.VISIBLE);
        }
    }

}
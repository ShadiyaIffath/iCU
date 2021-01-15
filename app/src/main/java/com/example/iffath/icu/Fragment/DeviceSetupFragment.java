package com.example.iffath.icu.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.DTO.Request.CameraRequest;
import com.example.iffath.icu.Model.Camera;
import com.example.iffath.icu.Model.RTSP;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.CameraService;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.net.InetSocketAddress;
import java.net.Socket;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;

public class DeviceSetupFragment extends Fragment implements View.OnClickListener {
    MaterialButton btnDeviceCancel,btnDeviceConfirm;
    TextInputLayout device_model,device_rtsp;
    View view;

    SharedPreferenceManager preferenceManager;
    ResponseCallback updateCameraCallback, setupCallBack;
    int accountId;
    boolean hasConnection,isHome= false;

    CameraService cameraService;
    CameraRequest cameraRequest;
    Camera camera;

    public DeviceSetupFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_device_setup, container, false);
        preferenceManager = SharedPreferenceManager.getInstance(getContext());
        cameraService = new CameraService(getContext());
        accountId = preferenceManager.GetLoggedInUserId();
        hasConnection = preferenceManager.HasConnection();
        createCallbacks();

        btnDeviceCancel = view.findViewById(R.id.btnDeviceCancel);
        btnDeviceConfirm = view.findViewById(R.id.btnDeviceConfirm);

        device_model = view.findViewById(R.id.device_model);
        device_rtsp = view.findViewById(R.id.device_rtsp);

        //data retrieval
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            isHome = bundle.getBoolean("isHome");
        }

        if(hasConnection){
            loadCameraDetails();
        }

        btnDeviceCancel.setOnClickListener(this);
        btnDeviceConfirm.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnDeviceCancel:
                navigateBack();
                break;

            case R.id.btnDeviceConfirm:
                if(hasConnection){
                    updateConnection();
                }else{
                    setupConnection();
                }
                break;
        }
    }

    private void loadCameraDetails(){
        camera = preferenceManager.GetCamera();

        device_model.getEditText().setText(camera.getModel());
        device_rtsp.getEditText().setText(camera.getRtsp_address());
    }

    private void createCallbacks() {
        updateCameraCallback = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                Toasty.success(getContext(), "Your IP device has been successfully updated", Toasty.LENGTH_SHORT).show();
                preferenceManager.UpdateDeviceDetails(cameraRequest);
                navigateBack();
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
                device_model.getEditText().setText(camera.getModel());
                device_rtsp.getEditText().setText(camera.getRtsp_address());
            }
        };

        setupCallBack = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                Camera cameraResponse = (Camera) response.body();
                preferenceManager.StoreDeviceDetails(cameraResponse);
                Toasty.success(getContext(), "Your IP device has been successfully setup", Toasty.LENGTH_SHORT).show();
                navigateBack();
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void navigateBack() {
        NavDirections action;
        if(isHome){
            action = DeviceSetupFragmentDirections.actionDeviceSetupFragmentToNavigationHome();
        }else {
            action = DeviceSetupFragmentDirections.actionDeviceSetupFragmentToNavigationDevices();
        }
        Navigation.findNavController(view).navigate(action);
    }

    private CameraRequest extractFieldValues(){
        String model = device_model.getEditText().getText().toString();
        String rtsp = device_rtsp.getEditText().getText().toString();
        if(model.isEmpty() || rtsp.isEmpty()){
            if(model.isEmpty()){
                device_model.setError("Camera model cannot be blank");
            }
            if(rtsp.isEmpty()){
                device_rtsp.setError("RTSP field cannot be blank");
            }
            Toasty.error(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return null;
        }else {
            device_rtsp.setError(null);
            device_model.setError(null);
            return new CameraRequest(model, rtsp, accountId,false);
        }
    }

    private void updateConnection(){
        cameraRequest = extractFieldValues();
        if(cameraRequest != null) {
            int result = testConnection(cameraRequest.getRtsp_address());
            if(result==1){
                cameraService.UpdateCamera(cameraRequest, camera.getId(), updateCameraCallback);
            }else if(result == -1){
                Toasty.error(getContext(),"Please make sure device is connected to the network",Toasty.LENGTH_SHORT).show();
            }
        }
    }
    private void setupConnection(){
        cameraRequest = extractFieldValues();
        if(cameraRequest != null) {
            int result = testConnection(cameraRequest.getRtsp_address());
            if(result==1){
                cameraService.SetupCamera(cameraRequest, setupCallBack);
            }else if(result == -1){
                Toasty.error(getContext(),"Please make sure device is connected to the network",Toasty.LENGTH_SHORT).show();
            }
        }
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

    private RTSP extractPortAndIPAddress(String rtsp){
        int port = -1;
        String ipAddress = "";
        try {
            port = Integer.parseInt(rtsp.substring(rtsp.lastIndexOf(":") + 1, rtsp.lastIndexOf("/")));
            if (rtsp.contains("@")) {
                ipAddress = rtsp.substring(rtsp.indexOf("@") + 1, rtsp.lastIndexOf(":"));
            } else {
                ipAddress = rtsp.substring(7, rtsp.lastIndexOf(":"));
            }
            return new RTSP(ipAddress,port);
        } catch(Exception ex){
            device_rtsp.setError("Invalid RTSP format");
            Toasty.error(getContext(),"Invalid RTSP address format. Please follow the format.",Toasty.LENGTH_SHORT).show();
            return null;
        }
    }

}
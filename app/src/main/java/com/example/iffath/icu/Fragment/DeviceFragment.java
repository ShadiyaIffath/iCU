package com.example.iffath.icu.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.DTO.Request.CameraRequest;
import com.example.iffath.icu.DTO.Response.CameraResponse;
import com.example.iffath.icu.DTO.Response.MessageResponse;
import com.example.iffath.icu.Model.Camera;
import com.example.iffath.icu.Model.RTSP;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.CameraService;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;

public class DeviceFragment extends Fragment implements View.OnClickListener {
    MaterialButton btnDeleteDevice,btnDeviceCancel,btnDeviceConfirm,test_connection,btnDeviceArm;
    TextView test_result;
    ImageView device_connectivity;
    TextInputLayout device_model,device_rtsp;

    int accountId;
    boolean hasConnection,isConnected = false;
    CameraService cameraService;
    Camera camera;
    CameraRequest cameraRequest;
    SharedPreferenceManager preferenceManager;
    ResponseCallback deleteCameraCallback, updateCameraCallback, setupCallBack,armDeviceCallback;

    public DeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        preferenceManager = SharedPreferenceManager.getInstance(getContext());
        cameraService = new CameraService();
        accountId = preferenceManager.GetLoggedInUserId();
        hasConnection = preferenceManager.HasConnection();
        createCallbacks();
        //hooks
        btnDeleteDevice = view.findViewById(R.id.btnDeleteDevice);
        btnDeviceCancel = view.findViewById(R.id.btnDeviceCancel);
        btnDeviceConfirm = view.findViewById(R.id.btnDeviceConfirm);
        btnDeviceArm = view.findViewById(R.id.btnDeviceArm);
        test_connection = view.findViewById(R.id.test_connection);
        test_result = view.findViewById(R.id.test_result);
        device_connectivity = view.findViewById(R.id.device_connectivity);
        device_model = view.findViewById(R.id.device_model);
        device_rtsp = view.findViewById(R.id.device_rtsp);

        if(hasConnection) {
            loadCameraDetails();
            btnDeleteDevice.setVisibility(View.VISIBLE);
        }

        btnDeleteDevice.setOnClickListener(this);
        btnDeviceCancel.setOnClickListener(this);
        btnDeviceConfirm.setOnClickListener(this);
        test_connection.setOnClickListener(this);
        btnDeviceArm.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnDeleteDevice:
                deleteDevice();
                break;

            case R.id.btnDeviceCancel:
                cancelChanges();
                break;

            case R.id.btnDeviceConfirm:
                if(hasConnection){
                    updateConnection();
                }else{
                    setupConnection();
                }
                break;

            case R.id.test_connection:
                testRtsp();
                break;

            case R.id.btnDeviceArm:
                armDevice();
                break;
        }
    }

    private void loadCameraDetails(){
        camera = preferenceManager.GetCamera();
        device_model.getEditText().setText(camera.getModel());
        device_rtsp.getEditText().setText(camera.getRtsp_address());
        btnDeviceConfirm.setEnabled(true);
        if(testConnection(camera.getRtsp_address())==1){
            device_connectivity.setImageResource(R.drawable.connected);
            isConnected = true;
            if(camera.isArmed()){
                btnDeviceConfirm.setEnabled(false);
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
                Toasty.success(getContext(),"Your IP device has been successfully removed", Toasty.LENGTH_SHORT).show();
                preferenceManager.clearDeviceSettings();
                device_model.getEditText().setText("");
                device_rtsp.getEditText().setText("");

                device_connectivity.setImageResource(R.drawable.disconnected);
                hasConnection = preferenceManager.HasConnection();
                isConnected = false;
                btnDeleteDevice.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
            }
        };

        updateCameraCallback = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                Toasty.success(getContext(), "Your IP device has been successfully updated", Toasty.LENGTH_SHORT).show();
                MessageResponse messageResponse = (MessageResponse) response.body();
                device_connectivity.setImageResource(R.drawable.connected);
                isConnected = true;
                preferenceManager.UpdateDeviceDetails(cameraRequest);
                camera = preferenceManager.GetCamera();
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
                device_connectivity.setImageResource(R.drawable.connected);
                preferenceManager.StoreDeviceDetails(cameraResponse);
                Toasty.success(getContext(), "Your IP device has been successfully setup", Toasty.LENGTH_SHORT).show();
                btnDeleteDevice.setVisibility(View.VISIBLE);
                hasConnection = true;
                camera = preferenceManager.GetCamera();
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
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void cancelChanges(){
        test_result.setVisibility(View.INVISIBLE);
        if(preferenceManager.HasConnection()) {
            device_model.getEditText().setText(camera.getModel());
            device_rtsp.getEditText().setText(camera.getRtsp_address());
        }else{
            device_model.getEditText().setText("");
            device_rtsp.getEditText().setText("");
        }
        device_model.setError(null);
        device_rtsp.setError(null);
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
                        test_result.setVisibility(View.INVISIBLE);
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

    private CameraRequest extractFieldValues(){
        test_result.setVisibility(View.INVISIBLE);
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
                cameraRequest.setArmed(camera.isArmed());
                if(result==1){
                    cameraService.UpdateCamera(cameraRequest, camera.getId(), updateCameraCallback);
                }else if(result == -1){
                    Toasty.error(getContext(),"Please make sure device is connected to the network",Toasty.LENGTH_SHORT).show();
                }
            }
    }
    private void setupConnection(){
        cameraRequest = extractFieldValues();
        cameraRequest.setArmed(false);
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
                    socket.connect(new InetSocketAddress(rtsp.getRtsp(), rtsp.getPort()), 1000);
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
            Toasty.error(getContext(),"Invalid RTSP address format. Please follow the format",Toasty.LENGTH_SHORT).show();
            return null;
        }
    }

    private void testRtsp(){
        String rtsp = device_rtsp.getEditText().getText().toString();
        device_rtsp.setError(null);
        device_model.setError(null);
        if(!rtsp.isEmpty()) {
            test_result.setText("");
            test_result.setVisibility(View.VISIBLE);
            int result = testConnection(rtsp);
            if (result == 1) {
                test_result.setText("Connection established with the server");
            } else {
                test_result.setText("Connection failed to be established with the server");
            }
        }else{
            device_rtsp.setError("RTSP field cannot be blank");
            Toasty.error(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void armDevice(){
        if(preferenceManager.HasConnection() && isConnected){
            cameraService.ArmCamera(camera.getAccount_id(),armDeviceCallback);
        }else{
            Toasty.error(getContext(),"A camera with stable connection required",Toasty.LENGTH_SHORT).show();
        }
    }

}
package com.example.iffath.icu.Fragment;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.iffath.icu.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment implements View.OnClickListener {
    VideoView video;
    ProgressDialog mDialog;
    ImageButton playBtn,pauseBtn,fullscreen_btn;
    TextView  errorMsg;

    String cameraIP;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //hooks
        video = view.findViewById(R.id.home_live);
        playBtn = view.findViewById(R.id.play_btn);
        pauseBtn = view.findViewById(R.id.pause_btn);
        fullscreen_btn = view.findViewById(R.id.fullscreen_btn);
        errorMsg = view.findViewById(R.id.home_error);

        getIPAddress();

        playBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        fullscreen_btn.setOnClickListener(this);
        return view;
    }

    private void getIPAddress(){
        //TODO: get ip address from preference manager
        if(cameraIP==null) {
            errorMsg.setText("You have not configured your IP camera to view surveillance. Setup your device in the application.");
            playBtn.setEnabled(false);
            fullscreen_btn.setEnabled(false);
            pauseBtn.setEnabled(false);
        }
    }
    

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.play_btn:
                playVideo();
                break;
            case R.id.pause_btn:
                pauseVideo();
                break;
            case R.id.fullscreen_btn:
                break;
        }
    }

    private void playVideo(){
        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        try{
            Uri uri = Uri.parse(cameraIP);
            video.setVideoURI(uri);
            errorMsg.setVisibility(View.INVISIBLE);
            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    fullscreen_btn.setEnabled(true);
                }
            });
        }catch (Exception ex){
            mDialog.dismiss();
            Toasty.error(getContext(),"An error Occurred!",Toasty.LENGTH_SHORT).show();
            if(cameraIP==null){
                errorMsg.setText("You have not configured your IP camera to view live surveillance");
            }else{
                errorMsg.setText("Please check if you are using a public IP address");
            }
            errorMsg.setVisibility(View.VISIBLE);
            playBtn.setEnabled(false);
            fullscreen_btn.setEnabled(false);
        }
        video.requestFocus();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mDialog.dismiss();
                mediaPlayer.setLooping(true);
                playBtn.setEnabled(false);
                playBtn.setVisibility(View.INVISIBLE);
                pauseBtn.setVisibility(View.VISIBLE);
                video.start();
            }
        });
    }

    private void pauseVideo(){
        video.pause();
    }
}
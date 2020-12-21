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

import es.dmoral.toasty.Toasty;

public class SurveillanceFragment extends Fragment implements View.OnClickListener {
    VideoView video;
    ProgressDialog mDialog;
    ImageButton playBtn,pauseBtn,fullscreen_btn;

    String cameraIP;
    public SurveillanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surveillance, container, false);
        // Inflate the layout for this fragment
        video = view.findViewById(R.id.surveillance_live);
        playBtn = view.findViewById(R.id.play_btn);
        pauseBtn = view.findViewById(R.id.pause_btn);
        fullscreen_btn = view.findViewById(R.id.fullscreen_btn);

        getIPAddress();

        playBtn.setOnClickListener(this);
        return view;
    }

    private void getIPAddress(){

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
            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    fullscreen_btn.setEnabled(true);
                }
            });
        }catch (Exception ex){
            mDialog.dismiss();
            Toasty.error(getContext(),"An error Occurred!",Toasty.LENGTH_SHORT).show();
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
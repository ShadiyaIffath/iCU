package com.example.iffath.icu.Fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iffath.icu.R;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.material.textfield.TextInputLayout;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;


import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class SurveillanceFragment extends Fragment {
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = false;

    SharedPreferenceManager preferenceManager;
    String cameraIP= "";

    private VLCVideoLayout mVideoLayout = null;
    TextInputLayout ipAddress;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;


    public SurveillanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surveillance, container, false);
        preferenceManager = SharedPreferenceManager.getInstance(getContext());
        cameraIP = preferenceManager.GetCamera().getRtsp_address();

        mVideoLayout = view.findViewById(R.id.surveillance_live);
        ipAddress = view.findViewById(R.id.surveillance_ip);

        final ArrayList<String> args = new ArrayList<>();
        args.add("--vout=android-display");
        args.add("-vvv");
        mLibVLC = new LibVLC(getContext(), args);
        mMediaPlayer = new MediaPlayer(mLibVLC);
        ipAddress.getEditText().setText(cameraIP);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mLibVLC.release();
    }

    @Override
    public void onStart() {
        super.onStart();

        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);

        try {
            final Media media = new Media(mLibVLC, Uri.parse(cameraIP));
            mMediaPlayer.setMedia(media);
            media.release();
            mMediaPlayer.play();
        } catch (Exception e) {
            Toasty.error(getContext(),"An error occurred",Toasty.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mMediaPlayer.stop();
        mMediaPlayer.detachViews();
    }


}
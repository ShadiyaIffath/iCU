package com.example.iffath.icu.Fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.MediaController;

import com.example.iffath.icu.Client.RetrofitClient;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Storage.SharedPreferenceManager;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static org.videolan.libvlc.MediaPlayer.Event.Buffering;
import static org.videolan.libvlc.MediaPlayer.Event.EncounteredError;
import static org.videolan.libvlc.MediaPlayer.Event.Opening;
import static org.videolan.libvlc.MediaPlayer.Event.Playing;
import static org.videolan.libvlc.MediaPlayer.Event.Stopped;

public class FootageFragment extends Fragment  implements IVLCVout.Callback{
    private String url;

    View view;

    private static FootageFragment sInstance;
    private LibVLC mLibVlc;
    private SurfaceView mVideoSurface = null;
    private IVLCVout vlcVout;
    private FrameLayout sdk;
    private MediaPlayer mMediaPlayer = null;
    private Media media;

    private MediaController controller;

    SharedPreferenceManager preferenceManager;
    public FootageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_footage, container, false);
        preferenceManager = SharedPreferenceManager.getInstance(getContext());
        sInstance = this;

//        getActivity().getActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mVideoSurface = view.findViewById(R.id.video_surface);

        ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVlc = new LibVLC(getContext(), args);
        mMediaPlayer = new MediaPlayer(mLibVlc);
        vlcVout = mMediaPlayer.getVLCVout();
        sdk = view.findViewById(R.id.video_surface_frame);

        setup_url();

        controller = new MediaController(getContext());
        controller.setMediaPlayer(playerInterface);
        controller.setAnchorView(mVideoSurface);
        mVideoSurface.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controller.show(8000);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mLibVlc.release();
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void setup_url()
    {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int notificationId = bundle.getInt("notificationId");
            String parameters = "accountId=" + preferenceManager.GetLoggedInUserId() + "&notificationId=" + notificationId;
            url = RetrofitClient.BASE_URL + getContext().getString(R.string.burglar_footage) + parameters;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        vlcVout.setWindowSize(displayMetrics.widthPixels,displayMetrics.heightPixels);
        vlcVout.setVideoView(mVideoSurface);
        vlcVout.attachViews();
        mMediaPlayer.getVLCVout().addCallback(this);
        if(!url.isEmpty()) {
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            media = new Media(mLibVlc, Uri.parse(url));
            mMediaPlayer.setMedia(media);
            media.release();
            mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
                @Override
                public void onEvent(MediaPlayer.Event event) {
                    switch(event.type){
                        case EncounteredError:
                            progressDialog.dismiss();
                            Toasty.info(getContext(),"Error encountered", Toasty.LENGTH_SHORT).show();
                            break;
                        case Buffering:
//                            progressDialog.setMessage("Loading video please wait!");
//                            progressDialog.show();
                            break;
                        case Playing:

                        case Opening:

                        case Stopped:
                            progressDialog.dismiss();
                            break;
                    }
                }
            });
            mMediaPlayer.play();
        }else {
            Toasty.info(getContext(),"No video available", Toasty.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mMediaPlayer.stop();
        mMediaPlayer.getVLCVout().detachViews();
        mMediaPlayer.getVLCVout().removeCallback(this);
    }


    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {
    }
    private MediaController.MediaPlayerControl playerInterface = new MediaController.MediaPlayerControl() {
        public int getBufferPercentage() {
            return 0;
        }

        public int getCurrentPosition() {
            float pos = mMediaPlayer.getPosition();
            return (int)(pos * getDuration());
        }

        public int getDuration() {
            return (int)mMediaPlayer.getLength();
        }

        public boolean isPlaying() {
            return mMediaPlayer.isPlaying();
        }

        public void pause() {
            mMediaPlayer.pause();
        }

        public void seekTo(int pos) {
            mMediaPlayer.setPosition((float)pos / getDuration());
        }

        public void start() {
            mMediaPlayer.play();
        }

        public boolean canPause() {
            return true;
        }

        public boolean canSeekBackward() {
            return true;
        }

        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            return 0;
        }
    };

}


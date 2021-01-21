package com.example.iffath.icu.Fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.Client.RetrofitClient;
import com.example.iffath.icu.DTO.Request.FootageRequest;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.NotificationService;
import com.example.iffath.icu.Storage.SharedPreferenceManager;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;

public class FootageFragment extends Fragment  {
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;
    private String url;

    private VLCVideoLayout mVideoLayout = null;
    View view;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;

    NotificationService notificationService;
    SharedPreferenceManager preferenceManager;
    public FootageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_footage, container, false);
        notificationService = new NotificationService(getContext());
        preferenceManager = SharedPreferenceManager.getInstance(getContext());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            int notificationId = bundle.getInt("notificationId");
            String parameters = "accountId=" + preferenceManager.GetLoggedInUserId() + "&notificationId=" + notificationId;
            url = RetrofitClient.BASE_URL + getContext().getString(R.string.burglar_footage)+ parameters;
            System.out.println(url);
        }

        mVideoLayout = view.findViewById(R.id.surveillance_footage);

        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(getContext(), args);
        mMediaPlayer = new MediaPlayer(mLibVLC);

//        notificationService.GetFootageStream(new FootageRequest(preferenceManager.GetLoggedInUserId(),15), this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);

        try {
            final Media media = new Media(mLibVLC, Uri.parse(url));
            mMediaPlayer.setMedia(media);
            media.release();
            mMediaPlayer.play();
        } catch (Exception e) {
            Toasty.error(getContext(),"An error occurred",Toasty.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mLibVLC.release();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMediaPlayer.stop();
        mMediaPlayer.detachViews();
    }
}
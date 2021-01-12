package com.example.iffath.icu.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iffath.icu.Adapter.NotificationAdapter;
import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.Model.Notification;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.Interface.CustomItemClickListener;
import com.example.iffath.icu.Service.NotificationService;
import com.example.iffath.icu.Storage.SharedPreferenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;

public class NotificationsFragment extends Fragment implements CustomItemClickListener, ResponseCallback {
    List<Notification> notifications;
    NotificationService notificationService;
    RecyclerView recyclerView;

    View view;
    int accountId;
    int pos = 0;
    DateFormat df;
    NotificationAdapter recyclerAdapter;
    SharedPreferenceManager preferenceManager;
    ResponseCallback deleteNotification;
    private final static String pattern = "MM/dd/yyyy HH:mm:ss";

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        notificationService = new NotificationService();
        df = new SimpleDateFormat(pattern);
        createCallBacks();

        preferenceManager = SharedPreferenceManager.getInstance(this.getContext());
        accountId = preferenceManager.GetLoggedInUserId();

        recyclerView = view.findViewById(R.id.notification_recycle);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        notificationService.GetAllNotifications(accountId, this);
        return view;
    }

    @Override
    public void onItemClick(View v, int position) {
        Notification notification = recyclerAdapter.getNotification(pos);
        switch (v.getId()){
            case R.id.btnDelete_msg:
                pos = position;
                notificationService.DeleteNotification(notification.getId(),deleteNotification);
                break;

            case R.id.notification_card:
                pos = position;
                displayMessage(notification);
                break;
        }
    }

    @Override
    public void onSuccess(Response response) {
        notifications = (List<Notification>)response.body();
        if(notifications.size() == 0){
            Toasty.info(getContext(), "You have 0 Notifications", Toasty.LENGTH_SHORT).show();
        }
        else {
            recyclerAdapter = new NotificationAdapter(getContext(), this);
            recyclerAdapter.setAllData(notifications);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String errorMessage) {
        Toasty.error(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void createCallBacks(){
        deleteNotification = new ResponseCallback() {
            @Override
            public void onSuccess(Response response) {
                recyclerAdapter.removeItem(pos);
                Toasty.success(getContext(),"Notification successfully deleted",Toasty.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void displayMessage(Notification m){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this.getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.msg_detail, null);

        //hooks
        TextView notification_title = dialogView.findViewById(R.id.notification_title);
        TextView notification_message = dialogView.findViewById(R.id.notification_message);
        TextView notification_date = dialogView.findViewById(R.id.notification_date);
        Button ok = dialogView.findViewById(R.id.ok_msg);

        //values
        notification_title.setText(m.getTitle());
        notification_date.setText(recyclerAdapter.getLocalDateTimeString(m.getOccurred_on()));
        notification_message.setText(m.getMessage());

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
}
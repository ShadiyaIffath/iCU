package com.example.iffath.icu.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iffath.icu.Model.Notification;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.Interface.CustomItemClickListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private Context context;
    View viewAdp;

    private final static String pattern = "yyyy-mm-dd HH:mm:ss";
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds

    DateFormat df;
    CustomItemClickListener listener;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, CustomItemClickListener listener){
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        viewAdp = inflater.inflate(R.layout.notification_card,parent,false); //create new card for each product
        return new NotificationAdapter.ViewHolder(viewAdp);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = notificationList.get(holder.getAdapterPosition());
        holder.msg_created.setText(getLocalDateTimeString(notification.getOccurred_on()));
        holder.msg_title.setText(notification.getTitle());
        if(notification.getTitle().equals("Burglar Alert!"))
            holder.btnRecording_msg.setVisibility(View.VISIBLE);
        setFadeAnimation(holder.itemView);
        holder.msg_cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onItemClick(view, holder.getAdapterPosition());
                return false;
            }
        });

        holder.btnRecording_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view,holder.getAdapterPosition());
            }
        });
        holder.msg_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMessage(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.notificationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    public String getLocalDateTimeString(String dateTime){
        Date dt = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        try {
            dt = df.parse(dateTime);
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(dt);
        }catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    public void setAllData(List<Notification> notifications) {
        this.notificationList = notifications;
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        this.notificationList.remove(position);
        notifyDataSetChanged();
    }

    public Notification getNotification(int position){
        return notificationList.get(position);
    }


    private void displayMessage(Notification m){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.msg_detail, null);

        //hooks
        TextView notification_title = dialogView.findViewById(R.id.notification_title);
        TextView notification_message = dialogView.findViewById(R.id.notification_message);
        TextView notification_date = dialogView.findViewById(R.id.notification_date);
        Button ok = dialogView.findViewById(R.id.ok_msg);

        //values
        notification_title.setText(m.getTitle());
        notification_date.setText(getLocalDateTimeString(m.getOccurred_on()));
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg_created,msg_title;
        ImageButton btnRecording_msg;
        CardView msg_cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msg_title = itemView.findViewById(R.id.msg_title);
            msg_created = itemView.findViewById(R.id.msg_created);
            btnRecording_msg = itemView.findViewById(R.id.btnRecording_msg);
            msg_cardView = itemView.findViewById(R.id.notification_card);
        }
    }
}

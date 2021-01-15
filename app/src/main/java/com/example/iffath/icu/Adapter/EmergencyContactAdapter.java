package com.example.iffath.icu.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iffath.icu.Fragment.ManageContactsFragmentDirections;
import com.example.iffath.icu.Model.EmergencyContact;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.Interface.CustomItemClickListener;

import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ViewHolder>{
    private Context context;
    View viewAdp;
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds

    CustomItemClickListener listener;
    private List<EmergencyContact> contactList;

    public EmergencyContactAdapter(Context context, CustomItemClickListener listener){
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        viewAdp = inflater.inflate(R.layout.contact_card,parent,false); //create new card for each product
        return new EmergencyContactAdapter.ViewHolder(viewAdp);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final EmergencyContact contact = contactList.get(position);
        holder.contact_name.setText(contact.getName());
        holder.contact_nickname.setText(contact.getNickname());
        setFadeAnimation(holder.itemView);

        holder.call_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(view, holder.getAdapterPosition());
            }
        });

        holder.contact_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = ManageContactsFragmentDirections.actionNavigationContactsToEmergencyContactFragment(contact,true);
                Navigation.findNavController(view).navigate(action);
            }
        });

        holder.contact_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {        //remove clothing item from cart
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
                mBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                mBuilder.setTitle("Delete Contact");
                mBuilder.setMessage("Are you sure you want to delete contact?");
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                listener.onItemClick(v, holder.getAdapterPosition());
                                // Do what is needed if user selects Yes
                            }
                        });
                mBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                // users selected No
                            }
                        });

                AlertDialog mAlertDialog = mBuilder.create();
                mAlertDialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    public void removeItem(int position){
        this.contactList.remove(position);
        notifyItemRemoved(position);
    }

    public void setAllData(List<EmergencyContact> contacts) {
        this.contactList = contacts;
        notifyDataSetChanged();
    }

    public EmergencyContact getContact(int position){ return this.contactList.get(position);}

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contact_name,contact_nickname;
        ImageButton call_contact;
        CardView contact_card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contact_name = itemView.findViewById(R.id.contact_name);
            contact_nickname = itemView.findViewById(R.id.contact_nickname);
            call_contact = itemView.findViewById(R.id.contact_call);
            contact_card = itemView.findViewById(R.id.contact_card);
        }
    }
}

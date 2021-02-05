package com.example.iffath.icu.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iffath.icu.Adapter.EmergencyContactAdapter;
import com.example.iffath.icu.Callback.ResponseCallback;
import com.example.iffath.icu.Model.EmergencyContact;
import com.example.iffath.icu.R;
import com.example.iffath.icu.Service.ContactsService;
import com.example.iffath.icu.Service.Interface.CustomItemClickListener;
import com.example.iffath.icu.Storage.SharedPreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;

public class ManageContactsFragment extends Fragment implements View.OnClickListener, CustomItemClickListener, ResponseCallback {
    List<EmergencyContact> contacts;
    ContactsService contactsService;
    RecyclerView recyclerView;
    FloatingActionButton btnNewContact;
    TextView contacts_title, no_contacts_text;
    ImageView no_contacts_image;

    View view;
    int accountId;
    int pos = 0;
    int size = 0;
    String no_contacts;
    EmergencyContactAdapter recyclerAdapter;
    SharedPreferenceManager preferenceManager;
    ResponseCallback deleteContact;
    public ManageContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_manage_contacts, container, false);
        contactsService = new ContactsService(getContext());
        preferenceManager = SharedPreferenceManager.getInstance(this.getContext());
        accountId = preferenceManager.GetLoggedInUserId();
        contacts = new ArrayList<>();
        no_contacts = getContext().getString(R.string.notification);
        createCallBacks();

        //hooks
        btnNewContact = view.findViewById(R.id.add_contact_btn);
        recyclerView = view.findViewById(R.id.emergency_recycle);
        contacts_title = view.findViewById(R.id.contacts_title);
        no_contacts_image = view.findViewById(R.id.no_contacts_image);
        no_contacts_text = view.findViewById(R.id.no_contacts_text);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        btnNewContact.setOnClickListener(this);

        contactsService.GetAccountContacts(accountId,this);
        return view;
    }

    @Override
    public void onClick(View view) {
        NavDirections action =ManageContactsFragmentDirections.actionNavigationContactsToEmergencyContactFragment(null,false);
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onItemClick(View v, int position) {
        switch (v.getId()){
            case R.id.contact_card:
                pos = position;
                contactsService.DeleteContact(recyclerAdapter.getContact(position).getId(),deleteContact);
                break;

            case R.id.contact_call:
                EmergencyContact contact = recyclerAdapter.getContact(position);
                dialNumber(String.valueOf(contact.getPhone()));
                break;
        }
    }

    @Override
    public void onSuccess(Response response) {
        contacts = (List<EmergencyContact>) response.body();
        size = contacts.size();
        String title = "#"+ size+ " emergency contacts";
        contacts_title.setText(title);
        if(contacts.size() == 0){
            noContactsLayout();
        }else {
            recyclerAdapter = new EmergencyContactAdapter(getContext(), this);
            recyclerAdapter.setAllData(contacts);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String errorMessage) {
        contacts_title.setVisibility(View.INVISIBLE);
        btnNewContact.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        no_contacts_image.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(no_contacts)
                .fit()
                .centerCrop()
                .into(no_contacts_image);
        no_contacts_text.setVisibility(View.VISIBLE);
    }

    private void createCallBacks(){
        deleteContact = new ResponseCallback() {

            @Override
            public void onSuccess(Response response) {
                recyclerAdapter.removeItem(pos);
                size--;
                String title = "#"+ size+ " emergency contacts";
                contacts_title.setText(title);
                if(recyclerAdapter.getItemCount() == 0){
                    noContactsLayout();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void dialNumber(final String contactNumber) { //method which is used to create an intent service to make the call
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contactNumber, null)));
    }

    private void noContactsLayout(){
        recyclerView.setVisibility(View.INVISIBLE);
        no_contacts_image.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(no_contacts)
                .fit()
                .centerCrop()
                .into(no_contacts_image);
        no_contacts_text.setVisibility(View.INVISIBLE);
    }
}
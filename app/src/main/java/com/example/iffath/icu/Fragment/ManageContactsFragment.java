package com.example.iffath.icu.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Response;

public class ManageContactsFragment extends Fragment implements View.OnClickListener, CustomItemClickListener, ResponseCallback {
    List<EmergencyContact> contacts;
    ContactsService contactsService;
    RecyclerView recyclerView;
    FloatingActionButton btnNewContact;
    TextView contacts_title;

    View view;
    int accountId;
    int pos = 0;
    int size = 0;
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
        contactsService = new ContactsService();
        preferenceManager = SharedPreferenceManager.getInstance(this.getContext());
        accountId = preferenceManager.getLoggedInUserId();
        contacts = new ArrayList<>();
        createCallBacks();

        //hooks
        btnNewContact = view.findViewById(R.id.add_contact_btn);
        recyclerView = view.findViewById(R.id.emergency_recycle);
        contacts_title = view.findViewById(R.id.contacts_title);

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
            case R.id.delete_contact:
                pos = position;
                contactsService.DeleteContact(recyclerAdapter.getContact(position).getId(),deleteContact);
                break;

            case R.id.edit_contact:
                EmergencyContact contact = recyclerAdapter.getContact(position);
                NavDirections action = ManageContactsFragmentDirections.actionNavigationContactsToEmergencyContactFragment(contact,true);
                Navigation.findNavController(view).navigate(action);
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
            Toasty.info(getContext(), "0 emergency contacts", Toasty.LENGTH_SHORT).show();
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
        Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
    }

    private void createCallBacks(){
        deleteContact = new ResponseCallback() {

            @Override
            public void onSuccess(Response response) {
                recyclerAdapter.removeItem(pos);
                size--;
                String title = "#"+ size+ " emergency contacts";
                contacts_title.setText(title);
                Toasty.success(getContext(),"Contact successfully deleted",Toasty.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toasty.error(getContext(), "Server error. Try again later", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
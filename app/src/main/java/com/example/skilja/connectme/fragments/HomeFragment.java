package com.example.skilja.connectme.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.skilja.connectme.R;
import com.example.skilja.connectme.model.CustomAdapter;
import com.example.skilja.connectme.model.DeleteGroup;
import com.example.skilja.connectme.model.User;
import com.example.skilja.connectme.model.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ListView myListView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups");
    private DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("deletedGroup");
    private CustomAdapter adapter;
    private List<Group> groups = new ArrayList<>();
    private List<String> deletedGroups = new ArrayList<>();

    private List<Group> forDeleteList = new ArrayList<>();

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ImageButton deleteButton;

    private EditText searchConversation;
    private ArrayList<Group> temp = new ArrayList<>();
    ;
    private ArrayList<Group> listGroup = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        myListView =  view.findViewById(R.id.listConversation1);
        adapter = new CustomAdapter(getContext(), groups);
        myListView.setAdapter(adapter);
        myListView.setDivider(null);
        deleteButton =  view.findViewById(R.id.deleteImageButton);
        searchConversation = view.findViewById(R.id.searchConversationId);


        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String email = getCurrentUserEmail();
                for (DataSnapshot u : dataSnapshot.getChildren()) {
                    DeleteGroup deleteGroup = u.getValue(DeleteGroup.class);
                    if (deleteGroup.getEmailUser().equals(email)) {
                        deletedGroups.add(deleteGroup.getGroupId());
                    }
                }
                for (int i = 0; i < deletedGroups.size(); i++) {
                    Log.d("GRUPE DELETED", "***************" + deletedGroups.get(i));
                }
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //UCITAVANJE IZ BAZE

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groups.clear();
                String email = getCurrentUser();
                Log.d("DATABASE", "FROM DATABASE SKILJA");
                Group g2 = new Group("dsadasd", "Kolege", null, -16711681);
                Group g3 = new Group("5345", "Android team fax", null, -55451 );
                groups.add(g2);
                groups.add(g3);
//                for (DataSnapshot u : dataSnapshot.getChildren()) {
//                    Group group = u.getValue(Group.class);
//                    for (User user : group.getMembers().values()) {
//                        if (user.getEmail().equals(email)) {
//                            groups.add(group);
//
//                        }
//                   }
//                }


//                for (int i = 0; i < groups.size(); i++) {
//                    for (int j = 0; j < deletedGroups.size(); j++) {
//                        if (groups.get(i).getId_group().equals(deletedGroups.get(j)))
//                            forDeleteList.add(groups.get(i));
//                    }
//                }

                groups.removeAll(forDeleteList);
                temp.clear();
                temp.addAll(groups);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        searchConversation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = searchConversation.getText().toString();
                Log.d("PRINT", "KEYWORD IS : " + input);
                if (input.equals("")) {
                    groups.clear();
                    groups.addAll(temp);
                    adapter.notifyDataSetChanged();
                    return;
                } else {
                    listGroup.clear();
                    groups.clear();
                    groups.addAll(temp);
                    for (int i = 0; i < groups.size(); i++) {
                        if (groups.get(i).getTitle().toLowerCase().contains(input)) {
                            listGroup.add(groups.get(i));
                        }
                    }
                    groups.clear();
                    groups.addAll(listGroup);

                    adapter.notifyDataSetChanged();

                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.notifyDataSetChanged();
            }
        });


        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        return view;
    }


    public String getCurrentUserEmail() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        return firebaseUser.getEmail();
    }

    public String getCurrentUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser =  firebaseAuth.getCurrentUser();

        return firebaseUser.getEmail();

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}

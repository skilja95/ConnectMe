package com.example.skilja.connectme.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.skilja.connectme.R;
import com.example.skilja.connectme.model.ContactAdapter;
import com.example.skilja.connectme.model.DeleteGroup;
import com.example.skilja.connectme.model.Group;
import com.example.skilja.connectme.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PeopleFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups");
    private DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("deletedGroup");
    private TextView myText;
    private ListView listContacts;


    private ArrayList<Group> groups = new ArrayList<>();
    private ArrayList<Group> temp = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<User> usersTemp = new ArrayList<>();
    private ArrayList<String> deletedGroups = new ArrayList<>();
    private ArrayList<Group> forDeletedList = new ArrayList<>();
    private List<User> list = new ArrayList<>();
    private List<String> list2 = new ArrayList<>();


    ContactAdapter adapter;


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PeopleFragment() {

    }





    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.fragment_people, container, false);
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        myText = view.findViewById(R.id.textId);
        listContacts = view.findViewById(R.id.listContacts);
        adapter = new ContactAdapter(getContext(), list2);
        listContacts.setAdapter(adapter);
        listContacts.setDivider(null);



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


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                groups.clear();
                String email = getCurrentUserEmail();
                for (DataSnapshot u : dataSnapshot.getChildren()) {


                    Group gr = u.getValue(Group.class);
                    for(User kor : gr.getMembers().values()) {

                        if (kor.getEmail().equals(email)) {

                            groups.add(gr);

                        }

                    }

                }
                for (int i = 0; i < groups.size(); i++) {
                    for (int j = 0; j < deletedGroups.size(); j++) {
                        if (groups.get(i).getId_group().equals(deletedGroups.get(j)))
                            forDeletedList.add(groups.get(i));
                    }
                }




                groups.removeAll(forDeletedList);

                temp.clear();
                temp.addAll(groups);
                goUsers();
                adapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;


    }

    public void goUsers() {

        String email = getCurrentUserEmail();
        //Set<User> hs = new HashSet<>(); // ovo koristim jer cu imati vise usera koji su isti, a set ne dozvoljava ponavljajuce vrednosti

        for(Group gro : groups) {

            for (User kori : gro.getMembers().values()) {

                if (!kori.getEmail().equals(email)) {

                    //hs.add(kori);
                    //users.add(kori);
                    list2.add(kori.getFirst_name()+ " " +kori.getLast_name());

                }


            }



        }

         //list =  new ArrayList<>(new HashSet<>(users));
       // list2 = new ArrayList<>(new HashSet<String>());
        //hs.addAll(users);
        //users.clear();
        //users.addAll(hs);
        //usersTemp.addAll(users);
        Set<String> hs = new HashSet<>();
        hs.addAll(list2);
        list2.clear();
        list2.addAll(hs);

    }


    public String getCurrentUserEmail() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser usr = firebaseAuth.getCurrentUser();

        return usr.getEmail();

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

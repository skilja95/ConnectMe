package com.example.skilja.connectme.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.skilja.connectme.R;
import com.example.skilja.connectme.model.DeleteAdapter;
import com.example.skilja.connectme.model.DeleteGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DeleteMessagesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    private ListView myListView;
    private EditText myEditText;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference("deletedGroup");

    private List<DeleteGroup> groups = new ArrayList<>();
    private List<DeleteGroup> temp = new ArrayList<>();
    private List<DeleteGroup> forAdd = new ArrayList<>();
    private DeleteAdapter adapter;




    public DeleteMessagesFragment() {

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
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_delete_messages, container, false);

        View view = inflater.inflate(R.layout.fragment_delete_messages, container, false);

        myEditText =  view.findViewById(R.id.searchDelConversations);
        myListView = view.findViewById(R.id.listDelConversations);
        adapter = new DeleteAdapter(getContext(), groups);
        myListView.setAdapter(adapter);
        myListView.setDivider(null);



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                groups.clear();
                String email = getCurrentUserEmail();

                for(DataSnapshot u: dataSnapshot.getChildren()) {

                    DeleteGroup group = u.getValue(DeleteGroup.class);
                    if (group.getEmailUser().equalsIgnoreCase(email)) {

                        groups.add(group);


                    }


                }

                temp.addAll(groups);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        myEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                String input = myEditText.getText().toString();

                if (input.equalsIgnoreCase("")) {

                    groups.clear();
                    groups.addAll(temp);
                    adapter.notifyDataSetChanged();
                    return ;


                } else {


                    groups.clear();
                    forAdd.clear();
                    groups.addAll(temp);

                    for (DeleteGroup gr : groups) {

                        String titl = gr.getTitle().toLowerCase();

                        if (titl.contains(input)) {


                            forAdd.add(gr);

                        }

                    }

                    groups.clear();
                    groups.addAll(forAdd);
                    adapter.notifyDataSetChanged();


                }





            }

            @Override
            public void afterTextChanged(Editable editable) {

                adapter.notifyDataSetChanged();

            }
        });



        return view;
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

    public String getCurrentUserEmail() {


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fireBaseUser = firebaseAuth.getCurrentUser();
        return fireBaseUser.getEmail();

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}


package com.example.skilja.connectme.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;

import com.example.skilja.connectme.R;
import com.example.skilja.connectme.model.User;
import com.example.skilja.connectme.model.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 6/10/2018.
 */

public class Pop extends Activity {


    public static final String GROUP_ID = "group_id";
    public static final String USER_ID = "user_id";
    public static final String USER_MAIL = "user_mail";

    private Spinner result;
    private Button create;

    private List<User> users = new ArrayList<>();
    private List<User> listUser = new ArrayList<>();
    private List<User> temp = new ArrayList<>();
    private UserAdapter adapter;
    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups");



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.pop_up_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.6));


        result = (Spinner) findViewById(R.id.spinner);
        create = (Button) findViewById(R.id.add);


        ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot u : dataSnapshot.getChildren()) {
                    Log.d("NESTO ", u.getValue().toString());
                    User user = u.getValue(User.class);
                    users.add(user);
                    Log.e("Conversation", "KEY: " + u.getKey());
                }
                temp.clear();
                temp.addAll(users);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new UserAdapter(Pop.this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        result.setAdapter(adapter);



    }
}

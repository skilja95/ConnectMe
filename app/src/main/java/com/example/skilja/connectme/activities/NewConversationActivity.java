package com.example.skilja.connectme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.skilja.connectme.R;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.skilja.connectme.model.Group;
import com.example.skilja.connectme.model.Message;
import com.example.skilja.connectme.model.User;
import com.example.skilja.connectme.model.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewConversationActivity extends AppCompatActivity {

    public static final String GROUP_ID = "group_id";
    public static final String USER_ID = "user_id";
    public static final String USER_MAIL = "user_mail";

    private EditText searchUser;
    private Spinner result;
    private EditText title;
    private Button create;

    private TextView useri;

    private List<User> users = new ArrayList<>();
    private List<User> listUser = new ArrayList<>();
    private List<User> temp = new ArrayList<>();
    private List<User> tempMembers = new ArrayList<>();
    private UserAdapter adapter;
    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups");
    private User user1;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchUser = (EditText) findViewById(R.id.search);

        useri = (TextView) findViewById(R.id.useriText) ;

        useri.setText("Selected users: ");

        result = (Spinner) findViewById(R.id.result);
        title = (EditText) findViewById(R.id.title);
        create = (Button) findViewById(R.id.createConversation);

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

        adapter = new UserAdapter(NewConversationActivity.this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        result.setAdapter(adapter);

        result.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                User us =  (User) adapterView.getItemAtPosition(i);
                tempMembers.add(us);
                useri.append(us.getFirst_name()+ ", ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(title.getText().toString())) {
                    title.setError("Please, set title.");
                    return;
                }

                String title1 = title.getText().toString();
                String s = result.getSelectedItem().toString();

                List<User> members = new ArrayList<User>();
                //User u = (User) result.getSelectedItem();

                User currentUser = getCurrentUser(temp);
                members.add(currentUser);

                //members.add(u);
                members.addAll(tempMembers);

                //Log.e("", "EMAIL OD : " + currentUser.getEmail());

                String uid = ref.push().getKey();

                HashMap<String, User> map = new HashMap<String, User>();
                for (User user : members) {
                    map.put(user.getFirst_name(), user);
                }
                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color1 = generator.getRandomColor();
                while (color1 == 0) {
                    color1 = generator.getRandomColor();
                }

                Group group = new Group(uid, title1, map, color1);
                Message m = new Message(currentUser.getEmail(), uid, "Welcome people", Long.parseLong("1487853534144"));
                group.getMessages().add(m);

                databaseReference.child(uid).setValue(group);
               // Log.e("", "SELECTED: " + s + u.getEmail());

                String currentUserId = getCurrentUserId();

                Intent intent = new Intent(NewConversationActivity.this, SendMessageActivity.class);
                intent.putExtra(NewConversationActivity.USER_ID, currentUserId);
                intent.putExtra(NewConversationActivity.USER_MAIL, currentUser.getEmail());
                intent.putExtra(NewConversationActivity.GROUP_ID, group.getId_group());
                startActivity(intent);

                FirebaseMessaging.getInstance().subscribeToTopic(group.getId_group());
            }
        });


        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = searchUser.getText().toString().toLowerCase();
                Log.d("ISPIS", "UBACENA REC JE: " + input);

                if (input.equals("")) {
                    users.clear();
                    users.addAll(temp);
                    adapter.notifyDataSetChanged();
                    return;
                } else {
                    listUser.clear();
                    users.clear();
                    users.addAll(temp);
                    for (int i = 0; i < users.size(); i++) {
                        if (users.get(i).getFirst_name().toLowerCase().contains(input)) {
                            listUser.add(users.get(i));
                            Log.d("ISPIS", "PRONASAO REC : " + users.get(i).getFirst_name().toLowerCase());
                        }
                    }
                    users.clear();
                    users.addAll(listUser);

                    Log.d("ISPIS", "UBACENA REC JE: " + users.size());
                    adapter.notifyDataSetChanged();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.notifyDataSetChanged();
            }
        } );

    }

    public User getCurrentUser(List<User> users) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = (FirebaseUser) firebaseAuth.getCurrentUser();
        String email = firebaseUser.getEmail();
        Log.e("", "EMAIL: " + email);
        Log.e("", "SIZE: " + users.size());

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail() != null) {
                Log.e("", "USER: " + users.get(i).getEmail());
                if (users.get(i).getEmail().equals(email)) {
                    return user1 = users.get(i);
                }
            }
        }
        return null;
    }

    public String getCurrentUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = (FirebaseUser) firebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();
        return uid;
    }

}

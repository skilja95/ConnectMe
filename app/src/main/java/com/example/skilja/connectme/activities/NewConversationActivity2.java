package com.example.skilja.connectme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.skilja.connectme.R;
import com.example.skilja.connectme.model.Group;
import com.example.skilja.connectme.model.Message;
import com.example.skilja.connectme.model.User;
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

/**
 * Created by Lenovo on 6/10/2018.
 */

public class NewConversationActivity2 extends AppCompatActivity implements UserDialog.DialogUserCall {


    public static final String GROUP_ID = "group_id";
    public static final String USER_ID = "user_id";
    public static final String USER_MAIL = "user_mail";

    private EditText myEditText;
    private TextView myText;
    private Button createConversation;
    private Button addUser;

    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups");



    private List<User> users = new ArrayList<>();
    private List<User> listUser = new ArrayList<>();
    private List<User> temp = new ArrayList<>();
    private User user1;


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    public void showUserDialog() {
        DialogFragment dialog = new UserDialog();
        dialog.show(getSupportFragmentManager(), "Dialog1");


    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, ArrayList<User> lista) {
        // User touched the dialog's positive button

        ArrayList<User> proba2 = lista;

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        dialog.getDialog().cancel();

    }



    @Override
    protected  void onCreate(Bundle savedInstancestate) {

        super.onCreate(savedInstancestate);
        setContentView(R.layout.activity_new_conversation2);


        myEditText = (EditText) findViewById(R.id.titleConversation);
        myText = (TextView) findViewById(R.id.titleConversation);
        createConversation = (Button) findViewById(R.id.createButton);
        addUser = (Button) findViewById(R.id.addUser);



        ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String email = getCurrentUserEmail();

                for (DataSnapshot u : dataSnapshot.getChildren()) {

                    User user = u.getValue(User.class);

                      /*  if (user.getEmail().equalsIgnoreCase(email)) {

                            user1 = user;
                           // break;

                        }*/

                    temp.add(user);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  startActivity(new Intent(NewConversationActivity2.this, Pop.class));
                showUserDialog();

            }
        });


        createConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(myEditText.getText().toString())) {
                    myEditText.setError("Please, set title.");
                    return;
                }

                if (myText.getText().toString().equalsIgnoreCase("")) {

                    myText.setError("Must add at least one user");
                    return;

                }

                String title = myEditText.getText().toString();

               // String users = myText.getText().toString();

                List<User> members = new ArrayList<User>();

               // User currentUser = getCurrentUser(users);
                members = users;
                members.add(user1);

                String uid = databaseReference.push().getKey();

                HashMap<String, User> map = new HashMap<String, User>();
                for (User user : members) {
                    map.put(user.getFirst_name(), user);
                }
                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color1 = generator.getRandomColor();
                while (color1 == 0) {
                    color1 = generator.getRandomColor();
                }

                Group group = new Group(uid, title, map, color1);
                Message m = new Message(user1.getEmail(), uid, "Welcome people", Long.parseLong("1487853534144"));
                group.getMessages().add(m);


                databaseReference.child(uid).setValue(group);

                String currentUserId = getCurrentUserId();

                Intent intent = new Intent(NewConversationActivity2.this, SendMessageActivity.class);
                intent.putExtra(NewConversationActivity2.USER_ID, currentUserId);
                intent.putExtra(NewConversationActivity2.USER_MAIL, user1.getEmail());
                intent.putExtra(NewConversationActivity2.GROUP_ID, group.getId_group());
                startActivity(intent);

                FirebaseMessaging.getInstance().subscribeToTopic(group.getId_group());


            }
        });



    }


    public String getCurrentUserId() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = (FirebaseUser) firebaseAuth.getCurrentUser();
        String uid = firebaseUser.getUid();
        return uid;
    }


    public String getCurrentUserEmail() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fireBaseUser = (FirebaseUser) firebaseAuth.getCurrentUser();
        String email = fireBaseUser.getEmail();
        return email;

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


}

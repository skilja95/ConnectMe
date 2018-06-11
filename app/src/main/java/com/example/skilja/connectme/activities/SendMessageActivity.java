package com.example.skilja.connectme.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.skilja.connectme.model.Group;
import com.example.skilja.connectme.model.Message;
import com.example.skilja.connectme.model.MessagesViewHolder;
import com.example.skilja.connectme.model.Notifikacija;
import com.example.skilja.connectme.model.User;
import com.example.skilja.connectme.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SendMessageActivity extends AppCompatActivity {

    private DatabaseReference databaseReferenceMessages = FirebaseDatabase.getInstance().getReference("messages");
    private DatabaseReference databaseReferenceGroups = FirebaseDatabase.getInstance().getReference("groups");
    private DatabaseReference databaseReferenceMessagesOfGroup;
    private DatabaseReference databaseNotification = FirebaseDatabase.getInstance().getReference("notifications");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    private Button sendButton;
    private EditText messageText;

    private Message message;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private ChildEventListener childEventListener;

    private String uidGroup;
    private String currentUserId;
    private String currentUserEmail;

    private Group group;

    private FirebaseRecyclerAdapter<Message, MessagesViewHolder> firebaseRecyclerAdapter;

    private List<User> users1;
    private List<User> users;

    private int n = 1;

    private String title = "";
    public static final String GROUP_ID = "group_id";
    public static final String USER_ID = "user_id";
    public static final String USER_MAIL = "user_mail";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);



        currentUserId = getIntent().getStringExtra(SendMessageActivity.USER_ID);
        currentUserEmail = getIntent().getStringExtra(SendMessageActivity.USER_MAIL);
        uidGroup = getIntent().getStringExtra(SendMessageActivity.GROUP_ID);


        databaseReferenceGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot u : dataSnapshot.getChildren()) {
                    Group group1 = u.getValue(Group.class);

                    Log.e("", "GROUP ID" + group1.getId_group());
                    if (group1.getId_group().equals(uidGroup)) {
                        group = group1;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerViewLayoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.listMessages);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        databaseReferenceMessagesOfGroup = FirebaseDatabase.getInstance().getReference("groups").child(uidGroup).child("messages");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessagesViewHolder>(Message.class, R.layout.universal_message, MessagesViewHolder.class, databaseReferenceMessagesOfGroup) {

            @Override
            protected void populateViewHolder(MessagesViewHolder viewHolder, Message model, int position) {
                if (model.getEmailUser().equals(currentUserEmail)) {
                    getData(model.getId_group(), viewHolder, model);
                } else {
                    getDataLeft(model.getId_group(), viewHolder, model);
                }
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

        getUsersOfGroup();

        messageText = (EditText) findViewById(R.id.messageBodyField);
        sendButton = (Button) findViewById(R.id.sendButton);

        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //DONE: Implementirati slanje poruke
        sendButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              String text = messageText.getText().toString();
                                              String uid = databaseReferenceMessages.push().getKey();
                                              message = new Message(currentUserEmail, uidGroup, text, System.currentTimeMillis());
                                              databaseReferenceMessages.child(uid).setValue(message);
                                              Log.e("", "PRE DODAVANJA PORUKA U GRUPU");

                                              messageText.setText("");
                                              group.getMessages().add(message);
                                              databaseReferenceGroups.child(group.getId_group()).setValue(group);

                                              //TODO: Implementirati notifikacije
                                              String loggedUserId = firebaseAuth.getCurrentUser().getUid();
                                              Map<String, User> map = group.getMembers();

                                              for (Map.Entry<String, User> entry : map.entrySet()){
                                                  User user = entry.getValue();
                                                  if(!user.getUser_id().equals(loggedUserId)){
                                                      String idNot = databaseNotification.push().getKey();
                                                      Notifikacija notifikacija = new Notifikacija(loggedUserId,user.getUser_id(),group.getId_group(),text);
                                                      databaseNotification.child(idNot).setValue(notifikacija);
                                                  }
                                              }




                                          }
                                      }
        );


        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                recyclerView.smoothScrollToPosition(firebaseRecyclerAdapter.getItemCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReferenceMessagesOfGroup.addChildEventListener(childEventListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                Intent intent = new Intent(SendMessageActivity.this, ConversationPageActivity.class);
                startActivity(intent);
                return true;
            //TODO: Trebace i slucaj za dodavanje novog prijatelja u konverzaciju
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getUsersOfGroup() {
        users = new ArrayList<>();
        databaseReferenceGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (n == 1) {
                    for (DataSnapshot u : dataSnapshot.getChildren()) {
                        Group group1 = u.getValue(Group.class);
                        if (group1 != null) {
                            if (uidGroup.equals(group1.getId_group())) {
                                users.addAll(group1.getMembers().values());
                            }
                        }
                    }
                    n++;
                    setTitleOfActivity(users);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setTitleOfActivity(List<User> users) {
        List<User> users2 = new ArrayList<>();
        for (User u : users) {
            if (!u.getEmail().equals(currentUserEmail)) {
                users2.add(u);
            }
        }
        for (User u : users2) {
            if (users2.indexOf(u) == users2.size() - 1) {
                title = title + u.getFirst_name();

            } else {
                title = title + u.getFirst_name() + ", ";
            }
        }
        this.setTitle(title);
    }

    public void getData(final String uidGroup, final MessagesViewHolder holder, final Message message) {
        users1 = new ArrayList<>();
        Log.e("", "get users ");
        databaseReferenceGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot u : dataSnapshot.getChildren()) {
                    Group group1 = u.getValue(Group.class);
                    if (group1 != null) {
                        if (uidGroup.equals(group1.getId_group())) {
                            users1.addAll(group1.getMembers().values());
                        }
                    }
                }
                setData(users1, holder, message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getDataLeft(final String uidGroup, final MessagesViewHolder holder, final Message message) {
        users1 = new ArrayList<>();
        Log.e("", "get users ");
        databaseReferenceGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot u : dataSnapshot.getChildren()) {
                    Group group1 = u.getValue(Group.class);
                    if (group1 != null) {
                        if (uidGroup.equals(group1.getId_group())) {
                            users1.addAll(group1.getMembers().values());
                        }
                    }
                }
                setDataLeft(users1, holder, message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setData(List<User> users, MessagesViewHolder holder, Message message) {
        Log.e("", "SET DATA");
        for (User u : users) {
            Log.e("", "USER " + u.getEmail());
            if ((message.getEmailUser()).equals(u.getEmail())) {
                String messageTextHolder = message.getText().toString();
                Log.e("", "DESNI TEKST : " + messageTextHolder);
                holder.messageText.setText(messageTextHolder);
                holder.messageText.setVisibility(View.VISIBLE);
                String senderHolder = u.getFirst_name() + " " + u.getLast_name();
                Log.e("", "DESNI NAME : " + senderHolder);
                holder.sender.setText(senderHolder);
                holder.sender.setVisibility(View.VISIBLE);

                if (holder.messageTextLeft.getVisibility() == View.VISIBLE && holder.senderLeft.getVisibility() == View.VISIBLE) {
                    holder.messageTextLeft.setVisibility(View.INVISIBLE);
                    holder.senderLeft.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void setDataLeft(List<User> users, MessagesViewHolder holder, Message message) {
        Log.e("", "SET DATA");
        for (User u : users) {
            Log.e("", "USER " + u.getEmail());
            if ((message.getEmailUser()).equals(u.getEmail())) {
                String messageTextHolder = message.getText().toString();
                Log.e("", "LIJEVI TEKST : " + messageTextHolder);
                holder.messageTextLeft.setText(messageTextHolder);
                holder.messageTextLeft.setVisibility(View.VISIBLE);
                String senderHolder = u.getFirst_name() + " " + u.getLast_name();
                Log.e("", "LIJEVI NAME : " + senderHolder);
                holder.senderLeft.setText(senderHolder);
                holder.senderLeft.setVisibility(View.VISIBLE);

                if (holder.messageText.getVisibility() == View.VISIBLE && holder.sender.getVisibility() == View.VISIBLE) {
                    holder.messageText.setVisibility(View.INVISIBLE);
                    holder.sender.setVisibility(View.INVISIBLE);
                }
            }
        }
    }



}
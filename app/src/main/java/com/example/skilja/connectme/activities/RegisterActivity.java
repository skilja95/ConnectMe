package com.example.skilja.connectme.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.skilja.connectme.R;

import com.example.skilja.connectme.model.Group;
import com.example.skilja.connectme.model.User;
import com.example.skilja.connectme.model.UserToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailRegister;
    private EditText firstNameRegister;
    private EditText lastNameRegister;
    private EditText passwordRegister;
    private Button buttonRegister;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;
    private DatabaseReference firebaseToken;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailRegister = (EditText) findViewById(R.id.userEmail);
        firstNameRegister = (EditText) findViewById(R.id.userFirstName);
        lastNameRegister = (EditText) findViewById(R.id.userLastName);
        passwordRegister = (EditText) findViewById(R.id.userPassword);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("users");
        firebaseToken = FirebaseDatabase.getInstance().getReference("usersToken");

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(emailRegister.getText().toString())) {
                    emailRegister.setError("Email is required!");
                    return;
                } else if (TextUtils.isEmpty(firstNameRegister.getText().toString())) {
                    firstNameRegister.setError("First name is required! ");
                    return;
                } else if (TextUtils.isEmpty(lastNameRegister.getText().toString())) {
                    lastNameRegister.setError("Last name is required! ");
                    return;
                } else if (TextUtils.isEmpty(passwordRegister.getText().toString())) {
                    passwordRegister.setError("Password is required! ");
                    return;
                }

                final ProgressDialog progressDialog = ProgressDialog.show(RegisterActivity.this, "Please wait...", "Procession..", true);

                (firebaseAuth.createUserWithEmailAndPassword(emailRegister.getText().toString(), passwordRegister.getText().toString()))
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    String uid = firebaseAuth.getCurrentUser().getUid();
                                    String email = firebaseAuth.getCurrentUser().getEmail();
                                    String token_id = FirebaseInstanceId.getInstance().getToken();
                                    User user = new User(uid, email, firstNameRegister.getText().toString(), lastNameRegister.getText().toString(), null, new ArrayList<Group>());
                                    UserToken userToken = new UserToken(uid, email, firstNameRegister.getText().toString(), lastNameRegister.getText().toString(), null, new ArrayList<Group>(),token_id);
                                    firebaseDatabase.child(firebaseAuth.getCurrentUser().getUid()).setValue(user);
                                    firebaseToken.child(firebaseAuth.getCurrentUser().getUid()).setValue(userToken);
                                    Toast.makeText(RegisterActivity.this, "Successful registration.", Toast.LENGTH_SHORT).show();

                                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(loginIntent);
                                    finish();

                                } else {
                                    Log.e("ERROR", task.getException().toString());
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERROR", e.getMessage());
                    }
                });

            }
        });

    }
}

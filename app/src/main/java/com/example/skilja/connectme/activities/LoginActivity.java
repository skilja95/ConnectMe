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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.example.skilja.connectme.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin;
    private EditText passwordLogin;
    private Button signInButton;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private String token;
    private SessionManager session;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new SessionManager(LoginActivity.this);
        emailLogin = (EditText) findViewById(R.id.loginEmail);
        passwordLogin = (EditText) findViewById(R.id.loginPassword);
        signInButton = (Button) findViewById(R.id.signInButton);
        registerButton = (Button) findViewById(R.id.regButton);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registrationIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registrationIntent);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(emailLogin.getText().toString())) {
                    emailLogin.setError("Email is required!");
                    return;
                } else if (TextUtils.isEmpty(passwordLogin.getText().toString())) {
                    passwordLogin.setError("Password is required!");
                    return;
                }

                final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait...", "Procession..", true);

                (firebaseAuth.signInWithEmailAndPassword(emailLogin.getText().toString(), passwordLogin.getText().toString()))
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    Task<GetTokenResult> s = firebaseUser.getToken(true);
                                    s.addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            token = task.getResult().getToken();
                                            String email = firebaseAuth.getCurrentUser().getEmail();

                                            session.createLoginSession(token, email, null);
                                        }
                                    });


                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                                    Intent loginIntent = new Intent(LoginActivity.this, ConversationPageActivity.class);
                                    loginIntent.putExtra("Email", firebaseAuth.getCurrentUser().getEmail());
                                    loginIntent.putExtra("ImageURL", firebaseAuth.getCurrentUser().getPhotoUrl());
                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    // Add new Flag to start new Activity
                                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(loginIntent);
                                    finish();
                                } else {
                                    Log.e("ERROR", task.getException().getMessage());
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

}

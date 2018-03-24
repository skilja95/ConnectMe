package com.example.skilja.connectme.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.skilja.connectme.R;

import com.example.skilja.connectme.SessionManager;
import com.example.skilja.connectme.model.User;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener  {

    private static final String TAG = "Main activity";

    private static final int RC_SIGN_IN = 9001;

    //Declare auth
    private FirebaseAuth firebaseAuth;
    //Declare authStateListener
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mRef;
    private FirebaseUser firebaseUser;
    private String token;

    //EMAIL & PASS
    private Button emailSignIn;

    //GOOGLE
    private GoogleApiClient googleApiClient;
    private SignInButton googleButton;

    //FACEBOOK
    private CallbackManager callbackManager;
    private LoginButton facebookButton;

    //SESSION MANAGER
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);


        session = new SessionManager(MainActivity.this);
        session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();

        // name
        String token = user.get(SessionManager.KEY_TOKEN);

        // email
        String email = user.get(SessionManager.KEY_EMAIL);

        String fcmID = FirebaseInstanceId.getInstance().getToken();

        Log.d("FROM MAIN-a", "   ////////////////" + email);
        Log.d("FROM MAIN-a", "   ////////////////" + token);
        Log.d("FROM MAIN-a", "   ////////////////" + fcmID);
        emailSignIn = (Button) findViewById(R.id.goToEmailReg);
        emailSignIn.setOnClickListener(this);

        googleButton = (SignInButton) findViewById(R.id.google_button);
        googleButton.setOnClickListener(this);

        facebookButton = (LoginButton) findViewById(R.id.facebook_button);

        //initialize auth
        firebaseAuth = FirebaseAuth.getInstance();

        mRef = FirebaseDatabase.getInstance().getReference("users");

        //Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this) //FragmentActivity,OnConnectionFailedListener
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Configure Facebook Login
        callbackManager = CallbackManager.Factory.create();
        facebookButton.setReadPermissions("email", "public_profile");
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());
                } else {
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
    }

    //-----------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_button:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.goToEmailReg:
                // TODO: SKILJA Intent to registration page

            default:
                return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        ;
        return;
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(..)
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            Log.e(TAG, "Google Sign IN failed." + statusCode);
            if (result.isSuccess()) {
                //Google Sign In was successful authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                //Google Sign In failed
                Log.e(TAG, "Google Sign IN failed.");
            }
        }

        //Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        Task<GetTokenResult> s = firebaseUser.getToken(true);
                        s.addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            @Override
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                token = task.getResult().getToken();
                                String email = firebaseAuth.getCurrentUser().getEmail();
                                String picture = firebaseAuth.getCurrentUser().getPhotoUrl().toString();
                                session.createLoginSession(token, email, picture);
                            }
                        });

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String uid = task.getResult().getUser().getUid();
                            String[] names = task.getResult().getUser().getDisplayName().split(" ");
                            String firstName = names[0];
                            String lastName = names[1];
                            String image = task.getResult().getUser().getPhotoUrl().toString();
                            String email = task.getResult().getUser().getEmail();


                            User user = new User(uid, email, firstName, lastName, image, null);

                            mRef.child(uid).setValue(user);

                            // TODO: SKILJA Intent to conversations page
                        }

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        } else {

                            String uid = task.getResult().getUser().getUid();
                            String[] names = task.getResult().getUser().getDisplayName().split(" ");
                            String firstName = names[0];
                            String lastName = names[1];
                            String image = task.getResult().getUser().getPhotoUrl().toString();
                            String email = task.getResult().getUser().getEmail();

                            User user = new User(uid, email, firstName, lastName, image, null);

                            mRef.child(uid).setValue(user);
                            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            Task<GetTokenResult> s = firebaseUser.getToken(true);
                            s.addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    token = task.getResult().getToken();
                                    String email = firebaseAuth.getCurrentUser().getEmail();
                                    String picture = firebaseAuth.getCurrentUser().getPhotoUrl().toString();
                                    session.createLoginSession(token, email, picture);
                                }
                            });

                            // TODO: SKILJA Intent to conversations page

                        }

                    }
                });
    }

}

package com.example.skilja.connectme.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.skilja.connectme.R;
import com.example.skilja.connectme.SessionManager;
import com.example.skilja.connectme.fragments.DeleteMessagesFragment;
import com.example.skilja.connectme.fragments.HomeFragment;
import com.example.skilja.connectme.fragments.PeopleFragment;
import com.example.skilja.connectme.model.CircleTransform;

import java.util.HashMap;


public class ConversationPageActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private String[] activityTitles;
    private ImageView imgProfile;
    private TextView emailShow;
    private SessionManager session;


    public static int navItemIndex = 0;


    private static final String TAG_HOME = "home";
    private static final String TAG_DELETE_MESSAGES = "delete_message";
    private static final String TAG_PEOPLE = "people";

    public static String CURRENT_TAG = TAG_HOME;

    private String urlProfileImg = "http://jagc.org/images/avatar.png";
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_page);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger_icon);
        session = new SessionManager(ConversationPageActivity.this);
        String emailFromIntent = getIntent().getStringExtra("Email");
        String imageFromIntent = getIntent().getStringExtra("ImageURL");
        if (imageFromIntent != null) {
            urlProfileImg = imageFromIntent;
        }


        //DONE: in case that user is from sharedpreference, and dont have intent and mail
        if (emailFromIntent == null) {
            HashMap<String, String> user = session.getUserDetails();
            emailFromIntent = user.get(SessionManager.KEY_EMAIL);

            //from facebook or gmail
            if (user.get(SessionManager.KEY_IMAGE) != null) {
                urlProfileImg = user.get(SessionManager.KEY_IMAGE);
            }
        }
        mHandler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);


        navHeader = navigationView.getHeaderView(0);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        imgProfile = navHeader.findViewById(R.id.img_profile);
        emailShow =  navHeader.findViewById(R.id.emailShow);
        emailShow.setText(emailFromIntent);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        loadNavHeader();


        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                switch (menuItem.getItemId()) {
                    case R.id.nav_inbox:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_message:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_DELETE_MESSAGES;
                        break;
                    case R.id.nav_people:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_PEOPLE;
                        break;
                    case R.id.nav_logout:
                        session.logoutUser();
                        finish();
                        break;
                    default:
                        navItemIndex = 0;
                }

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawer.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void loadNavHeader() {

        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

    }

    private void loadHomeFragment() {

        selectNavMenu();


        setToolbarTitle();

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            toggleFab();
            return;
        }


        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };


        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        toggleFab();


        drawer.closeDrawers();


        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                DeleteMessagesFragment deleteMessagesFragment = new DeleteMessagesFragment();
                return deleteMessagesFragment;
            case 2:
                PeopleFragment peopleFragment = new PeopleFragment();
                return peopleFragment;
            default:
                return new HomeFragment();
        }
    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }


    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }

}

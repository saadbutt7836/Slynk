package com.pronuxtech.slynk;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomePageActivity extends AppCompatActivity {

    //    WIDGETS

    private BottomNavigationView bottomNavigationView;

    //    FRAGMENTS
    private HomeFragmentBottomNavigate homeFragmentBottomNavigate;
    private SearchFragmentBottomNavigate searchFragmentBottomNavigate;
    private UploadPostFragmentBottomNavigate uploadPostFragmentBottomNavigate;
    private NotificationFragmentBottomNavigate notificationFragmentBottomNavigate;
    private SettingsFragmentBottomNavigate settingsFragmentBottomNavigate;

    //    FIREBASE REFRENCES
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

//        NULL TITLE BAR
        getSupportActionBar().setTitle(R.string.app_name);

//        FIREBASE AUTH INITIALIZE
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavBarListener);


        homeFragmentBottomNavigate = new HomeFragmentBottomNavigate();
        searchFragmentBottomNavigate = new SearchFragmentBottomNavigate();
        uploadPostFragmentBottomNavigate = new UploadPostFragmentBottomNavigate();
        notificationFragmentBottomNavigate = new NotificationFragmentBottomNavigate();
        settingsFragmentBottomNavigate = new SettingsFragmentBottomNavigate();

        setFragment(homeFragmentBottomNavigate);


//        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
//        navigationView = (NavigationView) findViewById(R.id.navigationView);

//        GET HEADER LAYOUT VIEW
//        headerLayoutView = navigationView.getHeaderView(0);

//        HEADER LAYOUT CASTINGS
//        drawerActiveUserProfile = (CircleImageView) headerLayoutView.findViewById(R.id.drawerActiveUserProfile);
//        drawerActiveUserName = (TextView) headerLayoutView.findViewById(R.id.drawerActiveUserName);
//        drawerActiveUserEmail = (TextView) headerLayoutView.findViewById(R.id.drawerActiveUserEmail);


//        drawerActiveUserProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(HomePageActivity.this, "profile pick click", Toast.LENGTH_SHORT).show();
//            }
//        });

//        ======================================================================================================================

//        actionBarDrawerToggle = new ActionBarDrawerToggle(HomePageActivity.this, drawerLayout, R.string.open, R.string.close);
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        navigationView.setNavigationItemSelectedListener(this);

//        BY DEFAULT HOME PAGE SHOW
//        getSupportFragmentManager().beginTransaction().replace(R.id.drawerFragmentContainer, new HomeDrawerFragment()).commit();
//        navigationView.setCheckedItem(R.id.drawerHome);

//===================================================================================================================================

//        FIREBASE LISTENER CHECK EITHER USER LOGIN IN OR NOT


    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavBarListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.bottomHome:
                    setFragment(homeFragmentBottomNavigate);
                    break;
                case R.id.bottomSearch:
                    setFragment(searchFragmentBottomNavigate);
                    break;
                case R.id.bottomChat:
                    setFragment(uploadPostFragmentBottomNavigate);
                    break;
                case R.id.bottomGroups:
                    setFragment(notificationFragmentBottomNavigate);
                    break;
                case R.id.bottomUser:
                    setFragment(settingsFragmentBottomNavigate);
                    break;
            }
            return true;
        }
    };


    //    ON START CHECKED EITHER USER LOGIN OR NOT
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(HomePageActivity.this, "user not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
//ACTIONBAR MENU HANDLING --START


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_page_activity_action_menu, menu);
        return true;
    }

    //    ACTIONBAR ITEM SELECTED
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.chatIcon:
                Intent intent = new Intent(this, ConversationUsersListActivity.class);
                startActivity(intent);
        }
        return true;
    }


    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
    }

}

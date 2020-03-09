package com.pronuxtech.slynk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConversationUsersListActivity extends AppCompatActivity implements View.OnClickListener {

    //    WIDGETS
    private View view;
    private ImageView chatBackArrow, searchIconHideBarShow, searchIconShowBarHide, newMessage;
    private RecyclerView conversations_RecyclerView;
    private TextView cretaeGroup;
    private LinearLayout searchBarLayout;
    private Animation animation;
    private TabLayout subTabLayout;
    private ViewPager mViewPager;

    //    STRING VARIABLES
    private String currentuserId;

    private List<ConversationsModelClass> conversationsList;
    private LinearLayoutManager linearLayoutManager;
    private ConversationAdapterClass adapter;

    //    DATBASE REFERENCES
    private DatabaseReference RootRef, UsersRef, ConversationsRef, MessagesRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_user_list);

        getSupportActionBar().hide();

        chatBackArrow = (ImageView) findViewById(R.id.chatBackArrow);
        searchBarLayout = (LinearLayout) findViewById(R.id.searchBarLayout);
        newMessage = (ImageView) findViewById(R.id.newMessage);
        cretaeGroup = (TextView) findViewById(R.id.cretaeGroup);
        conversations_RecyclerView = (RecyclerView) findViewById(R.id.conversations_RecyclerView);

//        searchIconHideBarShow = (ImageView) findViewById(R.id.searchIconHideBarShow);
//        searchIconShowBarHide = (ImageView) findViewById(R.id.searchIconShowBarHide);

//        subTabLayout = (TabLayout) findViewById(R.id.subTabLayout);
//        mViewPager = (ViewPager) findViewById(R.id.viewPager);

//        setupViewPager(mViewPager);
//        subTabLayout.setupWithViewPager(mViewPager);

        //ONCREATE SEARCHBAR ANIMATION
//        searchBarLayout.animate().translationY(-500f);

//        DATBASE REFERENCES CASTINGS
        firebaseAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = RootRef.child("Users");
        ConversationsRef = RootRef.child("conversations");
        MessagesRef = RootRef.child("messages");
        currentuserId = firebaseAuth.getCurrentUser().getUid();

//        OFFLINE DATA
        UsersRef.keepSynced(true);
        ConversationsRef.keepSynced(true);
        MessagesRef.keepSynced(true);


//        CLICK LISTENERS
        chatBackArrow.setOnClickListener(this);
        newMessage.setOnClickListener(this);
        cretaeGroup.setOnClickListener(this);
//        searchIconHideBarShow.setOnClickListener(this);
//        searchIconShowBarHide.setOnClickListener(this);
        conversationsList = new ArrayList<>();


    }


//    private void setupViewPager(ViewPager viewPager) {
//        viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new PeopleFragmentChat(), "People");
//
//        adapter.addFragment(new FriendsFragmentChat(), "Friends");
//
//        adapter.addFragment(new GroupsFragmentChat(), "Groups");
//        viewPager.setAdapter(adapter);
//    }


    @Override
    protected void onStart() {
        super.onStart();

        ConversationsRetrieve();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chatBackArrow:
                onBackPressed();
                break;
            case R.id.newMessage:
                startActivity(new Intent(this, AllChatUserListActivity.class));
                break;
            case R.id.cretaeGroup:
                startActivity(new Intent(this, CreateGroupActivity.class));
//            case R.id.searchIconHideBarShow:
//                searchIconHideBarShow();
//                break;
//
//            case R.id.searchIconShowBarHide:
//                searchIconShowBarHide();
//                break;
        }
    }

//    private void searchIconShowBarHide() {
//        searchIconHideBarShow.setVisibility(View.VISIBLE);
//        searchIconShowBarHide.setVisibility(View.GONE);


//                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);

//        searchBarLayout.animate().translationY(-1000f).setDuration(1000);
//        searchBarLayout.setVisibility(View.GONE);
//                        searchBarLayout.startAnimation(animation);
//    }
//
//    private void searchIconHideBarShow() {
//        searchIconHideBarShow.setVisibility(View.GONE);
//        searchIconShowBarHide.setVisibility(View.VISIBLE);
//
//
//        searchBarLayout.animate().translationY(0f).setDuration(1000);
//        searchBarLayout.setVisibility(view.VISIBLE);
//                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
//                searchBarLayout.startAnimation(animation);
//    }

    //TABLAYOUT class
//    class viewPagerAdapter extends FragmentPagerAdapter {
//
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public viewPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentTitleList.size();
//
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//
//            mFragmentList.add(fragment);
//
//            mFragmentTitleList.add(title);
//
//        }

//        @Nullable
//        @Override
//        public CharSequence getPageTitle(int position) {
//
//            return mFragmentTitleList.get(position);
//        }
//    }


    private void ConversationsRetrieve() {
        Query query = ConversationsRef.orderByChild("sent_by").equalTo(currentuserId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final ConversationsModelClass conversationsModelClass = snapshot.getValue(ConversationsModelClass.class);
                        final String chatRoomId = conversationsModelClass.getChatroom_id();
                        conversationsList.clear();
                        MessagesRef.child(chatRoomId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    conversationsList.add(conversationsModelClass);
                                }
                                adapter = new ConversationAdapterClass(getApplicationContext(), conversationsList);
                                conversations_RecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                conversations_RecyclerView.setAdapter(adapter);
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

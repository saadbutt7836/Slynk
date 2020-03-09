package com.pronuxtech.slynk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFriendsActivity extends AppCompatActivity {

    //    WIDGETS
    private RecyclerView friendsRecycleView;

    //    VARIABLES
    private String currentUserId;

    private FirebaseRecyclerAdapter<MyFriendsModelClass, FriendsViewHolder> friendsAdapter;
    private FirebaseRecyclerOptions<MyFriendsModelClass> options;

    List<LiveSearchModelClass> friendsList;

    //    DATBASE REFERENCES
    private DatabaseReference FriendsRef, UserRef;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);

        getSupportActionBar().setTitle("Friends");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        Toast.makeText(this, currentUserId, Toast.LENGTH_SHORT).show();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        friendsList = new ArrayList<>();


        friendsRecycleView = (RecyclerView) findViewById(R.id.friendsRecycleView);
        friendsRecycleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        friendsRecycleView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();


//

        MyFriendsListShow();


    }

    private void MyFriendsListShow() {
        FriendsRef.child("friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                final String key = snapshot.getKey();

                                friendsList.clear();
                                UserRef.child(snapshot.getKey())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String fullName = "", userName = "", profileImg = "";

                                                if (dataSnapshot.hasChild("first_name") && dataSnapshot.hasChild("last_name")) {
                                                    String fname = dataSnapshot.child("first_name").getValue().toString();
                                                    String lName = dataSnapshot.child("last_name").getValue().toString();
                                                    fullName = fname + " " + lName;
                                                }
                                                if (dataSnapshot.hasChild("username")) {
                                                    userName = dataSnapshot.child("username").getValue().toString();
                                                }
                                                if (dataSnapshot.hasChild("profile_img")) {
                                                    profileImg = dataSnapshot.child("profile_img").getValue().toString();
                                                }


                                                LiveSearchModelClass liveSearchModelClass = new LiveSearchModelClass(key, fullName, userName, profileImg);
                                                friendsList.add(liveSearchModelClass);

                                                LiveSearchAdapter adapter = new LiveSearchAdapter(getApplicationContext(), friendsList);
                                                friendsRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                friendsRecycleView.setAdapter(adapter);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                String msg = databaseError.toException().getMessage();
                                                Toast.makeText(MyFriendsActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        String msg = databaseError.toException().getMessage();
                        Toast.makeText(MyFriendsActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();

                    }
                });


    }

    //    VIEW HOLDER
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        private RelativeLayout liveSearchLayout;
        private CircleImageView imageView_SearchProfile;
        private TextView textView_FullName, textView_SearchUserName;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            liveSearchLayout = (RelativeLayout) mView.findViewById(R.id.liveSearchLayout);
            imageView_SearchProfile = (CircleImageView) mView.findViewById(R.id.imageView_SearchProfile);
            textView_FullName = (TextView) mView.findViewById(R.id.textView_FullName);
            textView_SearchUserName = (TextView) mView.findViewById(R.id.textView_SearchUserName);
        }
    }

}

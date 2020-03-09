package com.pronuxtech.slynk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikedPeopleActivity extends AppCompatActivity implements View.OnClickListener {

    //    WIDGETS
    private ImageView LikeBackArrow;
    private RecyclerView LikesRecycleView;

    //    VARIABLES
    private String currentPostId;

    //    DATABASE REFERENCES
    private DatabaseReference PostRefer, LikedPeopleRefer;

    List<LiveSearchModelClass> likeList;

    //    FIREBASE ADAPTER
//    FirebaseRecyclerAdapter<PostCommentsClass, PostLikedAdapter.PostLikedViewHolder> adapter;
//    FirebaseRecyclerOptions<PostCommentsClass> options;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_people);

        getSupportActionBar().hide();
        currentPostId = getIntent().getStringExtra("postId");

        PostRefer = FirebaseDatabase.getInstance().getReference("Posts").child(currentPostId);
        LikedPeopleRefer = FirebaseDatabase.getInstance().getReference("Users");

//        CASTINGS
        LikeBackArrow = (ImageView) findViewById(R.id.LikeBackArrow);
        LikesRecycleView = (RecyclerView) findViewById(R.id.LikesRecycleView);

        likeList = new ArrayList<>();

//        CLICK LISTENERS
        LikeBackArrow.setOnClickListener(this);
//        options = new FirebaseRecyclerOptions
//                .Builder<PostCommentsClass>()
//                .setQuery(PostRefer, PostCommentsClass.class).build();
        LikesRecycleView.setHasFixedSize(true);
        LikesRecycleView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        RetrievingDataOfLikedPeople();
    }

    private void RetrievingDataOfLikedPeople() {


        PostRefer.child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        final String key = snapshot.getKey();
                        likeList.clear();

                        LikedPeopleRefer.child(snapshot.getKey())
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
                                        likeList.add(liveSearchModelClass);

                                        LiveSearchAdapter adapter = new LiveSearchAdapter(getApplicationContext(), likeList);
                                        LikesRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                        LikesRecycleView.setAdapter(adapter);


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(LikedPeopleActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
//                        =================================================
//                        LikedPeopleRefer = LikedPeopleRefer.child(snapshot.getKey());
//                        options = new FirebaseRecyclerOptions
//                                .Builder<PostCommentsClass>()
//                                .setQuery(LikedPeopleRefer, PostCommentsClass.class).build();
//
//                        adapter = new FirebaseRecyclerAdapter<PostCommentsClass, PostLikedViewHolder>(options) {
//                            @Override
//                            protected void onBindViewHolder(@NonNull PostLikedViewHolder holder, int i, @NonNull PostCommentsClass model) {
//
//                                String a = getRef(i).getKey();
//                                Toast.makeText(LikedPeopleActivity.this, a, Toast.LENGTH_SHORT).show();
//                                Log.d("qq", a);
//                            }
//
//                            @NonNull
//                            @Override
//                            public PostLikedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_comment_layout, parent, false);
//                                return new PostLikedViewHolder(view);
//                            }
//                        };
//
//                        LikesRecycleView.setAdapter(adapter);
//                        adapter.startListening();


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LikedPeopleActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.LikeBackArrow: {
                onBackPressed();
                break;
            }
        }
    }


}

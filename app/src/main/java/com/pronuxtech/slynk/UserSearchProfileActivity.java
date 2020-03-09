package com.pronuxtech.slynk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserSearchProfileActivity extends AppCompatActivity {

    //    WIDGETS
    private Button btn_ConfirmRequest, btn_DeclineRequest;
    private CircleImageView imageView_ProfilePic;
    private TextView requestText, textView_FullName, textView_UserName,
            textView_University, textView_GraduationLevel, textView_Gender, textView_NoOfPosts, textView_Mylinks;
    private LinearLayout linked, btn_SendRequest, btn_Respond, confirm_decline_layout;

    //    VARIABLES STRING
    private String searchedUserId, currentUserId, friendRequestId, notificationId;
    private Double timeStamp;
    private Boolean friendsLink = true;
    private RecyclerView searchUserRecycleView;


    private FirebaseRecyclerAdapter<PostDataHomePage, OwnPostViewHolder> adapter;
    private FirebaseRecyclerOptions<PostDataHomePage> options;


    //    CONSTANT STRINGS
    private String CURRENT_STATE, timeAgo;


    //    DATABASE REFERENCE
    private DatabaseReference UsersReference, FriendRequestRef, NotificationsRef, reference, PostReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search_profile);

        requestText = (TextView) findViewById(R.id.requestText);
        btn_SendRequest = (LinearLayout) findViewById(R.id.btn_SendRequest);
        btn_Respond = (LinearLayout) findViewById(R.id.btn_Respond);
        linked = (LinearLayout) findViewById(R.id.linked);
        confirm_decline_layout = (LinearLayout) findViewById(R.id.confirm_decline_layout);
        btn_ConfirmRequest = (Button) findViewById(R.id.btn_ConfirmRequest);
        btn_DeclineRequest = (Button) findViewById(R.id.btn_DeclineRequest);
        imageView_ProfilePic = (CircleImageView) findViewById(R.id.imageView_ProfilePic);
//        linearLayout_MyLinks = (LinearLayout) findViewById(R.id.linearLayout_MyLinks);
        textView_FullName = (TextView) findViewById(R.id.textView_FullName);
        textView_UserName = (TextView) findViewById(R.id.textView_UserName);
        textView_University = (TextView) findViewById(R.id.textView_University);
        textView_GraduationLevel = (TextView) findViewById(R.id.textView_GraduationLevel);
        textView_Gender = (TextView) findViewById(R.id.textView_Gender);
        textView_NoOfPosts = (TextView) findViewById(R.id.textView_NoOfPosts);
        textView_Mylinks = (TextView) findViewById(R.id.textView_Mylinks);
        searchUserRecycleView = (RecyclerView) findViewById(R.id.searchUserRecycleView);


        UsersReference = FirebaseDatabase.getInstance().getReference("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference("friend-requests");
        NotificationsRef = FirebaseDatabase.getInstance().getReference("notifications");
        PostReference = FirebaseDatabase.getInstance().getReference("Posts");
        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

//        getSupportActionBar().hide();

//        ==============================

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);
        searchUserRecycleView.setHasFixedSize(true);
        searchUserRecycleView.setLayoutManager(linearLayoutManager);

//        ==============================


        searchedUserId = getIntent().getStringExtra("search_user_key");
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        CURRENT_STATE = "not_friends";


        CheckUserAlreadyFriend();
        RetrievingInformationOfSearchedUser();

        if (!currentUserId.equals(searchedUserId)) {
            btn_SendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btn_SendRequest.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friends")) {
                        SendFriendRequestToaPerson();
                    }
                    if (CURRENT_STATE.equals("request_sent")) {
                        CancelFriendRequest();
                    }

                    if (CURRENT_STATE.equals("friends")) {
                        UnfriendTheExistingFriend();
                    }
                }
            });


            btn_ConfirmRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CURRENT_STATE.equals("request_received")) {

                        AcceptFriendRequest();
                    }
                }
            });

        } else {
            linked.setVisibility(View.GONE);
            confirm_decline_layout.setVisibility(View.GONE);
            btn_DeclineRequest.setVisibility(View.GONE);
            btn_SendRequest.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Query query = PostReference.orderByChild("posted_by").equalTo(searchedUserId);
        options = new FirebaseRecyclerOptions.Builder<PostDataHomePage>()
                .setQuery(query, PostDataHomePage.class).build();

        adapter = new FirebaseRecyclerAdapter<PostDataHomePage, OwnPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final OwnPostViewHolder holder, final int position, @NonNull final PostDataHomePage model) {
                if (model.likes.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    // Unstar the post and remove self from stars
                    holder.imageView_LikeUnlike.setImageResource(R.drawable.liked);
                }
                if (model.getLikesCount() > 0) {
                    holder.linearLayout_LikedPeople.setVisibility(View.VISIBLE);
                }


                int type = model.getType();
//                postKey = getRef(position).getKey();


//                TIME AGOS HANDLE
                long firebaseTime = (long) model.getTimestamp();

                TimeAgosHandle(firebaseTime);


                holder.postTime.setText(timeAgo);
                holder.postTotalComments.setText(String.valueOf(model.getComments()));
                holder.postTotalLikes.setText(String.valueOf(model.getLikesCount()));

                if (type == 0) {
                    holder.postMessages.setText(model.getStatus());
                    Picasso.get().load((Uri) null).into(holder.postImage);
                } else if (type == 1) {
                    Picasso.get().load(model.getPost_image()).into(holder.postImage);
                    holder.postMessages.setText(null);
                } else if (type == 2) {
                    holder.postMessages.setText(model.getStatus());
                    Picasso.get().load(model.getPost_image()).into(holder.postImage);
                }


                UsersReference.child(model.getPosted_by())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("first_name") && dataSnapshot.hasChild("last_name")) {
                                        String firstName = dataSnapshot.child("first_name").getValue().toString();
                                        String lastName = dataSnapshot.child("last_name").getValue().toString();
                                        holder.postUserName.setText(firstName + " " + lastName);
                                    }
                                    if (dataSnapshot.hasChild("profile_img")) {
                                        String profilePic = dataSnapshot.child("profile_img").getValue().toString();

                                        Picasso.get().load(profilePic).into(holder.postProfilePic);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                holder.linearLayout_LikedPeople.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), LikedPeopleActivity.class);
                        intent.putExtra("postId", getRef(position).getKey());
                        startActivity(intent);
                    }
                });

                holder.postUnLikeStateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference databaseReference;
                        FirebaseAuth firebaseAuth;
                        databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(getRef(position).getKey());
                        String postId = model.getPid();
                        String postedBy = model.getPosted_by();
                        onStarClicked(databaseReference, postId, postedBy);
                        if (model.likes.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            // Unstar the post and remove self from stars
                            holder.imageView_LikeUnlike.setImageResource(R.drawable.liked);
                        } else {
//                            // Star the post and add self to stars
                            holder.imageView_LikeUnlike.setImageResource(R.drawable.like);
                        }

                    }
                });

                holder.postCommentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), PostCommentsActivity.class);
                        intent.putExtra("postId", getRef(position).getKey());
                        startActivity(intent);
                    }
                });
                holder.postProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String searchedUserKey = model.getPosted_by();
                        Intent intent = new Intent(getApplicationContext(), UserSearchProfileActivity.class);
                        intent.putExtra("search_user_key", searchedUserKey);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                holder.postUserName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String searchedUserKey = model.getPosted_by();
                        Intent intent = new Intent(getApplicationContext(), UserSearchProfileActivity.class);
                        intent.putExtra("search_user_key", searchedUserKey);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public OwnPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_recycle_view, parent, false);

                return new OwnPostViewHolder(view);
            }
        };
        searchUserRecycleView.setAdapter(adapter);
        adapter.startListening();
    }

    private void TimeAgosHandle(long time) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - time);
        long hours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - time);
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - time);


        if (seconds < 60) {
            timeAgo = seconds + " sec ago";
        } else if (minutes < 60) {
            timeAgo = minutes + " min";
        } else if (hours < 24) {
            timeAgo = hours + " hr ";
        } else if (days % 7 == 0) {
            long week = days / 7;
            timeAgo = week + " w";
        } else {
            timeAgo = days + " d ";
        }
    }

    //    ===================================
    private void onStarClicked(DatabaseReference postRef, final String posId, final String sendTolike) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                PostDataHomePage p = mutableData.getValue(PostDataHomePage.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.likes.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    // Unstar the post and remove self from stars
                    p.likesCount = p.likesCount - 1;
                    p.likes.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
                } else {
                    // Star the post and add self to stars
                    p.likesCount = p.likesCount + 1;
                    p.likes.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);

                    Map<String, Object> likeObject = new HashMap<>();

                    likeObject.put("post_id", posId);
                    likeObject.put("sent_by", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    likeObject.put("sent_to", sendTolike);
                    likeObject.put("timestamp", System.currentTimeMillis());
                    likeObject.put("type", 0);

                    String randomId = reference.push().getKey();
                    NotificationsRef.child(randomId).setValue(likeObject);

                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
//
//
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


//    ===================================


    private void UnfriendTheExistingFriend() {
        UsersReference.child(currentUserId).child("friends")
                .child(searchedUserId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            UsersReference.child(searchedUserId).child("friends")
                                    .child(currentUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                btn_SendRequest.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                requestText.setText("Add links");

                                                btn_DeclineRequest.setVisibility(View.GONE);
                                                btn_DeclineRequest.setEnabled(false);

                                            } else {
                                                String msg = task.getException().toString();
                                                Toast.makeText(UserSearchProfileActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            String msg = task.getException().toString();
                            Toast.makeText(UserSearchProfileActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void CheckUserAlreadyFriend() {
        UsersReference.child(currentUserId).child("friends")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String friendKey = snapshot.getKey();
                                if (friendKey.equals(searchedUserId)) {
//                                    ======================================
                                    btn_Respond.setEnabled(false);
                                    btn_Respond.setVisibility(View.GONE);
                                    btn_ConfirmRequest.setEnabled(false);
                                    btn_ConfirmRequest.setVisibility(View.GONE);
                                    confirm_decline_layout.setVisibility(View.GONE);


                                    btn_SendRequest.setEnabled(true);
                                    CURRENT_STATE = "friends";
                                    requestText.setText("Unfriend");
                                    btn_DeclineRequest.setVisibility(View.GONE);
                                    btn_DeclineRequest.setEnabled(false);

                                } else {

                                    confirm_decline_layout.setVisibility(View.GONE);
                                    btn_DeclineRequest.setVisibility(View.GONE);
                                    CheckFriendRequestAlreadySent();
                                }
                            }
                        } else {
                            confirm_decline_layout.setVisibility(View.GONE);
                            btn_DeclineRequest.setVisibility(View.GONE);
                            CheckFriendRequestAlreadySent();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        String errorMsg = databaseError.toException().getMessage();
                        Toast.makeText(UserSearchProfileActivity.this, "error: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AcceptFriendRequest() {
        UsersReference.child(currentUserId)
                .child("friends")
                .child(searchedUserId)
                .setValue(friendsLink)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AddFriends();
                        } else {
                            Toast.makeText(UserSearchProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void AddFriends() {
        UsersReference.child(searchedUserId)
                .child("friends")
                .child(currentUserId)
                .setValue(friendsLink)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            RequestChildRemoveOnAccept();
//                            ================================
                            btn_SendRequest.setVisibility(View.VISIBLE);
                            btn_Respond.setVisibility(View.GONE);
                            confirm_decline_layout.setVisibility(View.GONE);
                            btn_ConfirmRequest.setEnabled(false);
                            btn_ConfirmRequest.setVisibility(View.GONE);
//                            ======================================

                            btn_SendRequest.setEnabled(true);
                            CURRENT_STATE = "friends";
                            requestText.setText("Unfriend");

                            btn_DeclineRequest.setVisibility(View.GONE);
                            btn_DeclineRequest.setEnabled(false);

                            Toast.makeText(UserSearchProfileActivity.this, "Linked", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserSearchProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void RequestChildRemoveOnAccept() {
        FriendRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final String notificationId = snapshot.child("notification").getValue().toString();
                        String sendByUser = snapshot.child("sent_by").getValue().toString();
                        String sendToUser = snapshot.child("sent_to").getValue().toString();

                        if ((sendByUser.equals(currentUserId) && sendToUser.equals(searchedUserId)) ||
                                (sendByUser.equals(searchedUserId) && sendToUser.equals(currentUserId))) {
                            String key = snapshot.getKey();

//                            DELETE CHILD ON CANCEL REQUEST
                            FriendRequestRef.child(key).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {


                                                NotificationsRef.child(notificationId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    btn_SendRequest.setEnabled(true);
                                                                    CURRENT_STATE = "friends";
                                                                    requestText.setText("Unfriend");

                                                                    btn_DeclineRequest.setVisibility(View.INVISIBLE);
                                                                    btn_DeclineRequest.setEnabled(false);
//                                                                    ===================================
                                                                    AcceptRequestGenerateNotification();
//                                                                    ===================================
                                                                    Toast.makeText(UserSearchProfileActivity.this, "de successfully", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    String msg = task.getException().getMessage();
                                                                    Toast.makeText(UserSearchProfileActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });


                                            } else {
                                                Toast.makeText(UserSearchProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserSearchProfileActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AcceptRequestGenerateNotification() {
        Map<String, Object> AcceptObject = new HashMap<>();

        AcceptObject.put("seen", 0);
        AcceptObject.put("sent_by", currentUserId); //commentedBy
        AcceptObject.put("sent_to", searchedUserId);
        AcceptObject.put("timestamp", System.currentTimeMillis());
        AcceptObject.put("type", 2);

        String randomId = reference.push().getKey();
        NotificationsRef.child(randomId).setValue(AcceptObject);
    }

    private void RetrievingInformationOfSearchedUser() {
        UsersReference.child(searchedUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String profilePic = dataSnapshot.child("profile_img").getValue().toString();
                            String firstName = dataSnapshot.child("first_name").getValue().toString();
                            String lastName = dataSnapshot.child("last_name").getValue().toString();
                            String username = dataSnapshot.child("username").getValue().toString();
                            String course = dataSnapshot.child("current_course").getValue().toString();
                            String university = dataSnapshot.child("university_name").getValue().toString();
                            String courseLevel = dataSnapshot.child("course_level").getValue().toString();
                            String gender = dataSnapshot.child("gender").getValue().toString();


//                            SETTING VALUES
                            Picasso.get().load(profilePic).into(imageView_ProfilePic);
                            textView_FullName.setText(firstName + " " + lastName);
                            textView_UserName.setText("@" + username);
                            textView_University.setText(course + " From " + university);
                            textView_GraduationLevel.setText(courseLevel);
                            textView_Gender.setText(gender);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(UserSearchProfileActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void CancelFriendRequest() {
        FriendRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final String onCancelNotificationId = snapshot.child("notification").getValue().toString();
                        String sendByUser = snapshot.child("sent_by").getValue().toString();
                        String sendToUser = snapshot.child("sent_to").getValue().toString();

                        if ((sendByUser.equals(currentUserId) && sendToUser.equals(searchedUserId)) ||
                                (sendByUser.equals(searchedUserId) && sendToUser.equals(currentUserId))) {
                            String key = snapshot.getKey();

//                            DELETE CHILD ON CANCEL REQUEST
                            FriendRequestRef.child(key).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {


                                                NotificationsRef.child(onCancelNotificationId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    confirm_decline_layout.setVisibility(View.GONE);
                                                                    btn_Respond.setVisibility(View.GONE);
                                                                    btn_ConfirmRequest.setVisibility(View.GONE);
                                                                    btn_ConfirmRequest.setVisibility(View.INVISIBLE);
                                                                    btn_ConfirmRequest.setEnabled(false);

//                                                                    ================================

                                                                    btn_SendRequest.setEnabled(true);
                                                                    btn_SendRequest.setVisibility(View.VISIBLE);
                                                                    CURRENT_STATE = "not_friends";
                                                                    requestText.setText("Add links");

                                                                    btn_DeclineRequest.setVisibility(View.GONE);
                                                                    btn_DeclineRequest.setEnabled(false);
                                                                } else {
                                                                    String msg = task.getException().getMessage();
                                                                    Toast.makeText(UserSearchProfileActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });


                                            } else {
                                                Toast.makeText(UserSearchProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
//                        CURRENT_STATE = "request_sent";
//                        btn_SendRequest.setText("cancel request");
//                        btn_DeclineRequest.setVisibility(View.INVISIBLE);
//                        btn_DeclineRequest.setEnabled(false);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserSearchProfileActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CheckFriendRequestAlreadySent() {

        FriendRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String sendByUser = snapshot.child("sent_by").getValue().toString();
                        String sendToUser = snapshot.child("sent_to").getValue().toString();

                        if ((sendByUser.equals(currentUserId) && sendToUser.equals(searchedUserId))) {
//                            btn_ConfirmRequest.setVisibility(View.GONE);
                            confirm_decline_layout.setVisibility(View.GONE);
                            btn_Respond.setVisibility(View.GONE);
//                            ===================================
                            CURRENT_STATE = "request_sent";
                            requestText.setText("cancel link");
                            btn_DeclineRequest.setVisibility(View.GONE);
                            btn_DeclineRequest.setEnabled(false);

                        } else if ((sendByUser.equals(searchedUserId) && sendToUser.equals(currentUserId))) {
                            btn_ConfirmRequest.setVisibility(View.VISIBLE);
                            confirm_decline_layout.setVisibility(View.VISIBLE);
                            btn_Respond.setVisibility(View.VISIBLE);
                            btn_SendRequest.setVisibility(View.GONE);
//                            btn_SendRequest.setVisibility(View.INVISIBLE);
//                            =======================================
                            CURRENT_STATE = "request_received";
                            requestText.setText("Accept");

                            btn_DeclineRequest.setEnabled(true);
                            btn_DeclineRequest.setVisibility(View.VISIBLE);

//                            RESPOND CLICK LISTENER
                            btn_Respond.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(getApplicationContext(), RespondActivity.class));
                                    Toast.makeText(UserSearchProfileActivity.this, "respond click", Toast.LENGTH_SHORT).show();
                                }
                            });

                            btn_DeclineRequest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CancelFriendRequest();
                                }
                            });
                        } else {
//                            btn_ConfirmRequest.setVisibility(View.GONE);
//                            btn_Respond.setVisibility(View.GONE);
//                            confirm_decline_layout.setVisibility(View.GONE);
//                            btn_DeclineRequest.setVisibility(View.GONE);
//                            btn_DeclineRequest.setVisibility(View.INVISIBLE);
//                            btn_Respond.setEnabled(false);
//                            btn_ConfirmRequest.setEnabled(false);
//                            btn_DeclineRequest.setEnabled(false);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserSearchProfileActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SendFriendRequestToaPerson() {

        friendRequestId = reference.push().getKey();
        timeStamp = Double.parseDouble(String.valueOf(System.currentTimeMillis()));
        notificationId = reference.push().getKey();

        FriendRequestModelClass friendRequestModelClass = new FriendRequestModelClass(notificationId, currentUserId, searchedUserId, timeStamp);
        FriendRequestRef.child(friendRequestId)
                .setValue(friendRequestModelClass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            btn_ConfirmRequest.setVisibility(View.GONE);
//                            btn_Respond.setVisibility(View.GONE);
//                            confirm_decline_layout.setVisibility(View.GONE);
//                            ===================================
                            btn_SendRequest.setEnabled(true);
                            CURRENT_STATE = "request_sent";
                            requestText.setText("cancel link");

                            btn_DeclineRequest.setVisibility(View.GONE);
                            btn_DeclineRequest.setEnabled(false);

                            NotificationsChildFirebase();
                        } else {
                            Toast.makeText(UserSearchProfileActivity.this, "error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void NotificationsChildFirebase() {

        Map<String, Object> notifyObject = new HashMap<>();
        notifyObject.put("seen", 0);
        notifyObject.put("sent_by", currentUserId);
        notifyObject.put("sent_to", searchedUserId);
        notifyObject.put("timestamp", timeStamp);
        notifyObject.put("type", 3);

        NotificationsRef.child(notificationId).setValue(notifyObject);


    }


    //    VIEW HOLDER
    public static class OwnPostViewHolder extends RecyclerView.ViewHolder {
        View mView;


        private ImageView postProfilePic, postImage, imageView_LikeUnlike;
        private TextView postUserName, postTime, postMessages, postTotalComments, postTotalLikes;
        private LinearLayout postCommentBtn, linearLayout_LikedPeople, postUnLikeStateBtn;
        private RelativeLayout postMenuOption;


        public OwnPostViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            postUnLikeStateBtn = (LinearLayout) itemView.findViewById(R.id.postUnLikeStateBtn);
            linearLayout_LikedPeople = (LinearLayout) itemView.findViewById(R.id.linearLayout_LikedPeople);
            postProfilePic = (ImageView) itemView.findViewById(R.id.postProfilePic);
            postImage = (ImageView) itemView.findViewById(R.id.postImage);
            postUserName = (TextView) itemView.findViewById(R.id.postUserName);
            postTime = (TextView) itemView.findViewById(R.id.postTime);
            postMessages = (TextView) itemView.findViewById(R.id.postMessages);
            postTotalComments = (TextView) itemView.findViewById(R.id.postTotalComments);
            postTotalLikes = (TextView) itemView.findViewById(R.id.postTotalLikes);
            imageView_LikeUnlike = (ImageView) itemView.findViewById(R.id.imageView_LikeUnlike);
            postCommentBtn = (LinearLayout) itemView.findViewById(R.id.postCommentBtn);
            postMenuOption = (RelativeLayout) itemView.findViewById(R.id.postMenuOption);


        }
    }


}

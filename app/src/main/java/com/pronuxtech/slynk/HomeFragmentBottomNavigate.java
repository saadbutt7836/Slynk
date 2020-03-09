package com.pronuxtech.slynk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragmentBottomNavigate extends Fragment {
    //    VIEWS
    private View view;

    //    ARRAYLISTS AND ADAPTERS
    private List<PostDataHomePage> postLists;
    //    PostDataAdapterHomePage adapter;
//    private List<StoryDataHomePage> storyLists;


    private FirebaseRecyclerAdapter<PostDataHomePage, PostViewHolder> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<PostDataHomePage> options;


    //    WIDGETS
    public RecyclerView recycleView;
//    storyRecycleView;

    //    VARIABLES
    private String
            postKey;
//    currentUserID;

    List<String> extraList;
    int type, likesCount, comments, likes;
    double timeStamp;
    String id, timeAgo, friendsId;
    String currentUserID;
    String typeString, likesString, commentsString, timeStampString, idString, postImageString, postedByString, statusString;

    private DatabaseReference UsersReference, postReference, NotificationRef, reference, FriendsRef, extraRef;
    private FirebaseAuth firebaseAuth;

    Query query;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        VIEWS CASTINGS
        view = inflater.inflate(R.layout.home_fragment_bottom_navigate, container, false);
//        storyRecycleView = (RecyclerView) view.findViewById(R.id.storyRecycleView);

//        displayAllaUsersPosts();
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        postReference = FirebaseDatabase.getInstance().getReference("Posts");
        NotificationRef = FirebaseDatabase.getInstance().getReference("notifications");
//        postReference.keepSynced(true);


//        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recycleView = (RecyclerView) view.findViewById(R.id.recycleView);
        postLists = new ArrayList<>();
        extraList = new ArrayList<>();


        recycleView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);

        recycleView.setLayoutManager(linearLayoutManager);
//        firebaseRecyclerAdapter.startListening();
//  STORY RECYCLEVIEW CALL
//        storyRecycleView();

//
//  POST RECYCLEVIEW CALL
//        postRecycleView();

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            currentUserID = firebaseAuth.getCurrentUser().getUid();
            FriendsRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID).child("friends");
        }


        options = new FirebaseRecyclerOptions.Builder<PostDataHomePage>()
                .setQuery(postReference, PostDataHomePage.class).build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PostDataHomePage, PostViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final PostViewHolder holder, final int position, @NonNull final PostDataHomePage model) {

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            if (model.likes.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                // Unstar the post and remove self from stars
                                holder.imageView_LikeUnlike.setImageResource(R.drawable.liked);
                            }
                            if (model.getLikesCount() > 0) {
                                holder.linearLayout_LikedPeople.setVisibility(View.VISIBLE);
                            }
                        }


                        int type = model.getType();
                        postKey = getRef(position).getKey();

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
                                        Toast.makeText(getActivity(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                        holder.linearLayout_LikedPeople.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), LikedPeopleActivity.class);
                                intent.putExtra("postId", getRef(position).getKey());
                                startActivity(intent);
                            }
                        });

                        holder.postUnLikeStateBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DatabaseReference databaseReference;

                                databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(getRef(position).getKey());
                                String postId = model.getPid();
                                String postedBy = model.getPosted_by();
                                onStarClicked(databaseReference, postId, postedBy);
//                                ======================GENERATE NOTIFICATION========================


//                                =======================GENERATE NOTIFICATION==========================
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

                                Intent intent = new Intent(getActivity(), PostCommentsActivity.class);
                                intent.putExtra("postId", getRef(position).getKey());
                                startActivity(intent);
                            }
                        });
                        holder.postProfilePic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String searchedUserKey = model.getPosted_by();
                                Intent intent = new Intent(getActivity(), UserSearchProfileActivity.class);
                                intent.putExtra("search_user_key", searchedUserKey);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        holder.postUserName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String searchedUserKey = model.getPosted_by();
                                Intent intent = new Intent(getActivity(), UserSearchProfileActivity.class);
                                intent.putExtra("search_user_key", searchedUserKey);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_recycle_view, parent, false);

                        return new PostViewHolder(view);

                    }
                };


        recycleView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

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


//                    GENERATE NOTIFICATIONS

                    Map<String, Object> likeObject = new HashMap<>();

                    likeObject.put("post_id", posId);
                    likeObject.put("sent_by", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    likeObject.put("sent_to", sendTolike);
                    likeObject.put("timestamp", System.currentTimeMillis());
                    likeObject.put("type", 0);

                    String randomId = reference.push().getKey();
                    NotificationRef.child(randomId).setValue(likeObject);


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


    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }


    public class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;


        private ImageView postProfilePic, postImage, imageView_LikeUnlike;
        private TextView postUserName, postTime, postMessages, postTotalComments, postTotalLikes;
        private LinearLayout postCommentBtn, linearLayout_LikedPeople, postUnLikeStateBtn;
        private RelativeLayout postMenuOption;


        public PostViewHolder(@NonNull View itemView) {
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


        public void setStatus(String status) {
            TextView postMessages = (TextView) mView.findViewById(R.id.postMessages);
            postMessages.setText(status);
        }

        public void setTimestamp(double timestamp) {
            TextView postTime = (TextView) mView.findViewById(R.id.postTime);
            postTime.setText(String.valueOf(timestamp));
        }

        public void setComments(int comments) {
            TextView postTotalComments = (TextView) mView.findViewById(R.id.postTotalComments);
            postTotalComments.setText(String.valueOf(comments));
        }

        public void setPost_image(String post_image) {
            ImageView postImage = (ImageView) mView.findViewById(R.id.postImage);
            Picasso.get().load(post_image).into(postImage);

        }

        public void setType(int type) {
            TextView postTime = (TextView) mView.findViewById(R.id.postTime);
            postTime.setText(String.valueOf(type));
        }

        public void setLikesCount(int likesCount) {
            TextView postTotalLikes = (TextView) mView.findViewById(R.id.postTotalLikes);
            postTotalLikes.setText(String.valueOf(likesCount));
        }


    }

}

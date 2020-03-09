package com.pronuxtech.slynk;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class PostCommentsActivity extends AppCompatActivity implements View.OnClickListener {

    //    VIEWS
    View rootView;

    //    WIDGETS
    private RecyclerView commentsRecycleView;
    private EmojiconEditText editText_Comments;
    private EmojIconActions emojIconActions;
    private ImageView chatEmojisButton, commentsBackArrow;
    private LinearLayout commentSendBtn;

    //    VARIABLES
    private String comment, commentId, postId, commentBody, postedBy, currentUserId;
    private Double timeStamp;
    private Boolean postCommentBool;

    //    ARRAYS AND ADAPTERS
    List<PostCommentsClass> commentsLists;
    PostCommentsAdapter adapter;


    //    DATABASE REFERENCES
    private DatabaseReference postCommentDBReference, postCommentBoolDBReference, NotoficationRef, reference, PostRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);

        getSupportActionBar().hide();

//        DATABSE REFERENCES
        reference = FirebaseDatabase.getInstance().getReference();
        PostRef = FirebaseDatabase.getInstance().getReference("Posts");
        postCommentDBReference = FirebaseDatabase.getInstance().getReference().child("comments");
        postCommentBoolDBReference = FirebaseDatabase.getInstance().getReference().child("post-comments");
        NotoficationRef = FirebaseDatabase.getInstance().getReference("notifications");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();


        rootView = (LinearLayout) findViewById(R.id.commentLayout);
        commentsRecycleView = (RecyclerView) findViewById(R.id.commentsRecycleView);
        editText_Comments = (EmojiconEditText) findViewById(R.id.editText_Comments);
        chatEmojisButton = (ImageView) findViewById(R.id.chatEmojisButton);
        commentsBackArrow = (ImageView) findViewById(R.id.commentsBackArrow);
        commentSendBtn = (LinearLayout) findViewById(R.id.commentSendBtn);
        reference = FirebaseDatabase.getInstance().getReference();


//        EMOJI KEYBOARD HANDLING
        emojIconActions = new EmojIconActions(this, rootView, editText_Comments, chatEmojisButton);
        emojIconActions.ShowEmojIcon();

//      CLICK LISTENERS
        commentSendBtn.setOnClickListener(this);
        commentsBackArrow.setOnClickListener(this);

//        GET VALUE FROM "PostDataHomePageAdapter"
        postId = getIntent().getStringExtra("postId");
//        ARRAY LIST INITIALIZE

        commentsLists = new ArrayList<PostCommentsClass>();
//        ADAPTER INITIALIZE


//        COMMENTS SHOW
        commentShow();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentsBackArrow: {
                onBackPressed();
                break;
            }

            case R.id.commentSendBtn:

                comment = editText_Comments.getText().toString().trim();
                editText_Comments.clearFocus();
                editText_Comments.setText(null);

                if (comment.isEmpty()) {
                    return;
                }

                commentSaveFirebase();

                break;
        }
    }


    private void commentSaveFirebase() {

        timeStamp = Double.valueOf(System.currentTimeMillis());

        PostCommentsClass postCommentsClass = new PostCommentsClass(comment, currentUserId, timeStamp);

        commentId = postCommentDBReference.push().getKey();

        postCommentDBReference.child(commentId)
                .setValue(postCommentsClass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            postCommentBool = true;

                            commentBoolFirebase();
                        } else {
                            Toast.makeText(PostCommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        Toast.makeText(getApplicationContext(), "comment:" + comment, Toast.LENGTH_SHORT).show();
    }

    private void commentBoolFirebase() {


        if (postCommentBool == true) {
            postCommentBoolDBReference.child(postId).child(commentId).setValue(postCommentBool)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

//                                ===========================COMMENT NOTIFICATION=========================

                                PostRef.child(postId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("posted_by")) {
                                            String commentTo = dataSnapshot.child("posted_by").getValue().toString();
                                            Map<String, Object> commentObject = new HashMap<>();

                                            commentObject.put("post_id", postId);
                                            commentObject.put("sent_by", currentUserId); //commentedBy
                                            commentObject.put("sent_to", commentTo);
                                            commentObject.put("timestamp", System.currentTimeMillis());
                                            commentObject.put("type", 1);

                                            String randomId = reference.push().getKey();
                                            NotoficationRef.child(randomId).setValue(commentObject);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

//                                ===========================COMMENT NOTIFICATION=========================
                                Toast.makeText(PostCommentsActivity.this, "Comment Posted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PostCommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void commentShow() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("post-comments").child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.d("aa", String.valueOf(snapshot.getKey()));


                    final String extra = String.valueOf(snapshot.getKey());
//                    ANY NEW COMMENT ARRAYLIST EMPTY
                    commentsLists.clear();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments").child(extra);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                            if (dataSnapshot1.hasChild("body")) {
                                commentBody = String.valueOf(dataSnapshot1.child("body").getValue());
                            }
                            if (dataSnapshot1.hasChild("posted_by")) {
                                postedBy = String.valueOf(dataSnapshot1.child("posted_by").getValue());
                            }
                            if (dataSnapshot1.hasChild("timestamp")) {
                                timeStamp = Double.valueOf(dataSnapshot1.child("timestamp").getValue().toString());
                            }
//                            Log.d("qw", extra + " " + commentBody);
//
//                            Log.d("qw", extra + " " + postedBy);
//
//                            Log.d("qw", extra + " " + timeStamp);
                            PostCommentsClass postCommentsClass = new PostCommentsClass(commentBody, postedBy, timeStamp);
                            commentsLists.add(postCommentsClass);

                            adapter = new PostCommentsAdapter(getApplicationContext(), commentsLists);
                            commentsRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            commentsRecycleView.setAdapter(adapter);


                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            String msg = databaseError.toException().getMessage();
                            Toast.makeText(PostCommentsActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String msg = databaseError.toException().getMessage();
                Toast.makeText(PostCommentsActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
            }
        });


//        Query query = FirebaseDatabase.getInstance().getReference().child("post-comments").orderByKey().equalTo(iteratorId);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    postCommentsClass = snapshot.getValue(PostCommentsClass.class);
//
//                    commentsLists.add(postCommentsClass);
//                }
//
//                Toast.makeText(PostCommentsActivity.this, String.valueOf(commentsLists.add(postCommentsClass)), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(PostCommentsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}

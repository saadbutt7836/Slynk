package com.pronuxtech.slynk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AccountViewProfileActivity extends AppCompatActivity implements View.OnClickListener {


    //    WIDGETS
    private ImageView ViewProfile_ProfileEdited, imageView_ProfileEditIcon;
    private Button btn_EditProfile;
    private RecyclerView currentUserRecycleView;
    private TextView ViewProfile_FullName, ViewProfile_UserName, ViewProfile_University, ViewProfile_GraduationLevel, ViewProfile_Gender;

    //    CONSTANTS
    private static final int GALLERY_PERMISSION_CODE = 1002;
    private static final int IMAGE_PICK_CODE = 1003;

    //    URI
    Uri imageUri, resultUri;

    //    VARIABLES
    private String currentUserId, timeAgo;

    private FirebaseRecyclerAdapter<PostDataHomePage, CurrentOwnPostViewHolder> adapter;
    private FirebaseRecyclerOptions<PostDataHomePage> options;

    private DatabaseReference UsersReference, PostReference,NotificationsRef,reference;
    private StorageReference ProfileRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_view_profile);

//        CASTINGS
        btn_EditProfile = (Button) findViewById(R.id.btn_EditProfile);
        ViewProfile_ProfileEdited = (ImageView) findViewById(R.id.ViewProfile_ProfileEdited);
        imageView_ProfileEditIcon = (ImageView) findViewById(R.id.imageView_ProfileEditIcon);
        ViewProfile_FullName = (TextView) findViewById(R.id.ViewProfile_FullName);
        ViewProfile_UserName = (TextView) findViewById(R.id.ViewProfile_UserName);
        ViewProfile_University = (TextView) findViewById(R.id.ViewProfile_University);
        ViewProfile_GraduationLevel = (TextView) findViewById(R.id.ViewProfile_GraduationLevel);
        ViewProfile_Gender = (TextView) findViewById(R.id.ViewProfile_Gender);
        currentUserRecycleView = (RecyclerView) findViewById(R.id.currentUserRecycleView);

        //        CLICK LISTENERS
        imageView_ProfileEditIcon.setOnClickListener(this);
        btn_EditProfile.setOnClickListener(this);

//        FIREBASE CASTINGS
        firebaseAuth = FirebaseAuth.getInstance();
        UsersReference = FirebaseDatabase.getInstance().getReference("Users");
        PostReference = FirebaseDatabase.getInstance().getReference("Posts");
        NotificationsRef = FirebaseDatabase.getInstance().getReference("notifications");
        reference = FirebaseDatabase.getInstance().getReference();
        ProfileRef = FirebaseStorage.getInstance().getReference();

        currentUserRecycleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);
        currentUserRecycleView.setLayoutManager(linearLayoutManager);


//        CURRENTuSERID
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        RetrieveCurrentUserData();

    }


    @Override
    protected void onStart() {
        super.onStart();

        Query query = PostReference.orderByChild("posted_by").equalTo(currentUserId);

        options = new FirebaseRecyclerOptions.Builder<PostDataHomePage>()
                .setQuery(query, PostDataHomePage.class).build();
        adapter = new FirebaseRecyclerAdapter<PostDataHomePage, CurrentOwnPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CurrentOwnPostViewHolder holder, final int position, @NonNull final PostDataHomePage model) {
                if (model.likes.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    // Unstar the post and remove self from stars
                    holder.imageView_LikeUnlike.setImageResource(R.drawable.liked);
                }
                if (model.getLikesCount() > 0) {
                    holder.linearLayout_LikedPeople.setVisibility(View.VISIBLE);
                }


                int type = model.getType();
//                postKey = getRef(position).getKey();

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
            public CurrentOwnPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_recycle_view, parent, false);

                return new CurrentOwnPostViewHolder(view);
            }
        };
        currentUserRecycleView.setAdapter(adapter);
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


    @Override
    public void onClick(View item) {
        switch (item.getId()) {
            case R.id.imageView_ProfileEditIcon: {
                galleryPermission();
                break;
            }
            case R.id.btn_EditProfile: {
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
            }
        }
    }

    private void RetrieveCurrentUserData() {
        UsersReference.child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("profile_img")) {
                                String profilePic = dataSnapshot.child("profile_img").getValue().toString();
                                Picasso.get().load(profilePic).into(ViewProfile_ProfileEdited);
                            }
                            if (dataSnapshot.hasChild("first_name") && dataSnapshot.hasChild("last_name")) {
                                String fName = dataSnapshot.child("first_name").getValue().toString();
                                String lName = dataSnapshot.child("last_name").getValue().toString();
                                ViewProfile_FullName.setText(fName + " " + lName);
                            }
                            if (dataSnapshot.hasChild("username")) {
                                String userName = dataSnapshot.child("username").getValue().toString();
                                ViewProfile_UserName.setText("@" + userName);
                            }
                            if (dataSnapshot.hasChild("current_course") && dataSnapshot.hasChild("university_name")) {
                                String course = dataSnapshot.child("current_course").getValue().toString();
                                String university = dataSnapshot.child("university_name").getValue().toString();
                                ViewProfile_University.setText(course + " From " + university);
                            }
                            if (dataSnapshot.hasChild("course_level")) {
                                String courseLevel = dataSnapshot.child("course_level").getValue().toString();
                                ViewProfile_GraduationLevel.setText(courseLevel);
                            }
                            if (dataSnapshot.hasChild("gender")) {
                                String gender = dataSnapshot.child("gender").getValue().toString();
                                ViewProfile_Gender.setText(gender);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "error: " + databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void galleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

//                PERMISSION NOT ENABLED REQUEST IT
                String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

//                        SHOW POPUP TO REQUEST PERMISSION
                requestPermissions(galleryPermissions, GALLERY_PERMISSION_CODE);

            } else {
                openGallery();
            }
        } else {
//                    SYSTEM OS LESS THAN MARSHMALLOW
            openGallery();
        }
    }

    private void openGallery() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }


    //    OVERRIDE METHODS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
//                    PERMISSION FROM POPUP WAS DENIED
                    Toast.makeText(this, "Gallery Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                if (resultUri != null) {
                    ProfilePicUploaded();
                }
            }
        }

    }

    private void ProfilePicUploaded() {
        final StorageReference reference = ProfileRef.child("users/").child("profiles/" + currentUserId);
        reference.putFile(resultUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUri = uri.toString();
                                            Map<String, Object> profileUri = new HashMap<>();
                                            profileUri.put("profile_img", downloadUri);
                                            UsersReference.child(currentUserId)
                                                    .updateChildren(profileUri);
                                        }
                                    });
                            Toast.makeText(AccountViewProfileActivity.this, "profile updated...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public static class CurrentOwnPostViewHolder extends RecyclerView.ViewHolder {

        View mView;


        private ImageView postProfilePic, postImage, imageView_LikeUnlike;
        private TextView postUserName, postTime, postMessages, postTotalComments, postTotalLikes;
        private LinearLayout postCommentBtn, linearLayout_LikedPeople, postUnLikeStateBtn;
        private RelativeLayout postMenuOption;

        public CurrentOwnPostViewHolder(@NonNull View itemView) {
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

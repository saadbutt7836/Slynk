package com.pronuxtech.slynk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static android.app.Activity.RESULT_OK;

public class UploadPostFragmentBottomNavigate extends Fragment implements View.OnClickListener {

    //    VIEWS
    private View rootView;
    private View view;

    //    WIDGETS
    private ImageView imageView_emojis, imageView_PostPic, imageView_CameraIcon, imageView_GalleryIcon;
    private EmojiconEditText editText_PostText;
    private TextView post_userName, btn_Post, textView_CharCount;
    private EmojIconActions emojIconActions;
    private CircleImageView userProfile;

    //   VARIABLES
    private int type, likesCount, comments;
    private double timeStamp;
    private String postedBy, status, postImage, stringImageUri, postId;
    ;

    //    CONSTANTS
    private static final int GALLERY_PERMISSION_CODE = 1002;
    private static final int IMAGE_PICK_CODE = 1003;

    //    URI
    Uri imageUri;

    //    DATABSE REFRENCES
    private DatabaseReference PostReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.upload_post_fragment_bottom_navigate, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        PostReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        storageReference = FirebaseStorage.getInstance().getReference();


//        WIDGETS CASTINGS
        rootView = (LinearLayout) view.findViewById(R.id.postLinearLayout);
        userProfile = (CircleImageView) view.findViewById(R.id.userProfile);
        imageView_emojis = (ImageView) view.findViewById(R.id.imageView_emojis);
        imageView_PostPic = (ImageView) view.findViewById(R.id.imageView_PostPic);
//        imageView_CameraIcon = (ImageView) view.findViewById(R.id.imageView_CameraIcon);
        imageView_GalleryIcon = (ImageView) view.findViewById(R.id.imageView_GalleryIcon);
        editText_PostText = (EmojiconEditText) view.findViewById(R.id.editText_PostText);
        post_userName = (TextView) view.findViewById(R.id.post_userName);
        textView_CharCount = (TextView) view.findViewById(R.id.textView_CharCount);
        btn_Post = (TextView) view.findViewById(R.id.btn_Post);

        //        EMOJIS KEYBOARD SET
        emojIconActions = new EmojIconActions(getActivity(), rootView, editText_PostText, imageView_emojis);
        emojIconActions.ShowEmojIcon();

//        VARIABLES INITIALIZE
        stringImageUri = "";
        status = "";
        postedBy = "";
        status = "";
        postImage = "";
        likesCount = 0;
        comments = 0;
        type = 0;
        CurrentUseValueGet();


//        CLICK LISTENERS
        imageView_GalleryIcon.setOnClickListener(this);
        btn_Post.setOnClickListener(this);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

//        editText_PostText.setText("");
        editText_PostText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String currentText = editable.toString();
                int currentLength = currentText.length();
                currentLength = 160 - currentLength;
                textView_CharCount.setText(currentLength + " / 160");
            }
        });
    }

    private void CurrentUseValueGet() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("first_name") && dataSnapshot.hasChild("last_name")) {
                        String firstName = dataSnapshot.child("first_name").getValue().toString();
                        String lastName = dataSnapshot.child("last_name").getValue().toString();
                        post_userName.setText(firstName + " " + lastName);
                    }
                    if (dataSnapshot.hasChild("profile_img")) {
                        String profileImageUri = dataSnapshot.child("profile_img").getValue().toString();
                        Picasso.get().load(profileImageUri).into(userProfile);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView_GalleryIcon: {
                galleryPermission();
                break;
            }
            case R.id.btn_Post: {
//                String timeStamp = new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss'Z'").format(new Timestamp(System.currentTimeMillis()));
//                Calendar calendar = Calendar.getInstance();
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
//                String currentTime = simpleDateFormat.format(calendar.getTimeInMillis());


                postedBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
                status = editText_PostText.getText().toString().trim();
                timeStamp = System.currentTimeMillis();

                if (status.isEmpty() && stringImageUri.isEmpty()) {
                    return;
                }
                if (!status.isEmpty() && stringImageUri.isEmpty()) {
                    type = 0;
                }
                if (status.isEmpty() && !stringImageUri.isEmpty()) {
                    type = 1;
                }
                if (!status.isEmpty() && !stringImageUri.isEmpty()) {
                    type = 2;
                }

                firebasePost();
                break;
            }
        }
    }


    private void galleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

//                PERMISSION NOT ENABLED REQUEST IT
                String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

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

    private void uploadStatusOnly() {
        postId = PostReference.push().getKey();

        HashMap<String, Object> postObject = new HashMap<>();
        postObject.put("pid", postId);
        postObject.put("post_image", postImage);
        postObject.put("posted_by", postedBy);
        postObject.put("status", status);
        postObject.put("timestamp", timeStamp);
        postObject.put("type", type);
        postObject.put("likesCount", likesCount);
        postObject.put("comments", comments);


        PostReference.child(postId).updateChildren(postObject)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Posted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), HomePageActivity.class));
                        } else {
                            String errorMsg = task.getException().getMessage();
                            Toast.makeText(getActivity(), "error: " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebasePost() {


        if (imageUri != null) {
            final StorageReference reference = storageReference.child("posts/").child("images/" + UUID.randomUUID().toString());
            reference.putFile(imageUri)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getActivity(), "Posting...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUri = uri;
                                    postImage = downloadUri.toString();

                                    uploadStatusOnly();

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            uploadStatusOnly();
//            Toast.makeText(getActivity(), "image not selected", Toast.LENGTH_SHORT).show();
        }

//
//        Fragment someFragment = new HomeFragmentBottomNavigate();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragmentContainer, someFragment); // give your fragment container id in first parameter
//        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
//        transaction.commit();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
//                    PERMISSION FROM POPUP WAS DENIED
                    Toast.makeText(getActivity(), "Gallery Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageUri = data.getData();
            stringImageUri = imageUri.toString();
            imageView_PostPic.setImageURI(imageUri);
        }
    }
}

package com.pronuxtech.slynk;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.scwang.wave.MultiWaveHeader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener {

    //   PERMISSIONS CONSTANTS
    private static final int CAMERA_PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int GALLERY_PERMISSION_CODE = 1002;
    private static final int IMAGE_PICK_CODE = 1003;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 1004;
    private static final int RECORD_AUDIO_PICK_CODE = 1005;
    private static final int DOCUMENTS_PERMISSION_CODE = 1006;
    private static final int DOCUMENTS_PICKER_CODE = 1007;
    private static final int VIDEO_GALLERY_PERMISSION_CODE = 1008;
    private static final int VIDEO_PICK_CODE = 1009;


    //    WIDGETS
    View rootView;
    private ImageView chatEmojisButton, chatAttach;
    private EmojiconEditText messagesEditText;
    private LinearLayout messageSendBtn, btnStartRecording, btnStopRecording;
    private EmojIconActions emojIconActions;
    private LinearLayout dialogLinearLayout, editTextHide;
    private RecyclerView recyclerView;
    private MultiWaveHeader wave;


    //    STRING VARIABLES
    private String audioFilePathSave = "", audioFile = "";
    private String currentUserId, message, receiverId, chatRoomId, currentSenderUserName, currentUserFirstName, receiverFirstName;

    private Double timeStamp;
    private int i = 0;
    private int timeLimit;

    private String myUrl;
    private String randomUUID = UUID.randomUUID().toString();

    Boolean create = true;

    //    LOCATION

    //    URIs
    private Uri fileUri, imageUri, videoUri;

    //    URI PATHs
    private String videoUriPath;
    private String videoThumbBase64;

    //    BITMAP
    private Bitmap compressedImagem, bitmapVideoThumbnail;
    private ProgressDialog progressDialog;
    //------------------------
    MediaRecorder mediaRecorder, mRecorder;
    MediaPlayer mediaPlayer;

    private Handler myHandler = new Handler();
    //--------------------------

    //    ARRAYLIST
    private final List<TextMessagesModelClass> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private ChatRoomAdapter adapter;


    //  DATABASE REFERENCES
    private DatabaseReference rootRef, UserReference, MessagesRef, ConversationRef;
    private StorageReference StorageRefer;
    private FirebaseAuth mAuth;

    Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        progressDialog = new ProgressDialog(this);

//        WIDGETS CASTINGS
        rootView = (RelativeLayout) findViewById(R.id.chatActivity);
        chatEmojisButton = (ImageView) findViewById(R.id.chatEmojisButton);
        chatAttach = (ImageView) findViewById(R.id.chatAttach);
        messagesEditText = (EmojiconEditText) findViewById(R.id.messagesEditText);
        messageSendBtn = (LinearLayout) findViewById(R.id.messageSendBtn);
        btnStartRecording = (LinearLayout) findViewById(R.id.btnStartRecording);
        btnStopRecording = (LinearLayout) findViewById(R.id.btnStopRecording);
        editTextHide = (LinearLayout) findViewById(R.id.editTextHide);
        wave = (MultiWaveHeader) findViewById(R.id.wave);

//        LOCATION

        wave.setVelocity(1);
        wave.setProgress(1);
        wave.isRunning();
        wave.setWaveHeight(40);
        wave.setGradientAngle(60);
        wave.setStartColor(Color.DKGRAY);
        wave.setCloseColor(Color.GREEN);

//        EMOJIS KEYBOARD SET
        emojIconActions = new EmojIconActions(this, rootView, messagesEditText, chatEmojisButton);
        emojIconActions.ShowEmojIcon();

//        RECEIEVE ID FROM "AllChatUserListActivity" OF Receiver
        receiverId = getIntent().getStringExtra("receiverId");

//        DATABSE REFERENCES CASTINGS
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        chatRoomId = "";
        StorageRefer = FirebaseStorage.getInstance().getReference();
        UserReference = rootRef.child("Users");
        MessagesRef = FirebaseDatabase.getInstance().getReference("messages");
        ConversationRef = rootRef.child("conversations");

//        OFFLINE FEATURES
        UserReference.keepSynced(true);
        MessagesRef.keepSynced(true);
        ConversationRef.keepSynced(true);


//        ARRAY LIST ADAPTER LAYOUT MANAGER
        adapter = new ChatRoomAdapter(messageList);
        recyclerView = (RecyclerView) findViewById(R.id.chat_RecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        RetrieveInfoOfCurrentUser();
        RetrieveInfoOfReceiverUser();

        //        ACTIONBAR HANDLE
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        CLICK LISTENERS
        messageSendBtn.setOnClickListener(this);
        chatAttach.setOnClickListener(this);

        crateChatRoomId(receiverId, currentUserId);
//        CLICKS LISTENERS START
        btnStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyKeyboard();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // GALLERY PERMISSION
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                        PERMISSION NOT ENABLED REQUEST IT?
                        String[] cameraPermission = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                        SHOW POPUP TO REQUEST PERMISSION
                        requestPermissions(cameraPermission, RECORD_AUDIO_PERMISSION_CODE);
                    } else {
//                        PERMISSION ALREADY GRANTED
                        audioFilePathSave = Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/" + randomUUID + ".m4a";
                        setupMediaRecorder();
                        try {
                            btnStartRecording.setVisibility(View.GONE);
                            btnStartRecording.animate().rotation(360).setDuration(1000);
                            btnStopRecording.setVisibility(View.VISIBLE);
                            btnStopRecording.animate().rotation(360).setDuration(1000);

                            mediaRecorder.prepare();
                            mediaRecorder.start();
                            Toast.makeText(ChatRoomActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
//                            HANDLER
                            timeLimit = 1;
                            final Runnable UpdateSongTime = new Runnable() {
                                public void run() {
                                    if (timeLimit <= 120) {
                                        timeLimit += 1;
                                        myHandler.removeCallbacks(this);
                                        myHandler.postDelayed(this, 1000);
                                        editTextHide.setVisibility(View.GONE);

//                                        SHOW WAVES
                                        wave.setVisibility(View.VISIBLE);
                                        Toast.makeText(ChatRoomActivity.this, "timer", Toast.LENGTH_SHORT).show();

                                    } else {
                                        btnStopRecording.setVisibility(View.GONE);
                                        btnStopRecording.animate().rotation(-360).setDuration(1000);
                                        btnStartRecording.setVisibility(View.VISIBLE);
                                        btnStartRecording.animate().rotation(-360).setDuration(1000);

                                        editTextHide.setVisibility(View.VISIBLE);
                                        wave.setVisibility(View.GONE);
//                THIS FUNCTION HANDLE THE STOP AUDIO RECORDING
                                        Toast.makeText(ChatRoomActivity.this, "time up", Toast.LENGTH_SHORT).show();
                                        uploadAudioFileToFirebase();
                                        mediaRecorder.stop();
                                        myHandler.removeCallbacks(this);
                                    }
//
                                }
                            };
                            UpdateSongTime.run();

                            btnStopRecording.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    btnStopRecording.setVisibility(View.GONE);
                                    btnStopRecording.animate().rotation(-360).setDuration(1000);
                                    btnStartRecording.setVisibility(View.VISIBLE);
                                    btnStartRecording.animate().rotation(-360).setDuration(1000);

//                THIS FUNCTION HANDLE THE STOP AUDIO RECORDING
//                audioRecordStopDialog();
                                    myHandler.removeCallbacks(UpdateSongTime);
                                    editTextHide.setVisibility(View.VISIBLE);
                                    wave.setVisibility(View.GONE);
                                    mediaRecorder.stop();
                                    Toast.makeText(ChatRoomActivity.this, "stop recirding", Toast.LENGTH_SHORT).show();
                                    uploadAudioFileToFirebase();
                                }
                            });


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        btnPlayRecording.setEnabled(false);
//                        btnStopPlayingRecording.setEnabled(false);

                    }
                } else {
                    //SYSTEM OS < MARSHMALLOW
                    setupMediaRecorder();
                    try {
                        btnStartRecording.setVisibility(View.GONE);
                        btnStartRecording.animate().rotation(360).setDuration(1000);
                        btnStopRecording.setVisibility(View.VISIBLE);
                        btnStopRecording.animate().rotation(360).setDuration(1000);

                        mediaRecorder.prepare();
                        mediaRecorder.start();

//                            HANDLER
                        timeLimit = 1;
                        final Runnable UpdateSongTime = new Runnable() {
                            public void run() {
                                if (timeLimit <= 120) {
                                    timeLimit += 1;
                                    myHandler.removeCallbacks(this);
                                    myHandler.postDelayed(this, 1000);
                                    editTextHide.setVisibility(View.GONE);

                                    wave.setVisibility(View.VISIBLE);


                                } else {
                                    btnStopRecording.setVisibility(View.GONE);
                                    btnStopRecording.animate().rotation(-360).setDuration(1000);
                                    btnStartRecording.setVisibility(View.VISIBLE);
                                    btnStartRecording.animate().rotation(-360).setDuration(1000);

                                    editTextHide.setVisibility(View.VISIBLE);
                                    wave.setVisibility(View.GONE);
//                THIS FUNCTION HANDLE THE STOP AUDIO RECORDING
                                    Toast.makeText(ChatRoomActivity.this, "time up", Toast.LENGTH_SHORT).show();
                                    uploadAudioFileToFirebase();
                                    mediaRecorder.stop();
                                    myHandler.removeCallbacks(this);
                                }
//
                            }
                        };
                        UpdateSongTime.run();

                        btnStopRecording.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                btnStopRecording.setVisibility(View.GONE);
                                btnStopRecording.animate().rotation(-360).setDuration(1000);
                                btnStartRecording.setVisibility(View.VISIBLE);
                                btnStartRecording.animate().rotation(-360).setDuration(1000);

//                THIS FUNCTION HANDLE THE STOP AUDIO RECORDING
//                audioRecordStopDialog();
                                uploadAudioFileToFirebase();
                                myHandler.removeCallbacks(UpdateSongTime);
                                editTextHide.setVisibility(View.VISIBLE);
                                wave.setVisibility(View.GONE);
                                mediaRecorder.stop();
                            }
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


    }

    private void RetrieveInfoOfReceiverUser() {
        UserReference.child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UsersInfoModelClass usersInfoModelClass = dataSnapshot.getValue(UsersInfoModelClass.class);
                    receiverFirstName = usersInfoModelClass.getFirst_name();
                    getSupportActionBar().setTitle(receiverFirstName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void crateChatRoomId(String receiverId, String currentUserId) {

        int value = currentUserId.compareTo(receiverId);

        if (value < 0) {
            chatRoomId = receiverId + currentUserId;
            Log.d("chatId", "if chalta he " + value);
        } else {
            chatRoomId = currentUserId + receiverId;
            Log.d("chatId", "else chalta he");
        }
        Log.d("chatId", chatRoomId);

        ArrayList<String> members;
        members = new ArrayList<>();
        members.add(currentUserId);
        members.add(receiverId);

        CreateResent(currentUserId, receiverId, chatRoomId, members, receiverFirstName, 0);
        CreateResent(receiverId, currentUserId, chatRoomId, members, receiverFirstName, 0);
    }

    private void CreateResent(final String sender, final String receiver, final String chattingRoomid,
                              final ArrayList<String> members, final String receiverFirstName, final int type) {

        Query query = ConversationRef.orderByChild("chatroom_id").equalTo(chattingRoomid);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Boolean create = true;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ConversationsModelClass conversationsModelClass = snapshot.getValue(ConversationsModelClass.class);
                        if (conversationsModelClass.getSent_by().equals(sender)) {
                            create = false;
                        }
                    }

                }
//
                if (create) {
                    Log.d("create", String.valueOf(create));
                    CreateConversation(sender, receiver, chattingRoomid,
                            members, receiverFirstName, type);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        if (create[0]) {
//            Log.d("create", String.valueOf(create[0]));
//            CreateConversation(sender, receiver, chattingRoomid,
//                    members, receiverFirstName, type);
//        }
    }

    private void CreateConversation(final String sender, String receiver, final String chattingRoomid,
                                    final ArrayList<String> members, final String receiverFirstName, final int type) {
        String sentByrandomId = rootRef.push().getKey();
        final Map<String, Object> sendByCurrentUserConverMap = new HashMap<>();

        sendByCurrentUserConverMap.put("chatroom_id", chattingRoomid);
        sendByCurrentUserConverMap.put("cid", sentByrandomId);
        sendByCurrentUserConverMap.put("last_message", "");
        sendByCurrentUserConverMap.put("sent_by", sender);
        sendByCurrentUserConverMap.put("sent_to", receiver);
        sendByCurrentUserConverMap.put("members", members);
        sendByCurrentUserConverMap.put("sent_to_name", receiverFirstName);
        sendByCurrentUserConverMap.put("timestamp", timeStamp);
        sendByCurrentUserConverMap.put("type", type);
        sendByCurrentUserConverMap.put("unread_counter", 0);

        Log.d("121", String.valueOf(true));


        ConversationRef.child(sentByrandomId)
                .updateChildren(sendByCurrentUserConverMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChatRoomActivity.this, "my conversation created", Toast.LENGTH_SHORT).show();

                        } else {
                            String msg = task.getException().getMessage();
                            Toast.makeText(ChatRoomActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chatAttach:
                closeKeyKeyboard();
                chatAttachementDialog();
                break;

            case R.id.messageSendBtn:
                message = messagesEditText.getText().toString().trim();
                if (!message.isEmpty()) {

                    saveMessageToFirebase();

                } else {
                    Toast.makeText(this, "not send", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        RetrieveMessages();

    }


    //ACTIONBAR MENU HANDLING
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.audioCallIcon:
                Toast.makeText(this, "Audio call Manage", Toast.LENGTH_SHORT).show();
                break;
            case R.id.videoCallIcon:
                Toast.makeText(this, "Video call Manage", Toast.LENGTH_SHORT).show();
                break;
            case R.id.searchOption:
                Toast.makeText(this, "Search Bar show", Toast.LENGTH_SHORT).show();
                break;
            case R.id.blockOption:
                Toast.makeText(this, "User Block", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    //    HANDLING PERMISSION RESULT
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        THIS METHOD CALL WHEN USER PRESS ALLOW OR DENY FROM PERMISSION REQUEST POPUP
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    PERMISSION FROM POPUP WAS GRANTED
                    openCamera();
                } else {
//                    PERMISSION FROM POPUP WAS DENIED
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case GALLERY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
//                    PERMISSION FROM POPUP WAS DENIED
                    Toast.makeText(this, "Gallery Permission Denied", Toast.LENGTH_SHORT).show();

                }
            }
            break;

            case VIDEO_GALLERY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    OpenVideoGallery();
                } else {
//                    PERMISSION FROM POPUP WAS DENIED
                    Toast.makeText(this, "Video gallery Permission Denied", Toast.LENGTH_SHORT).show();

                }
            }
            break;
            case RECORD_AUDIO_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    PERMISSION FROM POPUP WAS GRANTED
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
//                    PERMISSION FROM POPUP WAS DENIED
                    Toast.makeText(this, "Microphone Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case DOCUMENTS_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openDocuments();
                } else {
//                    PERMISSION FROM POPUP WAS DENIED
                    Toast.makeText(this, "Documents Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        CALL WHEN IMAGE WAS CAPTURED
        if (resultCode == RESULT_OK && requestCode == IMAGE_CAPTURE_CODE) {
            Toast.makeText(this, "Camera Settled", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null && data.getData() != null) {


            imageUri = data.getData();

            final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("messages/").child(chatRoomId).child("images/" + UUID.randomUUID().toString());

            storageReference.putFile(imageUri)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMax(100);
                            progressDialog.setMessage("image sending");
                            progressDialog.show();

                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUri = uri;
                                    String msgSendImage = downloadUri.toString();
                                    sendingImageToFirebase(msgSendImage);
                                }
                            });
                            Toast.makeText(ChatRoomActivity.this, "successfully uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatRoomActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        } else if (resultCode == RESULT_OK && requestCode == VIDEO_PICK_CODE && data != null) {

            videoUri = data.getData();
            String[] mediaColumns = {MediaStore.Video.Media.SIZE};
            Cursor cursor = this.getContentResolver().query(videoUri, mediaColumns, null, null, null);
            cursor.moveToFirst();
            int sizeColInd = cursor.getColumnIndex(mediaColumns[0]);
            long originalVideoSize = cursor.getLong(sizeColInd);
            cursor.close();
//            SIZE LIMIT 15mb
            long sizeLimit = 15000000;

            if (originalVideoSize <= sizeLimit) {
                Toast.makeText(this, "ok done", Toast.LENGTH_SHORT).show();


                //                GET THUMBNAIL FROM VIDEO
                bitmapVideoThumbnail = ThumbnailUtils
                        .createVideoThumbnail(getRealPathFromURIForVideo(videoUri), MediaStore.Video.Thumbnails.MINI_KIND);


                //                CONVERT TO BASE64
                videoThumbBase64 = ThumbnailToBase64(bitmapVideoThumbnail);


//                SEND TO FIREBASE STORAGE
                final StorageReference videoStorageRefer = StorageRefer
                        .child("messages/").child(chatRoomId).child("videos/" + UUID.randomUUID().toString() + ".mp4");


                videoStorageRefer.putFile(videoUri)
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.setMax(100);
                                progressDialog.setMessage("Uploading video");
                                progressDialog.show();
                                Toast.makeText(ChatRoomActivity.this, "video uploading...", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                videoStorageRefer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri videoUploadStorageUri = uri;
                                        String videoUploadStorageUriString = videoUploadStorageUri.toString();
                                        SendVideoToFirebase(videoUploadStorageUriString);
                                        Log.d("videoMsgSend", "upload to storage" + " msg sent");
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ChatRoomActivity.this, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                Toast.makeText(this, "Maximum 15Mb", Toast.LENGTH_SHORT).show();
            }


        } else if (resultCode == RESULT_OK && requestCode == DOCUMENTS_PICKER_CODE) {

            String documetsFilePath = data.getData().getPath();
            Toast.makeText(this, "Document Files Path Settled: " + documetsFilePath, Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_OK && requestCode == RECORD_AUDIO_PICK_CODE) {
            Toast.makeText(this, "Voice setted", Toast.LENGTH_SHORT).show();
        }
    }


    //    OPEN CHAT ATTACHEMENT DIALOG METHOD
    private void chatAttachementDialog() {
        View views = getLayoutInflater().inflate(R.layout.chat_attachment_modal_dialog, null);

        //                DIALOG WIDGETS
        final ImageButton chatGallery = (ImageButton) views.findViewById(R.id.chatGallery);
        ImageButton chatVideos = (ImageButton) views.findViewById(R.id.chatVideos);
//        final ImageButton chatColouredCamera = (ImageButton) views.findViewById(R.id.chatColouredCamera);
//        final ImageButton chatDocuments = (ImageButton) views.findViewById(R.id.chatDocuments);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatRoomActivity.this);

        alertDialog.setView(views);
        final AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();

//        VIDEO GALLERY PERMISSION
        chatVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog1.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                        PERMISSION NOT ENABLED REQUEST IT
                        String[] videoGalleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                        SHOW POPUP TO REQUEST PERMISSION
                        requestPermissions(videoGalleryPermissions, VIDEO_GALLERY_PERMISSION_CODE);
                    } else {
//                        PERMISSION GRANTED
                        OpenVideoGallery();
                    }
                } else {
//                    SYSTEM OS LESS THAN MARSHMALLOW
                    OpenVideoGallery();
                }
            }
        });


        //        IMAGE GALLERY CLICK PERMISSION
        chatGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog1.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                        PERMISSION NOT ENABLED REQUEST IT
                        String[] imageGalleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                        SHOW POPUP TO REQUEST PERMISSION
                        requestPermissions(imageGalleryPermissions, GALLERY_PERMISSION_CODE);
                    } else {
//                        PERMISSION GRANTED
                        openGallery();
                    }
                } else {
//                    SYSTEM OS LESS THAN MARSHMALLOW
                    openGallery();
                }
            }
        });

        //CAMERA CLICK PERMISSION
//        chatColouredCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog1.dismiss();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                     GALLERY PERMISSION
//                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                        PERMISSION NOT ENABLED REQUEST IT?
//                        String[] cameraPermission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                        SHOW POPUP TO REQUEST PERMISSION
//                        requestPermissions(cameraPermission, CAMERA_PERMISSION_CODE);
//                    } else {
//                        PERMISSION ALREADY GRANTED
//                        openCamera();
//                    }
//                } else {
        //SYSTEM OS < MARSHMALLOW
//                    openCamera();
//                }
//            }
//        });

//        DOCUMENTS CLICK PERMISSION
//        chatDocuments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                alertDialog1.dismiss();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                        PERMISSION NOT ENABLED REQUEST IT
//                        String[] documentsPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                        SHOW POPUP TO REQUEST PERMISSION
//                        requestPermissions(documentsPermissions, DOCUMENTS_PERMISSION_CODE);
//                    } else {
//                        PERMISSION GRANTED
//                        openDocuments();
//                    }
//                } else {
//                    SYSTEM OS LESS THAN MARSHMALLOW
//                    openDocuments();
//                }
//            }
//        });
    }


    //    OPEN CAMERA METHOD
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Sloop Pictures");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
//        PICTURES SAVE IN GALLERY
        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

//        CAMERA INTENT
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    //    OPEN GALLERY METHOD
    private void openGallery() {
        Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageIntent.setType("image/*");
        startActivityForResult(imageIntent.createChooser(imageIntent, "Select image"), IMAGE_PICK_CODE);
    }

    private void OpenVideoGallery() {
        Intent videoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        videoIntent.setType("video/*");
        startActivityForResult(videoIntent.createChooser(videoIntent, "Select video"), VIDEO_PICK_CODE);
    }

    //    OPEN DOCUMENTS METHOD
    private void openDocuments() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        startActivityForResult(fileIntent, DOCUMENTS_PICKER_CODE);
    }


    //    SETUP MEDIA RECORDER
    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(audioFilePathSave);
    }

    //    AUDIO CLICK OPEN DIALOG METHOD
    private void audioRecordStopDialog() {
        View views2 = getLayoutInflater().inflate(R.layout.audio_record_stoped_dialog, null);

        final LinearLayout btnPlayRecording = (LinearLayout) views2.findViewById(R.id.btnPlayRecording);
        final LinearLayout btnStopPlayingRecording = (LinearLayout) views2.findViewById(R.id.btnStopPlayingRecording);
        final TextView cancelAudio = (TextView) views2.findViewById(R.id.cancelAudio);
        final TextView sendAudio = (TextView) views2.findViewById(R.id.sendAudio);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatRoomActivity.this);


        alertDialog.setView(views2);
        final AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();


        btnPlayRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatRoomActivity.this, "Media Play", Toast.LENGTH_SHORT).show();

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioFilePathSave);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();

                btnStopPlayingRecording.setVisibility(View.VISIBLE);
                btnPlayRecording.setVisibility(View.GONE);
            }
        });

//        PLAY RECORDING STOP TO CLICK
        btnStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mediaPlayer != null) {
                    btnStopPlayingRecording.setVisibility(View.GONE);
                    btnPlayRecording.setVisibility(View.VISIBLE);
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    setupMediaRecorder();
                }
            }
        });

        sendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog1.dismiss();
//                uploadAudioFileToFirebase();
                Toast.makeText(ChatRoomActivity.this, "send Audio Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        cancelAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer = null;
                alertDialog1.dismiss();
                Toast.makeText(ChatRoomActivity.this, "Cancel Audio Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    RETRIEVE INFORMATION OF CURRENT USER
    private void RetrieveInfoOfCurrentUser() {
        UserReference.child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("username") && dataSnapshot.hasChild("first_name")) {
                            currentUserFirstName = dataSnapshot.child("first_name").getValue().toString();
                            currentSenderUserName = dataSnapshot.child("username").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        String msg = databaseError.toException().getMessage();
                        Toast.makeText(ChatRoomActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
                    }
                });


    }

//    //    CURRENT USER HASH MAP
//    private void CurrentUserHashMap() {
//        String sentByrandomId = rootRef.push().getKey();
//        final Map<String, Object> sendByCurrentUserConverMap = new HashMap<>();
//
//        sendByCurrentUserConverMap.put("chatroom_id", chatRoomId1);
//        sendByCurrentUserConverMap.put("cid", sentByrandomId);
//        sendByCurrentUserConverMap.put("last_message", "");
//        sendByCurrentUserConverMap.put("sent_by", currentUserId);
//        sendByCurrentUserConverMap.put("sent_to", receiverId);
//        sendByCurrentUserConverMap.put("sent_to_name", receiverFirstName);
//        sendByCurrentUserConverMap.put("timestamp", timeStamp);
//        sendByCurrentUserConverMap.put("type", 0);
//        sendByCurrentUserConverMap.put("unread_counter", 0);
//
////        List<String> membersArray = new ArrayList<>();
////        membersArray.size();
////        membersArray.add(currentUserId);
////        membersArray.add(receiverId);
////
////        sendByCurrentUserConverMap.put("members", membersArray);
//
//        ConversationRef.child(sentByrandomId)
//                .updateChildren(sendByCurrentUserConverMap)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(ChatRoomActivity.this, "my conversation created", Toast.LENGTH_SHORT).show();
//
//                        } else {
//                            String msg = task.getException().getMessage();
//                            Toast.makeText(ChatRoomActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//    }
//
//    //     RECEIVER HASH MAP
//    private void ReceiverUserHasMap() {
//        final String sentTorandomId = rootRef.push().getKey();
//
//        final Map<String, Object> sendToCurrentUserConverMap = new HashMap<>();
//
//        sendToCurrentUserConverMap.put("chatroom_id", chatRoomId1);
//        sendToCurrentUserConverMap.put("cid", sentTorandomId);
//        sendToCurrentUserConverMap.put("last_message", "");
//        sendToCurrentUserConverMap.put("sent_by", receiverId);
//        sendToCurrentUserConverMap.put("sent_to", currentUserId);
//        sendToCurrentUserConverMap.put("sent_to_name", currentUserFirstName);
//        sendToCurrentUserConverMap.put("timestamp", timeStamp);
//        sendToCurrentUserConverMap.put("type", 0);
//        sendToCurrentUserConverMap.put("unread_counter", 0);
//
//        ConversationRef.child(sentTorandomId)
//                .updateChildren(sendToCurrentUserConverMap)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            bothConversationExist = 1;
//                            Toast.makeText(ChatRoomActivity.this, "conversation created",
//                                    Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });
//
//    }

    //    RETRIEVE MESSAGES
    private void RetrieveMessages() {
//        Log.d("chatRoomId", chatRoomId);
//        messageList.clear();
        rootRef.child("messages").child(chatRoomId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        TextMessagesModelClass modelClass = dataSnapshot.getValue(TextMessagesModelClass.class);
                        messageList.add(modelClass);
                        adapter.notifyDataSetChanged();

                        recyclerView.smoothScrollToPosition(messageList.size());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        adapter.notifyDataSetChanged();
//                        recyclerView.smoothScrollToPosition(messageList.size());

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void saveMessageToFirebase() {
        timeStamp = Double.valueOf(System.currentTimeMillis());
        String msgrandomId = rootRef.push().getKey();

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("message_id", msgrandomId);
        messageMap.put("sender", mAuth.getCurrentUser().getUid());
        messageMap.put("sender_name", currentSenderUserName);
        messageMap.put("status", 0);
        messageMap.put("timestamp", timeStamp);
        messageMap.put("type", 0);

        rootRef.child("messages").child(chatRoomId)
                .child(msgrandomId)
                .updateChildren(messageMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            messagesEditText.setText("");
                            Toast.makeText(getApplicationContext(), "message sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "sending failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void sendingImageToFirebase(String imageUri) {
        timeStamp = Double.valueOf(System.currentTimeMillis());
        String imgRandomId = rootRef.push().getKey();

        Map<String, Object> imgMessageMap = new HashMap<>();
        imgMessageMap.put("image", imageUri);
        imgMessageMap.put("message", "picture");
        imgMessageMap.put("message_id", imgRandomId);
        imgMessageMap.put("sender", mAuth.getCurrentUser().getUid());
        imgMessageMap.put("sender_name", currentSenderUserName);
        imgMessageMap.put("status", 0);
        imgMessageMap.put("timestamp", timeStamp);
        imgMessageMap.put("type", 1);

        rootRef.child("messages").child(chatRoomId)
                .child(imgRandomId)
                .updateChildren(imgMessageMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            messagesEditText.setText("");
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "image sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "image failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadAudioFileToFirebase() {
        final StorageReference audioRef = StorageRefer
                .child("messages/")
                .child(chatRoomId)
                .child("audios/" + randomUUID + ".m4a");

        Uri audioUri = Uri.fromFile(new File(audioFilePathSave));

        audioRef.putFile(audioUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.setTitle("Audio");
                        progressDialog.setMessage("sending");
                        progressDialog.setProgress(100);
                        progressDialog.show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        audioRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri firebaseUri = uri;
                                String firebaseAudioStringUri = firebaseUri.toString();

                                savedAudioUriToFirebaseDatabase(firebaseAudioStringUri);


                            }
                        });

                        Toast.makeText(ChatRoomActivity.this, "audio finished", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void savedAudioUriToFirebaseDatabase(String firebaseAudioStringUri) {
        timeStamp = Double.valueOf(System.currentTimeMillis());
        String audioRandomId = rootRef.push().getKey();

        Map<String, Object> audioMessageMap = new HashMap<>();

        audioMessageMap.put("audio", firebaseAudioStringUri);
        audioMessageMap.put("message", "audio");
        audioMessageMap.put("message_id", audioRandomId);
        audioMessageMap.put("sender", mAuth.getCurrentUser().getUid());
        audioMessageMap.put("sender_name", currentSenderUserName);
        audioMessageMap.put("status", 0);
        audioMessageMap.put("timestamp", timeStamp);
        audioMessageMap.put("type", 2);

        rootRef.child("messages").child(chatRoomId)
                .child(audioRandomId).updateChildren(audioMessageMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatRoomActivity.this, "Audio sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatRoomActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendVideoToFirebase(String videoUploadStorageUriString) {
        timeStamp = Double.valueOf(System.currentTimeMillis());
        String videoRandomId = rootRef.push().getKey();

        Map<String, Object> videoMessageMap = new HashMap<>();

        videoMessageMap.put("message", "video");
        videoMessageMap.put("message_id", videoRandomId);
        videoMessageMap.put("sender", mAuth.getCurrentUser().getUid());
        videoMessageMap.put("sender_name", currentSenderUserName);
        videoMessageMap.put("status", 0);
        videoMessageMap.put("thumbnail", videoThumbBase64);
        videoMessageMap.put("timestamp", timeStamp);
        videoMessageMap.put("type", 3);
        videoMessageMap.put("video", videoUploadStorageUriString);

        rootRef.child("messages").child(chatRoomId)
                .child(videoRandomId).updateChildren(videoMessageMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatRoomActivity.this, "video sent to firebase database", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatRoomActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//==============================    RETURN VALUE METHODS =============================

    //    GET REAL PATH FROM URI FRO THUMBNAIL
    private String getRealPathFromURIForVideo(Uri selectedVideoUri) {
        String wholeID = DocumentsContract.getDocumentId(selectedVideoUri);
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Video.Media.DATA};
        String sel = MediaStore.Video.Media._ID + "=?";
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);


        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            videoUriPath = cursor.getString(columnIndex);
        }
        cursor.close();
        return videoUriPath;
    }

    //    THUMBNAIL TO BASE 64
    private String ThumbnailToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
        byte[] bytes = outputStream.toByteArray();
//        base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    //    INPUT SOFT KEYBOARD METHOD
    private void closeKeyKeyboard() {
        View keyboardView = this.getCurrentFocus();
        if (keyboardView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(keyboardView.getWindowToken(), 0);
        }
    }

}

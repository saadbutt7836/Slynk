package com.pronuxtech.slynk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RespondActivity extends AppCompatActivity {

    //    WIDGETES
    private RecyclerView respond_RecycleView;

    //    STRINGS VARIABLES
    String currentUserId;

    List<FriendRequestModelClass> friendList;
    //    DATABASE REFERENCES
    private DatabaseReference reference, UserRefer, FriendRequestRefer, NotificationsRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond);

        respond_RecycleView = (RecyclerView) findViewById(R.id.respond_RecycleView);


//        REFERENCES
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        UserRefer = FirebaseDatabase.getInstance().getReference("Users");
        FriendRequestRefer = FirebaseDatabase.getInstance().getReference("friend-requests");
        NotificationsRef = FirebaseDatabase.getInstance().getReference("notifications");

        friendList = new ArrayList<>();


    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOtherUserSendRequestToCurrentUser();

    }

    private void CheckOtherUserSendRequestToCurrentUser() {
        FriendRequestRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String sendTo = snapshot.child("sent_to").getValue().toString();

                        if (sendTo.equals(currentUserId)) {
                            String sendBy = snapshot.child("sent_by").getValue().toString();
                            String notifi = snapshot.child("notification").getValue().toString();
                            Double timeStamp = Double.valueOf(snapshot.child("timestamp").getValue().toString());

                            FriendRequestModelClass friendClass =
                                    new FriendRequestModelClass(notifi, sendBy, sendTo, timeStamp);

                            friendList.add(friendClass);
                            RespondAdapter adapter = new RespondAdapter(RespondActivity.this, friendList);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            linearLayoutManager.setStackFromEnd(true);
                            linearLayoutManager.setReverseLayout(true);
                            linearLayoutManager.setSmoothScrollbarEnabled(true);
                            respond_RecycleView.setAdapter(adapter);
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String msg = databaseError.toException().getMessage();
                Toast.makeText(RespondActivity.this, "error: " + msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class RespondAdapter extends RecyclerView.Adapter<RespondAdapter.RespondViewHolder> {

        Context mContext;
        List<FriendRequestModelClass> mData;

        public RespondAdapter(Context mContext, List<FriendRequestModelClass> mData) {
            this.mContext = mContext;
            this.mData = mData;
        }

        @NonNull
        @Override
        public RespondViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().from(getApplicationContext()).inflate(R.layout.respond_user_layout, parent, false);


            return new RespondViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RespondViewHolder holder, final int position) {


            UserRefer.child(mData.get(position).getSent_by()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("first_name") && dataSnapshot.hasChild("last_name")) {
                        String firstName = dataSnapshot.child("first_name").getValue().toString();
                        String lastName = dataSnapshot.child("last_name").getValue().toString();
                        holder.textView_RespondFullName.setText(firstName + " " + lastName);
                    }
                    if (dataSnapshot.hasChild("username")) {
                        String username = dataSnapshot.child("username").getValue().toString();
                        holder.textView_RespondUserName.setText("@" + username);
                    }
                    if (dataSnapshot.hasChild("profile_img")) {
                        String profileImg = dataSnapshot.child("profile_img").getValue().toString();
                        Picasso.get().load(profileImg).into(holder.imageView_RespondProfile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String msg = databaseError.toException().getMessage();
                    Toast.makeText(mContext, "error: " + msg, Toast.LENGTH_SHORT).show();
                }
            });


            holder.btn_ConfirmRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Boolean friendsLink = true;
                    UserRefer.child(currentUserId)
                            .child("friends")
                            .child(mData.get(position).getSent_by())
                            .setValue(friendsLink)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
//                                        =========================================
                                        UserRefer.child(mData.get(position).getSent_by())
                                                .child("friends")
                                                .child(currentUserId)
                                                .setValue(friendsLink)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
//                                                            +++++++++++++++++++++++++++++++++++++++
                                                            FriendRequestRefer.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {
                                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                            final String notificationId = snapshot.child("notification").getValue().toString();
                                                                            String sendByUser = snapshot.child("sent_by").getValue().toString();
                                                                            String sendToUser = snapshot.child("sent_to").getValue().toString();

                                                                            if ((sendByUser.equals(currentUserId) && sendToUser.equals(mData.get(position).getSent_by())) ||
                                                                                    (sendByUser.equals(mData.get(position).getSent_by()) && sendToUser.equals(currentUserId))) {
                                                                                String key = snapshot.getKey();

//                            DELETE CHILD ON CANCEL REQUEST
//                                                                                ============ACCEPT REQUEST GENERATE NOTOFICATION==========
                                                                                String sentTo = mData.get(position).getSent_by();
                                                                                AcceptRequestGenerateNotification(mContext, sentTo);
//                                                                                ============ACCEPT REQUEST GENERATE NOTOFICATION==========

                                                                                FriendRequestRefer.child(key).removeValue()
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
//                                                                                                                        btn_SendRequest.setEnabled(true);
//                                                                                                                        CURRENT_STATE = "friends";
//                                                                                                                        requestText.setText("Unfriend");
//
//                                                                                                                        btn_DeclineRequest.setVisibility(View.INVISIBLE);
//                                                                                                                        btn_DeclineRequest.setEnabled(false);h

                                                                                                                        holder.btn_ConfirmRequest.setVisibility(View.GONE);
                                                                                                                        holder.btn_DeclineRequest.setVisibility(View.GONE);
                                                                                                                        holder.btn_DeclineRequest.setEnabled(false);
                                                                                                                        holder.btn_ConfirmRequest.setEnabled(false);

                                                                                                                        Toast.makeText(getApplicationContext(), "de successfully", Toast.LENGTH_SHORT).show();
                                                                                                                    } else {
                                                                                                                        String msg = task.getException().getMessage();
                                                                                                                        Toast.makeText(getApplicationContext(), "error: " + msg, Toast.LENGTH_SHORT).show();
                                                                                                                    }
                                                                                                                }
                                                                                                            });


                                                                                                } else {
                                                                                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                            Toast.makeText(getApplicationContext(), "Linked", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
//                                        =========================================


                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });


//            CLICK LISTENERS
            holder.btn_DeclineRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FriendRequestRefer.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    final String onCancelNotificationId = snapshot.child("notification").getValue().toString();
                                    String sendByUser = snapshot.child("sent_by").getValue().toString();
                                    String sendToUser = snapshot.child("sent_to").getValue().toString();

                                    if ((sendByUser.equals(currentUserId) && sendToUser.equals(mData.get(position).getSent_by())) ||
                                            (sendByUser.equals(mData.get(position).getSent_by()) && sendToUser.equals(currentUserId))) {
                                        String key = snapshot.getKey();

//                            DELETE CHILD ON CANCEL REQUEST
                                        FriendRequestRefer.child(key).removeValue()
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
//                                                                                confirm_decline_layout.setVisibility(View.GONE);
//                                                                                btn_Respond.setVisibility(View.GONE);
//                                                                                btn_ConfirmRequest.setVisibility(View.GONE);
//                                                                                btn_ConfirmRequest.setVisibility(View.INVISIBLE);
//                                                                                btn_ConfirmRequest.setEnabled(false);

//                                                                    ================================

//                                                                                btn_SendRequest.setEnabled(true);
//                                                                                btn_SendRequest.setVisibility(View.VISIBLE);
//                                                                                CURRENT_STATE = "not_friends";
//                                                                                requestText.setText("Add links");

//                                                                                btn_DeclineRequest.setVisibility(View.GONE);
//                                                                                btn_DeclineRequest.setEnabled(false);
                                                                                Toast.makeText(RespondActivity.this, "notify remove", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                String msg = task.getException().getMessage();
                                                                                Toast.makeText(getApplicationContext(), "error: " + msg, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });


                                                        } else {
                                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });
        }


        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class RespondViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView imageView_RespondProfile;
            private TextView textView_RespondFullName, textView_RespondUserName;
            private Button btn_ConfirmRequest, btn_DeclineRequest;

            public RespondViewHolder(@NonNull View itemView) {
                super(itemView);

                imageView_RespondProfile = (CircleImageView) itemView.findViewById(R.id.imageView_RespondProfile);
                textView_RespondFullName = (TextView) itemView.findViewById(R.id.textView_RespondFullName);
                textView_RespondUserName = (TextView) itemView.findViewById(R.id.textView_RespondUserName);
                btn_ConfirmRequest = (Button) itemView.findViewById(R.id.btn_ConfirmRequest);
                btn_DeclineRequest = (Button) itemView.findViewById(R.id.btn_DeclineRequest);


            }


        }


    }

    private void AcceptRequestGenerateNotification(final Context mCtx, String sentTo) {

        Map<String, Object> AcceptObject = new HashMap<>();

        AcceptObject.put("seen", 0);
        AcceptObject.put("sent_by", currentUserId); //Accepted By User
        AcceptObject.put("sent_to", sentTo); //person whose request accepted by me
        AcceptObject.put("timestamp", System.currentTimeMillis());
        AcceptObject.put("type", 2);

        String randomId = reference.push().getKey();
        NotificationsRef.child(randomId).setValue(AcceptObject)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mCtx, "Connected Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mCtx, "error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}

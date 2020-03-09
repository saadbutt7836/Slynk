package com.pronuxtech.slynk;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pronuxtech.slynk.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationFragmentBottomNavigate extends Fragment {


    //    WIDGETS
    private RecyclerView notifyRecycleView;

    //    STRING VARIABLES
    private String currentUserId;
    String sentTo, postId;
    String sentBy;
    int type;
    int seen = 0;
    Double timestamp;

    List<NotificationsModelClass> notifyList = new ArrayList<>();
    NotificationsModelClass notifyClass;
    NotificationAdapter notifyAdapter;

    //    DATABSE REFERENCES
    private FirebaseAuth firebaseAuth;

//    private FirebaseRecyclerAdapter<NotificationsModelClass, NotificationViewHolder> notifyAdapter;
//    private FirebaseRecyclerOptions<NotificationsModelClass> options;

    private DatabaseReference UserRef, FriendRequestRef, NotifyRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment_bottom_navigate, container, false);

        notifyRecycleView = (RecyclerView) view.findViewById(R.id.notifyRecycleView);


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference("friend-requests");
        NotifyRef = FirebaseDatabase.getInstance().getReference("notifications");

        notifyList = new ArrayList<>();

        notifyRecycleView.setHasFixedSize(true);
        notifyRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
//        NotificationsAdapter();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        GetNewNotificationSentById();


    }


//    private void NotificationsAdapter(String id) {
//        DatabaseReference queryRef = FirebaseDatabase.getInstance().getReference().child("notifications");
//        Query query = FirebaseDatabase.getInstance().getReference().child("notifications").child(id);
//
//        options = new FirebaseRecyclerOptions.Builder<NotificationsModelClass>()
//                .setQuery(query, NotificationsModelClass.class).build();
//
//
//        notifyAdapter = new FirebaseRecyclerAdapter<NotificationsModelClass, NotificationViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int i, @NonNull NotificationsModelClass model) {
//
//                Log.d("sendBy", model.getSent_by());
//                Log.d("sendTo", model.getSent_to());
//            }
//
//            @NonNull
//            @Override
//            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_layout, parent, false);
//
//                return new NotificationViewHolder(view);
//            }
//        };
//
//        notifyRecycleView.setAdapter(notifyAdapter);
//        notifyAdapter.startListening();
//    }

    private void GetNewNotificationSentById() {
        NotifyRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                notifyList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        sentTo = snapshot.child("sent_to").getValue().toString();
                        type = Integer.valueOf(snapshot.child("type").getValue().toString());
                        sentBy = snapshot.child("sent_by").getValue().toString();
                        timestamp = Double.valueOf(snapshot.child("timestamp").getValue().toString());

                        if (sentTo.equals(currentUserId) && type == 3) {
                            Log.d("sendBy", sentBy);
                            Log.d("sendTo", sentTo);
                            seen = Integer.valueOf(snapshot.child("seen").getValue().toString());

                            notifyClass = new NotificationsModelClass(seen, type, postId, sentBy, sentTo, timestamp);
                            notifyList.add(notifyClass);
                        }

                        if (sentTo.equals(currentUserId) && type == 0) {

                            postId = snapshot.child("post_id").getValue().toString();
                            Log.d("sendBy", sentBy + " 0");
                            Log.d("sendTo", sentTo + " 0");
                            Log.d("pos", postId);
                            notifyClass = new NotificationsModelClass(seen, type, postId, sentBy, sentTo, timestamp);
                            notifyList.add(notifyClass);
                        }

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        linearLayoutManager.setReverseLayout(true);
                        linearLayoutManager.setStackFromEnd(true);
                        linearLayoutManager.setSmoothScrollbarEnabled(true);

                        notifyAdapter = new NotificationAdapter(getActivity(), notifyList);
                        notifyRecycleView.setLayoutManager(linearLayoutManager);
                        notifyRecycleView.setAdapter(notifyAdapter);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String msg = databaseError.toException().getMessage();
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
        Context mContext;
        List<NotificationsModelClass> mData;

        public NotificationAdapter(Context mContext, List<NotificationsModelClass> mData) {
            this.mContext = mContext;
            this.mData = mData;
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_search_layout, parent, false);

            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final NotificationViewHolder holder, int position) {
            final int type = mData.get(position).getType();
            if (type == 0) {
                holder.textView_Message.setText(getString(R.string.like_post));
            }
            if (type == 1) {
                holder.textView_Message.setText(getString(R.string.comment_post));
            }
            if (type == 2) {
                holder.textView_Message.setText(getString(R.string.accept_request));
            }
            if (type == 3) {
                holder.textView_Message.setText(getString(R.string.send_request));
            }


            DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference("Users");
            UserRef.child(mData.get(position).getSent_by())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild("first_name") && dataSnapshot.hasChild("last_name")) {
                                    String fName = dataSnapshot.child("first_name").getValue().toString();
                                    String lName = dataSnapshot.child("last_name").getValue().toString();

                                    holder.textView_FullName.setText(fName + " " + lName);
                                }
                                if (dataSnapshot.hasChild("profile_img")) {
                                    String profileImg = dataSnapshot.child("profile_img").getValue().toString();
                                    Picasso.get().load(profileImg).into(holder.imageView_SearchProfile);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(mContext, "error: " + databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class NotificationViewHolder extends RecyclerView.ViewHolder {
            private RelativeLayout liveSearchLayout;
            private CircleImageView imageView_SearchProfile;
            private TextView textView_FullName, textView_Message;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                liveSearchLayout = (RelativeLayout) itemView.findViewById(R.id.liveSearchLayout);
                imageView_SearchProfile = (CircleImageView) itemView.findViewById(R.id.imageView_SearchProfile);
                textView_FullName = (TextView) itemView.findViewById(R.id.textView_FullName);
                textView_Message = (TextView) itemView.findViewById(R.id.textView_SearchUserName);
            }
        }
    }


}

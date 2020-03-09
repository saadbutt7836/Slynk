package com.pronuxtech.slynk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllChatUserListActivity extends AppCompatActivity {

    //    WIDGETS
    private RecyclerView AllChatUser_RecycleView;

    //STRINGS VARIABLES
    private String currentUserId, receiverId;


    //    FIREBASE REFERENCES
    private DatabaseReference UsersRef, databaseReference;
    private FirebaseAuth firebaseAuth;

    //    FIREBASE RECYCLE ADAPTER OPTIONS
    private FirebaseRecyclerAdapter<UsersInfoModelClass, AllUserViewHolder> adapter;
    private FirebaseRecyclerOptions<UsersInfoModelClass> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chat_user_list);


        AllChatUser_RecycleView = (RecyclerView) findViewById(R.id.AllChatUser_RecycleView);


//        DATABASE REFERENCES CASTINGS
        databaseReference = FirebaseDatabase.getInstance().getReference();
        UsersRef = databaseReference.child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

//        OFFLINE FEATURE
        UsersRef.keepSynced(true);


        AllChatUser_RecycleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);

        AllChatUser_RecycleView.setLayoutManager(linearLayoutManager);
    }


    @Override
    protected void onStart() {
        super.onStart();
        options = new FirebaseRecyclerOptions.Builder<UsersInfoModelClass>()
                .setQuery(UsersRef, UsersInfoModelClass.class).build();
        adapter = new FirebaseRecyclerAdapter<UsersInfoModelClass, AllUserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllUserViewHolder holder, int position, @NonNull final UsersInfoModelClass model) {
                receiverId = model.getUid();

                if (!receiverId.equals(currentUserId)) {
                    Picasso.get().load(model.getProfile_img()).into(holder.imageView_SearchProfile);
                    holder.textView_FullName.setText(model.getFirst_name() + " " + model.getLast_name());
                    holder.textView_SearchUserName.setText(model.getUsername());
                } else {
                    holder.imageView_SearchProfile.setVisibility(View.GONE);
                    holder.textView_FullName.setVisibility(View.GONE);
                    holder.textView_SearchUserName.setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        receiverId = model.getUid();
                        String receiverFirstName = model.getFirst_name();
                        Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                        intent.putExtra("receiverId", receiverId);
                        intent.putExtra("receiverFirstName", receiverFirstName);
                        startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public AllUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_layout, parent, false);

                return new AllUserViewHolder(view);
            }
        };

        AllChatUser_RecycleView.setAdapter(adapter);
        adapter.startListening();

    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    //    VIEW HOLDER
    public static class AllUserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        private RelativeLayout liveSearchLayout;
        private CircleImageView imageView_SearchProfile;
        private TextView textView_FullName, textView_SearchUserName;

        public AllUserViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            liveSearchLayout = (RelativeLayout) mView.findViewById(R.id.liveSearchLayout);
            imageView_SearchProfile = (CircleImageView) mView.findViewById(R.id.imageView_SearchProfile);
            textView_FullName = (TextView) mView.findViewById(R.id.textView_FullName);
            textView_SearchUserName = (TextView) mView.findViewById(R.id.textView_SearchUserName);
        }
    }

}

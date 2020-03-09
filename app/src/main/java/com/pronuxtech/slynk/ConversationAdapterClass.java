package com.pronuxtech.slynk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ConversationAdapterClass extends RecyclerView.Adapter<ConversationAdapterClass.ConversationViewHolder> {

    Context mCtx;
    List<ConversationsModelClass> conversationList;
    String firstName, lastName, profileImg;

    public ConversationAdapterClass(Context mCtx, List<ConversationsModelClass> conversationList) {
        this.mCtx = mCtx;
        this.conversationList = conversationList;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View conversationView = LayoutInflater.from(mCtx).inflate(R.layout.conversations_layout, parent, false);

        return new ConversationViewHolder(conversationView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ConversationViewHolder holder, final int position) {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(conversationList.get(position).getSent_to());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UsersInfoModelClass usersInfoModelClass = dataSnapshot.getValue(UsersInfoModelClass.class);

                    firstName = usersInfoModelClass.getFirst_name();
                    lastName = usersInfoModelClass.getLast_name();
                    profileImg = usersInfoModelClass.getProfile_img();

                    holder.conversation_UserName.setText(firstName + " " + lastName);
                    Picasso.get().load(profileImg).into(holder.imageView_ConversationProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.textView_ConversationMsg.setText(conversationList.get(position).

                getLast_message());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sentTo = conversationList.get(position).getSent_to();
                Intent intent = new Intent(mCtx, ChatRoomActivity.class);
                intent.putExtra("receiverId", sentTo);
                intent.putExtra("receiverFirstName", firstName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageView_ConversationProfile;
        public TextView conversation_UserName, textView_ConversationMsg;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView_ConversationProfile = (CircleImageView) itemView.findViewById(R.id.imageView_ConversationProfile);
            textView_ConversationMsg = (TextView) itemView.findViewById(R.id.textView_ConversationMsg);
            conversation_UserName = (TextView) itemView.findViewById(R.id.conversation_UserName);

        }
    }
}

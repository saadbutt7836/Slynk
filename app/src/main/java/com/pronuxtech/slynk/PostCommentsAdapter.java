package com.pronuxtech.slynk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostCommentsAdapter extends RecyclerView.Adapter<PostCommentsAdapter.PostCommentsViewHolder> {

    Context mContext;
    List<PostCommentsClass> mData;

    String timeAgo;

    public PostCommentsAdapter(Context mContext, List<PostCommentsClass> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public PostCommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_comment_layout, parent, false);
        PostCommentsViewHolder holder = new PostCommentsViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostCommentsViewHolder holder, int position) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(mData.get(holder.getAdapterPosition()).getPosted_by());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String firstName = dataSnapshot.child("first_name").getValue().toString();
                String lastName = dataSnapshot.child("last_name").getValue().toString();
                String profileImg = dataSnapshot.child("profile_img").getValue().toString();

                Picasso.get().load(profileImg).into(holder.imageView_CommentProfile);
                holder.textView_UserName.setText(firstName + " " + lastName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.textView_CommentMsg.setText(mData.get(holder.getAdapterPosition()).getBody());
        long firebaseTime = (long) mData.get(position).getTimestamp();

        TimeAgosHandle(firebaseTime);
        holder.textView_CommentTimeStamp.setText(String.valueOf(timeAgo));
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

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class PostCommentsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView_CommentProfile;
        private TextView textView_UserName, textView_CommentMsg, textView_CommentTimeStamp;

        public PostCommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView_CommentProfile = (CircleImageView) itemView.findViewById(R.id.imageView_CommentProfile);
            textView_UserName = (TextView) itemView.findViewById(R.id.textView_UserName);
            textView_CommentMsg = (TextView) itemView.findViewById(R.id.textView_CommentMsg);
            textView_CommentTimeStamp = (TextView) itemView.findViewById(R.id.textView_CommentTimeStamp);

        }
    }
}

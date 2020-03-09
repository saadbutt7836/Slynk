package com.pronuxtech.slynk;
//     RecycleView.Adapter
//     RecycleView.ViewHolder

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeopleDataAdapter extends RecyclerView.Adapter<PeopleDataAdapter.PeopleDataViewHolder> {

    Context mContext;
    List<PeopleDataModelClass> mData;

    public PeopleDataAdapter(Context mContext, List<PeopleDataModelClass> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public PeopleDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.chatfragment_people_recycleviews, parent, false);
        final PeopleDataViewHolder holder = new PeopleDataViewHolder(view);


        holder.chatFragmentPeopleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatRoomActivity.class);
                mContext.startActivity(intent);


            }
        });
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull PeopleDataViewHolder holder, int position) {

        holder.usernameTextView.setText(mData.get(position).getUsername());
        holder.messageTextView.setText(mData.get(position).getUserMessages());
        holder.messageTimeTextView.setText(mData.get(position).getMessageTime());
        holder.messageCountTextView.setText(mData.get(position).getMessagesCount());
        holder.profilePic.setImageResource(mData.get(position).getUserProfile());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class PeopleDataViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout chatFragmentPeopleItem;
        private CircleImageView profilePic;
        private TextView usernameTextView, messageTextView, messageTimeTextView, messageCountTextView;

        public PeopleDataViewHolder(@NonNull View itemView) {
            super(itemView);

            chatFragmentPeopleItem = (LinearLayout) itemView.findViewById(R.id.chatFragmentPeopleItem);
            usernameTextView = (TextView) itemView.findViewById(R.id.usernameTextView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageTimeTextView = (TextView) itemView.findViewById(R.id.messageTimeTextView);
            messageCountTextView = (TextView) itemView.findViewById(R.id.messageCountTextView);
            profilePic = (CircleImageView) itemView.findViewById(R.id.profilePic);

        }
    }
}
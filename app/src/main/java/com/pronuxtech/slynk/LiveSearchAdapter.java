package com.pronuxtech.slynk;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LiveSearchAdapter extends RecyclerView.Adapter<LiveSearchAdapter.LiveSearchViewHolder> {

    Context context;
    List<LiveSearchModelClass> mData;

    public LiveSearchAdapter(Context context, List<LiveSearchModelClass> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public LiveSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_search_layout, parent, false);
        return new LiveSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LiveSearchViewHolder holder, final int position) {

        holder.textView_FullName.setText(mData.get(position).getLower_name());
        holder.textView_SearchUserName.setText("@" + mData.get(position).getUsername());
        Picasso.get().load(mData.get(position).getProfile_img()).into(holder.imageView_SearchProfile);


        holder.liveSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchedUserKey = mData.get(holder.getAdapterPosition()).getId();
                Intent intent = new Intent(context, UserSearchProfileActivity.class);
                intent.putExtra("search_user_key", searchedUserKey);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class LiveSearchViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout liveSearchLayout;
        private CircleImageView imageView_SearchProfile;
        private TextView textView_FullName, textView_SearchUserName;

        public LiveSearchViewHolder(@NonNull View itemView) {
            super(itemView);

            liveSearchLayout = (RelativeLayout) itemView.findViewById(R.id.liveSearchLayout);
            imageView_SearchProfile = (CircleImageView) itemView.findViewById(R.id.imageView_SearchProfile);
            textView_FullName = (TextView) itemView.findViewById(R.id.textView_FullName);
            textView_SearchUserName = (TextView) itemView.findViewById(R.id.textView_SearchUserName);
        }
    }

    {

    }
}

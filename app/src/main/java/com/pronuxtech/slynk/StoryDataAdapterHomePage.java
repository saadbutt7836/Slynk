package com.pronuxtech.slynk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryDataAdapterHomePage extends RecyclerView.Adapter<StoryDataAdapterHomePage.StoryDataHomePageViewHolder> {

    private Context mContext;
    private List<StoryDataHomePage> mData;

    public StoryDataAdapterHomePage(Context mContext, List<StoryDataHomePage> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public StoryDataHomePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.story_recycle_view, null);
        final StoryDataHomePageViewHolder storyDataHomePageViewHolder = new StoryDataHomePageViewHolder(view);
        return storyDataHomePageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StoryDataHomePageViewHolder holder, int position) {
        holder.storyImages.setImageResource(mData.get(position).getStoryImages());
        holder.storyUserName.setText(mData.get(position).getStoryUserName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class StoryDataHomePageViewHolder extends RecyclerView.ViewHolder {
        CircleImageView storyImages;
        TextView storyUserName;

        public StoryDataHomePageViewHolder(@NonNull View itemView) {
            super(itemView);

            storyUserName = (TextView) itemView.findViewById(R.id.storyUserName);
            storyImages = (CircleImageView) itemView.findViewById(R.id.storyImages);
        }
    }
}

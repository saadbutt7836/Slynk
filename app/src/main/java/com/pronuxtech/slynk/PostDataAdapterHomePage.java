//package com.pronuxtech.slynk;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.MutableData;
//import com.google.firebase.database.Transaction;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//import static androidx.constraintlayout.widget.Constraints.TAG;
//
//public class PostDataAdapterHomePage extends RecyclerView.Adapter<PostDataAdapterHomePage.PostDataHomePageViewHolder> {
//
//    private Context mContext;
//    private List<PostDataHomePage> mData;
//
//    private String fullname;
//    private int likesCount = 0;
//    private Boolean likeState = false;
//
//
//    public PostDataAdapterHomePage(Context mContext, List<PostDataHomePage> mData) {
//        this.mContext = mContext;
//        this.mData = mData;
//    }
//
//    @NonNull
//    @Override
//    public PostDataHomePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.post_recycle_view, null);
//        final PostDataHomePageViewHolder holder = new PostDataHomePageViewHolder(view);
//
//
////=======================================================================Like Area====================================================
//
//
////        //=======================================================================Like Area====================================================
//////        holder.likeState.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View view) {
//////                holder.likeState.setVisibility(View.GONE);
//////                holder.unLikeState.setVisibility(View.VISIBLE);
//////            }
//////        });
////
//////        holder.postShareBtn.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View view) {
//////
//////                final MediaPlayer shareTune = MediaPlayer.create(mContext, R.raw.share);
//////                shareTune.start();
//////            }
//////        });
////        holder.postMenuOption.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                View postBottomSheetView = LayoutInflater.from(mContext).inflate(R.layout.post_menu_bottom_sheet_dialog, null);
////                LinearLayout sheet = (LinearLayout) postBottomSheetView.findViewById(R.id.sheet);
////                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
////                bottomSheetDialog.setContentView(postBottomSheetView);
////                bottomSheetDialog.show();
////
////                sheet.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        Toast.makeText(mContext, "listener click", Toast.LENGTH_SHORT).show();
////                    }
////                });
////
////                switch (postBottomSheetView.getId()) {
////                    case R.id.sheet: {
////                        Toast.makeText(mContext, "switch click", Toast.LENGTH_SHORT).show();
////                    }
////                }
////            }
////        });
//
//
//        return holder;
//    }
//
//
//    @Override
//    public void onBindViewHolder(@NonNull final PostDataHomePageViewHolder holder, int position) {
//
//        final int type = mData.get(holder.getAdapterPosition()).getType();
//
//
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
//                .child("Users")
//                .child(mData.get(holder.getAdapterPosition()).getPosted_by());
//
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                fullname = dataSnapshot.child("first_name").getValue().toString() + " " +
//                        dataSnapshot.child("last_name").getValue().toString();
//
//                holder.postUserName.setText(fullname);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(mContext, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//        holder.postTime.setText(String.valueOf(mData.get(holder.getAdapterPosition()).getTimestamp()));
//        holder.postTotalComments.setText(String.valueOf(mData.get(holder.getAdapterPosition()).getComments()));
//        holder.postTotalLikes.setText(String.valueOf(mData.get(holder.getAdapterPosition()).getLikesCount()));
//
//
//        if (type == 0) {
//            holder.postMessages.setText(mData.get(holder.getAdapterPosition()).getStatus());
//            Picasso.get().load((Uri) null).into(holder.postImage);
//        } else if (type == 1) {
//            Picasso.get().load(mData.get(holder.getAdapterPosition()).getPost_image()).into(holder.postImage);
//            holder.postMessages.setText(null);
//        } else if (type == 2) {
//            holder.postMessages.setText(mData.get(holder.getAdapterPosition()).getStatus());
//            Picasso.get().load(mData.get(holder.getAdapterPosition()).getPost_image()).into(holder.postImage);
//        }
//
//
////        if (mData.get(position).likes.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
////            // Unstar the post and remove self from stars
////            holder.postUnLikeStateBtn.setImageResource(R.drawable.sinjko);
////        } else {
////            // Star the post and add self to stars
////           holder.postUnLikeStateBtn.setImageResource(R.drawable.like);
////        }
//
//        holder.postUnLikeStateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatabaseReference databaseReference;
//                FirebaseAuth firebaseAuth;
//                databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(mData.get(holder.getAdapterPosition()).getPid());
//                onStarClicked(databaseReference);
//                if (mData.get(holder.getAdapterPosition()).likes.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                    // Unstar the post and remove self from stars
//                    holder.postUnLikeStateBtn.setImageResource(R.drawable.sinjko);
//                } else {
//                    // Star the post and add self to stars
//                    holder.postUnLikeStateBtn.setImageResource(R.drawable.like);
//                }
//
//            }
//        });
//
//
//        holder.postCommentBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(mContext, PostCommentsActivity.class);
//                intent.putExtra("postId", mData.get(holder.getAdapterPosition()).getPid());
//                mContext.startActivity(intent);
//
//
//            }
//        });
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mData.size();
//    }
//
//    class PostDataHomePageViewHolder extends RecyclerView.ViewHolder {
//
//        private ImageView postProfilePic, postImage, postUnLikeStateBtn;
//        private TextView postUserName, postTime, postMessages, postTotalComments, postTotalLikes;
//        private LinearLayout likeState, unLikeState, postCommentBtn, postShareBtn;
//        private RelativeLayout postMenuOption;
//
//
//        public PostDataHomePageViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            postProfilePic = (ImageView) itemView.findViewById(R.id.postProfilePic);
//            postImage = (ImageView) itemView.findViewById(R.id.postImage);
//            postUserName = (TextView) itemView.findViewById(R.id.postUserName);
//            postTime = (TextView) itemView.findViewById(R.id.postTime);
//            postMessages = (TextView) itemView.findViewById(R.id.postMessages);
//            postTotalComments = (TextView) itemView.findViewById(R.id.postTotalComments);
//            postTotalLikes = (TextView) itemView.findViewById(R.id.postTotalLikes);
//            likeState = (LinearLayout) itemView.findViewById(R.id.postLikeStateBtn);
////            postUnLikeStateBtn = (ImageView) itemView.findViewById(R.id.postUnLikeStateBtn);
//            postCommentBtn = (LinearLayout) itemView.findViewById(R.id.postCommentBtn);
////            postShareBtn = (LinearLayout) itemView.findViewById(R.id.postShareBtn);
//            postMenuOption = (RelativeLayout) itemView.findViewById(R.id.postMenuOption);
//        }
//
//
//    }
//
//
//    private void onStarClicked(DatabaseReference postRef) {
//        postRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                PostDataHomePage p = mutableData.getValue(PostDataHomePage.class);
//                if (p == null) {
//                    return Transaction.success(mutableData);
//                }
//
//                if (p.likes.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                    // Unstar the post and remove self from stars
//                    p.likesCount = p.likesCount - 1;
//                    p.likes.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                } else {
//                    // Star the post and add self to stars
//                    p.likesCount = p.likesCount + 1;
//                    p.likes.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
//                }
//
//                // Set value and report transaction success
//                mutableData.setValue(p);
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b,
//                                   DataSnapshot dataSnapshot) {
//                // Transaction completed
//                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
//            }
//        });
////
////
//    }
//}

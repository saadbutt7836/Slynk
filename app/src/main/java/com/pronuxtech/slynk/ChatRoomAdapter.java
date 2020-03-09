package com.pronuxtech.slynk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ExtraViewHolder> {

    private Context mCtx;
    private int timeLabel;
    private MediaPlayer mediaPlayer;
    private List<TextMessagesModelClass> userMsgList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef, UserRef;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();

    public ChatRoomAdapter(Context mCtx, List<TextMessagesModelClass> userMsgList) {
        this.mCtx = mCtx;
        this.userMsgList = userMsgList;
    }

    public ChatRoomAdapter(List<TextMessagesModelClass> userMsgList) {
        this.userMsgList = userMsgList;
    }


    @NonNull
    @Override
    public ExtraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msgs_layout, parent, false);

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        UserRef = rootRef.child("Users");
        ExtraViewHolder holder = new ExtraViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ExtraViewHolder holder, final int position) {

        String messageSenderId = firebaseAuth.getCurrentUser().getUid();
        final TextMessagesModelClass messages = userMsgList.get(position);


        String fromUserID = messages.getSender();
        int type = messages.getType();


        UserRef.child(fromUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String profileImg = dataSnapshot.child("profile_img").getValue().toString();
//                    Picasso.get().load(profileImg).into(holder.receiverProfileImg);
//                    Picasso.get().load(profileImg).into(holder.senderProfileImg);

                    Picasso.get().load(profileImg).networkPolicy(NetworkPolicy.OFFLINE).into(holder.receiverProfileImg, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profileImg).into(holder.receiverProfileImg);
                        }
                    });
                    Picasso.get().load(profileImg).networkPolicy(NetworkPolicy.OFFLINE).into(holder.senderProfileImg, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profileImg).into(holder.senderProfileImg);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        ALL LAYOUTS VISIBILTY GONE
        holder.textView_sent.setVisibility(View.GONE);
        holder.textView_Time.setVisibility(View.GONE);
        holder.receiverMsgTxt.setVisibility(View.GONE);
        holder.receiverProfileImg.setVisibility(View.GONE);
        holder.senderMsgTxt.setVisibility(View.GONE);
        holder.senderProfileImg.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);
        holder.receiverAudioMsg_linearLayout.setVisibility(View.GONE);
        holder.senderAudioMsg_linearLayout.setVisibility(View.GONE);
        holder.receiver_videoThumbnail.setVisibility(View.GONE);
        holder.receiverVideoPlayBtn.setVisibility(View.GONE);
        holder.sender_videoThumbnail.setVisibility(View.GONE);
        holder.senderVideoPlayBtn.setVisibility(View.GONE);


        final int holderPos = holder.getAdapterPosition();
        if (holderPos == userMsgList.size() - 1 && fromUserID.equals(messageSenderId)) {
            holder.textView_sent.setVisibility(View.VISIBLE);
        }
        if (holderPos % 5 == 0) {
            holder.textView_Time.setVisibility(View.VISIBLE);

            long time = (new Double(messages.getTimestamp())).longValue();

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            Date timeStamp = c.getTime();
            SimpleDateFormat formatDate = new SimpleDateFormat("d-L-Y hh:mm a");
            String timestampDateTime = formatDate.format(timeStamp);

//            GET DAY FROM TIMESTAMP
            SimpleDateFormat timeStampDayFormat = new SimpleDateFormat("E");
            String timeStampDay = timeStampDayFormat.format(timeStamp);
            SimpleDateFormat timeStampTimeFormat = new SimpleDateFormat("hh:mm a");
            String timeStampTime = timeStampTimeFormat.format(timeStamp);


//            GET CURRENT DAY
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("E");
            String currentDate = currentDateFormat.format(new Date());
            if (currentDate.equals(timeStampDay)) {
//                holder.textView_Time.setText("Today " + timeStampTime);
            } else {
//                holder.textView_Time.setText(timestampDateTime);

            }
            holder.textView_Time.setText(timestampDateTime);

        }


        if (type == 0) {
            if (fromUserID.equals(messageSenderId)) {
                holder.senderProfileImg.setVisibility(View.VISIBLE);
                holder.senderMsgTxt.setVisibility(View.VISIBLE);
                holder.senderMsgTxt.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMsgTxt.setText(messages.getMessage());
                Log.d("sender", messages.getMessage());

            } else {
                Log.d("receiver", messages.getMessage());
                holder.senderMsgTxt.setVisibility(View.INVISIBLE);
                holder.senderProfileImg.setVisibility(View.INVISIBLE);

                holder.receiverProfileImg.setVisibility(View.VISIBLE);
                holder.receiverMsgTxt.setVisibility(View.VISIBLE);

                holder.receiverMsgTxt.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMsgTxt.setText(messages.getMessage());

            }
        } else if (type == 1) {
            if (fromUserID.equals(messageSenderId)) {
                holder.senderProfileImg.setVisibility(View.VISIBLE);
                holder.messageSenderPicture.setVisibility(View.VISIBLE);

//                Picasso.get().load(messages.image).into(holder.messageSenderPicture);

                Picasso.get().load(messages.image).networkPolicy(NetworkPolicy.OFFLINE).into(holder.messageSenderPicture, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(messages.image).into(holder.messageSenderPicture);

                    }
                });


            } else {
                holder.receiverProfileImg.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
//
//                Picasso.get().load(messages.image).into(holder.messageReceiverPicture);
                Picasso.get().load(messages.image).networkPolicy(NetworkPolicy.OFFLINE).into(holder.messageReceiverPicture, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get().load(messages.image).into(holder.messageReceiverPicture);
                    }
                });
            }
        } else if (type == 2) {
            if (fromUserID.equals(messageSenderId)) {
                holder.senderProfileImg.setVisibility(View.VISIBLE);
                holder.senderAudioMsg_linearLayout.setVisibility(View.VISIBLE);


//                CLICK LISTENER
                holder.senderPlayAudioBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(messages.getAudio());

                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mPlayer) {
                                    mPlayer.start();
                                    holder.senderPlayAudioBtn.setVisibility(View.GONE);
                                    holder.senderPauseAudioBtn.setVisibility(View.VISIBLE);

                                    finalTime = mediaPlayer.getDuration();
                                    startTime = mediaPlayer.getCurrentPosition();

                                    final Runnable UpdateSongTime = new Runnable() {
                                        public void run() {
                                            startTime = mediaPlayer.getCurrentPosition();

                                            Double remain = finalTime - startTime;
                                            int time = remain.intValue();
//                                            long remainLong = (new Double(remain)).longValue();

//                                            String timeLabel = "";
//                                            int min = time / 1000 / 60;
//                                            int sec = time / 1000 % 60;
//
//                                            timeLabel = min + ":";
//                                            if (sec < 10) timeLabel += "0";
//                                            timeLabel += sec;

                                            holder.senderAudioRemainTime.setText(createTimeLabel(time));

//                                            holder.senderAudioTime.setText(String.format("%d:%d",
//                                                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
//                                                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
//                                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                                                    toMinutes((long) finalTime)))
//                                            );

//                                            holder.senderAudioTime.setText(String.format("%d:%d",
//                                                    TimeUnit.MILLISECONDS.toMinutes(remainLong),
//                                                    TimeUnit.MILLISECONDS.toSeconds(remainLong) -
//                                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainLong))));

//                                            holder.senderAudioTime.setText(String.format("%d:%d",
//                                                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
//                                                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
//                                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                                                    toMinutes((long) startTime)))
//                                            );

                                            myHandler.postDelayed(this, 100);
                                        }
                                    };
                                    UpdateSongTime.run();

                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            holder.senderPauseAudioBtn.setVisibility(View.GONE);
                                            holder.senderPlayAudioBtn.setVisibility(View.VISIBLE);
                                            myHandler.removeCallbacks(UpdateSongTime);
                                        }
                                    });


//                STOP AUDIO
                                    holder.senderPauseAudioBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (mediaPlayer != null) {
                                                holder.senderPauseAudioBtn.setVisibility(View.GONE);
                                                holder.senderPlayAudioBtn.setVisibility(View.VISIBLE);
                                                mediaPlayer.pause();
                                                myHandler.removeCallbacks(UpdateSongTime);
                                            }
                                        }
                                    });


                                }

                            });

                            mediaPlayer.prepare();

                        } catch (IOException e) {
                            e.getStackTrace();
                        }
                    }
                });


            } else {
                holder.receiverProfileImg.setVisibility(View.VISIBLE);
                holder.receiverAudioMsg_linearLayout.setVisibility(View.VISIBLE);


//                CLICK LISTENER
                holder.receiverPlayAudioBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mediaPlayer = new MediaPlayer();
                        try {

                            mediaPlayer.setDataSource(messages.getAudio());
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(final MediaPlayer mPlayer) {
                                    mPlayer.start();
                                    holder.receiverPlayAudioBtn.setVisibility(View.GONE);
                                    holder.receiverPauseAudioBtn.setVisibility(View.VISIBLE);


                                    finalTime = mediaPlayer.getDuration();
                                    startTime = mediaPlayer.getCurrentPosition();


                                    final Runnable UpdateSongTime = new Runnable() {
                                        public void run() {
                                            startTime = mediaPlayer.getCurrentPosition();

                                            Double remain = finalTime - startTime;
                                            int time = remain.intValue();
                                            holder.receiverAudioTime.setText(createTimeLabel(time));
//                                            holder.receiverAudioTime.setText(String.format("%d:%d",
//                                                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
//                                                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
//                                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                                                    toMinutes((long) startTime)))
//                                            );
                                            myHandler.postDelayed(this, 100);
                                        }
                                    };
                                    UpdateSongTime.run();


                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            holder.receiverPauseAudioBtn.setVisibility(View.GONE);
                                            holder.receiverPlayAudioBtn.setVisibility(View.VISIBLE);
                                            myHandler.removeCallbacks(UpdateSongTime);
                                        }
                                    });


                                    //STOP AUDIO

                                    holder.receiverPauseAudioBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (mediaPlayer != null) {
                                                holder.receiverPauseAudioBtn.setVisibility(View.GONE);
                                                holder.receiverPlayAudioBtn.setVisibility(View.VISIBLE);
                                                mediaPlayer.pause();
                                                myHandler.removeCallbacks(UpdateSongTime);
                                            }
                                        }
                                    });


                                }
                            });


                            mediaPlayer.prepare();


                        } catch (IOException e) {
                            e.getStackTrace();
                        }
                    }
                });


            }
        } else if (type == 3) {
            if (fromUserID.equals(messageSenderId)) {
                holder.sender_videoThumbnail.setVisibility(View.VISIBLE);
                holder.senderProfileImg.setVisibility(View.VISIBLE);
                holder.senderVideoPlayBtn.setVisibility(View.VISIBLE);

                byte[] senderDecodedString = Base64.decode(messages.getThumbnail(), Base64.DEFAULT);
                Bitmap senderDecodedByte = BitmapFactory.decodeByteArray(senderDecodedString, 0, senderDecodedString.length);

                holder.sender_videoThumbnail.setImageBitmap(senderDecodedByte);
                holder.sender_videoThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(holder.layoutView.getContext(), MessageVideoView.class);
                        intent.putExtra("VideoMessagesUri", messages.getVideo());
                        holder.layoutView.getContext().startActivity(intent);
                    }
                });

            } else {
                //RECEIVER AREA
                holder.receiver_videoThumbnail.setVisibility(View.VISIBLE);
                holder.receiverProfileImg.setVisibility(View.VISIBLE);
                holder.receiverVideoPlayBtn.setVisibility(View.VISIBLE);

                byte[] receiverDecodedString = Base64.decode(messages.getThumbnail(), Base64.DEFAULT);
                Bitmap receiverDecodedByte = BitmapFactory.decodeByteArray(receiverDecodedString, 0, receiverDecodedString.length);

                holder.receiver_videoThumbnail.setImageBitmap(receiverDecodedByte);
                holder.receiver_videoThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(holder.layoutView.getContext(), MessageVideoView.class);
                        intent.putExtra("VideoMessagesUri", messages.getVideo());
                        holder.layoutView.getContext().startActivity(intent);

                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMsgList.size();
    }

    public class ExtraViewHolder extends RecyclerView.ViewHolder {

        View layoutView;

        public TextView textView_sent, textView_Time, senderMsgTxt, receiverMsgTxt, senderAudioRemainTime, senderAudioTime, receiverAudioTime;
        public CircleImageView receiverProfileImg, senderProfileImg;
        public ImageView messageSenderPicture, messageReceiverPicture, receiverPlayAudioBtn, receiverPauseAudioBtn,
                senderPlayAudioBtn, senderPauseAudioBtn;
        public LinearLayout receiverAudioMsg_linearLayout, senderAudioMsg_linearLayout;
        public ProgressBar senderProgress;
        public ImageView sender_videoThumbnail, receiver_videoThumbnail, senderVideoPlayBtn, receiverVideoPlayBtn;
        //        public LinearLayout mSeekbar;


        public ExtraViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutView = itemView;
            senderProfileImg = (CircleImageView) itemView.findViewById(R.id.sender_profile_image);
            receiverProfileImg = (CircleImageView) itemView.findViewById(R.id.receiver_profile_image);
            textView_sent = (TextView) itemView.findViewById(R.id.textView_sent);
            textView_Time = (TextView) itemView.findViewById(R.id.textView_Time);
            senderMsgTxt = (TextView) itemView.findViewById(R.id.sender_msgs_text);
            receiverMsgTxt = (TextView) itemView.findViewById(R.id.receiver_msgs_text);
            messageSenderPicture = (ImageView) itemView.findViewById(R.id.message_sender_image_view);
            messageReceiverPicture = (ImageView) itemView.findViewById(R.id.message_receiver_image_view);
            receiverPlayAudioBtn = (ImageView) itemView.findViewById(R.id.receiverPlayAudioBtn);
            receiverPauseAudioBtn = (ImageView) itemView.findViewById(R.id.receiverPauseAudioBtn);
            senderPlayAudioBtn = (ImageView) itemView.findViewById(R.id.senderPlayAudioBtn);
            senderPauseAudioBtn = (ImageView) itemView.findViewById(R.id.senderPauseAudioBtn);
            receiverAudioMsg_linearLayout = (LinearLayout) itemView.findViewById(R.id.receiverAudioMsg_linearLayout);
            senderAudioMsg_linearLayout = (LinearLayout) itemView.findViewById(R.id.senderAudioMsg_linearLayout);
//            mSeekbar = (LinearLayout) itemView.findViewById(R.id.seekbar);
//            senderAudioTime = (TextView) itemView.findViewById(R.id.senderAudioTime);
            senderAudioRemainTime = (TextView) itemView.findViewById(R.id.senderAudioRemainTime);
            receiverAudioTime = (TextView) itemView.findViewById(R.id.receiverAudioTime);
            sender_videoThumbnail = (ImageView) itemView.findViewById(R.id.sender_videoThumbnail);
            receiver_videoThumbnail = (ImageView) itemView.findViewById(R.id.receiver_videoThumbnail);
            receiverVideoPlayBtn = (ImageView) itemView.findViewById(R.id.receiverVideoPlayBtn);
            senderVideoPlayBtn = (ImageView) itemView.findViewById(R.id.senderVideoPlayBtn);
//            senderProgress = (ProgressBar) itemView.findViewById(R.id.senderProgress);


        }
    }


    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }


}

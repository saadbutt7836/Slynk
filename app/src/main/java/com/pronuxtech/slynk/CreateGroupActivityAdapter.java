package com.pronuxtech.slynk;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.VISIBLE;


public class CreateGroupActivityAdapter extends RecyclerView.Adapter<CreateGroupActivityAdapter.CreateGroupViewHolder> {

    public interface ChangeStatusListener {
        void onItemChangeListener(int position, GroupCreateUserSeleceModelClass model);
    }

    List<GroupCreateUserSeleceModelClass> userList;
    Context mCtx;
    ChangeStatusListener changeStatusListener;


    private FirebaseAuth firebaseAuth;
    private String currentUserId;

    public CreateGroupActivityAdapter(List<GroupCreateUserSeleceModelClass> userList, Context mCtx, ChangeStatusListener changeStatusListener) {
        this.userList = userList;
        this.mCtx = mCtx;
        this.changeStatusListener = changeStatusListener;
    }

    @NonNull
    @Override
    public CreateGroupActivityAdapter.CreateGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mCtx).inflate(R.layout.create_group_userlist_layout, parent, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getUid();
        return new CreateGroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CreateGroupActivityAdapter.CreateGroupViewHolder holder, final int position) {

        final GroupCreateUserSeleceModelClass model = userList.get(position);
        String groupCreaterId = model.getUid();

        if (!groupCreaterId.equals(currentUserId)) {
            String firstname = model.getFirst_name();
            String lastname = model.getLast_name();
            String username = model.getUsername();

            holder.conversation_UserName.setText(firstname + " " + lastname);
            holder.textView_ConversationMsg.setText(username);
            Picasso.get().load(model.getProfile_img()).into(holder.imageView_ConversationProfile);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (userList != null) {
            return userList.size();
        }
        return 0;
    }

    public class CreateGroupViewHolder extends RecyclerView.ViewHolder {
        View view;

        private RelativeLayout selectItemLayout, viewLayout;
        private CircleImageView imageView_ConversationProfile;
        private TextView conversation_UserName, textView_ConversationMsg;

        public CreateGroupViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            viewLayout = (RelativeLayout) view.findViewById(R.id.viewLayout);
            selectItemLayout = (RelativeLayout) view.findViewById(R.id.selectItemLayout);
            imageView_ConversationProfile = (CircleImageView) view.findViewById(R.id.imageView_ConversationProfile);
            conversation_UserName = (TextView) view.findViewById(R.id.conversation_UserName);
            textView_ConversationMsg = (TextView) view.findViewById(R.id.textView_ConversationMsg);


        }
    }
}


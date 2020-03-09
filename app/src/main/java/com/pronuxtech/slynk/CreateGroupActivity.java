package com.pronuxtech.slynk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.annotations.NonNull;

import static android.view.View.VISIBLE;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener, CreateGroupActivityAdapter.ChangeStatusListener {

    //    WIDGETS
    private ImageView createGroupBackArrow;
    private TextView nextBtn;
    private RecyclerView recyclerView;

    //    VARIABLES
    private String currentUserId = "";


    //    ARRAYLIST
    private List<GroupCreateUserSeleceModelClass> userLists;
    private LinearLayoutManager linearLayoutManager;
    private CreateGroupActivityAdapter adapter;


    //    DATABASE REFERENCES
    private DatabaseReference RootRef, UsersRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        getSupportActionBar().hide();

        createGroupBackArrow = (ImageView) findViewById(R.id.createGroupBackArrow);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        nextBtn = (TextView) findViewById(R.id.nextBtn);

        RootRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = RootRef.child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        userLists = new ArrayList<>();

//        CLICK LISTENERS
        nextBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ShowAllUser();
    }


    private void ShowAllUser() {
        userLists.clear();
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GroupCreateUserSeleceModelClass model = snapshot.getValue(GroupCreateUserSeleceModelClass.class);
                        if (!model.getUid().equals(currentUserId)) {
                            userLists.add(model);
                        }
                    }
                }
                adapter = new CreateGroupActivityAdapter(userLists, getApplicationContext(),
                        (CreateGroupActivityAdapter.ChangeStatusListener) getApplicationContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextBtn:
                updateList();
                break;
        }
    }

    private void updateList() {
        ArrayList<GroupCreateUserSeleceModelClass> temp = new ArrayList<>();
        try {
            for (int i = 0; i < userLists.size(); i++) {
                if (userLists.get(i).getSelect()) {
                    temp.add(userLists.get(i));
                }
            }
        } catch (Exception e) {

        }

        userLists = temp;
        if (userLists.size() == 0) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemChangeListener(int position, GroupCreateUserSeleceModelClass model) {
        try {
            userLists.set(position, model);
        } catch (Exception e) {

        }
    }
}

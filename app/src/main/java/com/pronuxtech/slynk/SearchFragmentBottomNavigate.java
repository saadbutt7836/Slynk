package com.pronuxtech.slynk;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFragmentBottomNavigate extends Fragment {

    //    WIDGETS
    private EditText editText;
    private RecyclerView liveSearchRecycleView;

    //    ARRAY LIST
    ArrayList<LiveSearchModelClass> list;

    //    FIREBASE ADAPTER
    FirebaseRecyclerAdapter<LiveSearchModelClass, SearchFragmentBottomNavigate.LiveSearchViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<LiveSearchModelClass> options;


    DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment_bottom_navigate, container, false);

//        EditText editor = new EditText(getActivity());
//        editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


        editText = (EditText) view.findViewById(R.id.editText);
        liveSearchRecycleView = (RecyclerView) view.findViewById(R.id.liveSearchRecycleView);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        list = new ArrayList<>();
        options = new FirebaseRecyclerOptions
                .Builder<LiveSearchModelClass>()
                .setQuery(reference, LiveSearchModelClass.class).build();
        liveSearchRecycleView.setHasFixedSize(true);
        liveSearchRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    search(editable.toString());
                } else {
                    search("");
                }
            }
        });


        showRecycleView();


    }

    private void search(String s) {
        Query query = reference.orderByChild("lower_name")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Map<String, String> map = (Map<String, String>) snapshot.getValue();

//                        final LiveSearchModelClass liveSearchModelClass = snapshot.getValue(LiveSearchModelClass.class);
                        LiveSearchModelClass liveSearchModelClass =
                                new LiveSearchModelClass(snapshot.getKey(), map.get("lower_name"), map.get("username"), map.get("profile_img"));
                        list.add(liveSearchModelClass);
                    }

                    LiveSearchAdapter myAdapter = new LiveSearchAdapter(getActivity(), list);
                    liveSearchRecycleView.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String msg = databaseError.toException().getMessage();
                Toast.makeText(getActivity(), "error: " + msg, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void showRecycleView() {
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<LiveSearchModelClass, LiveSearchViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final LiveSearchViewHolder holder, final int position, @NonNull final LiveSearchModelClass model) {
                holder.textView_FullName.setText(model.getLower_name());
                holder.textView_SearchUserName.setText(model.getUsername());
                Picasso.get().load(model.getProfile_img()).into(holder.imageView_SearchProfile);

                holder.liveSearchLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String searchedUserKey = getRef(position).getKey();
//                        Toast.makeText(getActivity(), searchedUserKey, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), UserSearchProfileActivity.class);
                        intent.putExtra("search_user_key", searchedUserKey);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public LiveSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_search_layout, parent, false);
                return new LiveSearchViewHolder(view);
            }
        };
        liveSearchRecycleView.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.startListening();
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
}

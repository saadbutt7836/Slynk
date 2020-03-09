package com.pronuxtech.slynk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class PeopleFragmentChat extends Fragment {

    //    WIDGETS
    private RecyclerView peopleRecycleView;

    //    LISTS
    private List<PeopleDataModelClass> peopleDataModelClassList;

    //    VIEWS
    View view;

    public PeopleFragmentChat() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.people_fragment_chat, container, false);

//        WIDGETS CASTING
        peopleRecycleView = (RecyclerView) view.findViewById(R.id.peopleRecycleView);
        PeopleDataAdapter peopleDataAdapter = new PeopleDataAdapter(getContext(), peopleDataModelClassList);
        peopleRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        peopleRecycleView.setAdapter(peopleDataAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        peopleDataModelClassList = new ArrayList<>();

        peopleDataModelClassList.add(new PeopleDataModelClass(1, "Annie Michel", "hello, how are you?", "11:07", "9", R.drawable.profile));
        peopleDataModelClassList.add(new PeopleDataModelClass(1, " johny Bravo", "hello, what about Today?", "1:07", "3", R.drawable.profile));
    }
}

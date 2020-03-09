package com.pronuxtech.slynk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.pronuxtech.slynk.R;


public class GroupsFragmentChat extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.groups_fragment_chat, container, false);

        return view;
    }

}

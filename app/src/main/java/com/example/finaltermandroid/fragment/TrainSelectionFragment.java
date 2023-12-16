package com.example.finaltermandroid.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finaltermandroid.R;

public class TrainSelectionFragment extends Fragment {
    public TrainSelectionFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_train_selection, container, false);

        //RecyclerView recyclerView = view.findViewById(R.id.recyclerViewTrains);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recyclerView.setAdapter(new TrainAdapter());

        return view;
    }
}
package com.example.finaltermandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.adapter.NotificationAdapter;
import com.example.finaltermandroid.adapter.TicketAdapter;
import com.example.finaltermandroid.model.Notification;
import com.example.finaltermandroid.model.Ticket;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {
    RecyclerView recyclerNotification;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notifcation, container, false);
        recyclerNotification = view.findViewById(R.id.recyclerNotification);
        recyclerNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        DatabaseReference notificationRefs = FirebaseDatabase.getInstance().getReference().child("notification");
        notificationRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notification> notificationList = new ArrayList<>();
                for(DataSnapshot notificationSnapshot: snapshot.getChildren()){
                    Notification notification = notificationSnapshot.getValue(Notification.class);
                    notificationList.add(notification);
                }
                Collections.reverse(notificationList);
                NotificationAdapter adapter = new NotificationAdapter(getContext(),notificationList);
                recyclerNotification.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}

package com.example.finaltermandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.adapter.TicketAdapter;
import com.example.finaltermandroid.adapter.TrainCarriageAdapter;
import com.example.finaltermandroid.model.Ticket;
import com.example.finaltermandroid.model.TrainCarriage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TicketFragment extends Fragment implements TicketAdapter.OnButtonETicketClickListener , TicketAdapter.OnButtonViewMapClickListener{
    RecyclerView recyclerTicket;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);
        recyclerTicket = view.findViewById(R.id.recyclerTicket);
        DatabaseReference ticketRefs = FirebaseDatabase.getInstance().getReference().child("ticket");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        recyclerTicket.setLayoutManager(new LinearLayoutManager(getContext()));
        ticketRefs.orderByChild("accountEmail").equalTo(currentUser.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Ticket> ticketList = new ArrayList<>();
                for(DataSnapshot ticketSnapshot: snapshot.getChildren()){
                    Ticket ticket = ticketSnapshot.getValue(Ticket.class);
                    ticketList.add(ticket);
                }
                TicketAdapter adapter = new TicketAdapter(ticketList,getContext());
                adapter.setOnButtonETicketClickListener(TicketFragment.this);
                adapter.setOnButtonViewMapClickListener(TicketFragment.this);
                recyclerTicket.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    @Override
    public void OnButtonETicketClick(int position) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new ETicketFragment(position));
        fragmentTransaction.commit();
    }

    @Override
    public void OnButtonViewMapClick(int position) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new StationMapFragment(position));
        fragmentTransaction.commit();
    }
}

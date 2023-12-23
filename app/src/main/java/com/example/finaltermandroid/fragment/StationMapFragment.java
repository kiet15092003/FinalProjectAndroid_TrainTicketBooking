package com.example.finaltermandroid.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.model.Ticket;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StationMapFragment extends Fragment {

    int ticketPosition;
    TextView tv_DepartureStation, tv_DestinationStation;
    Button btn_chooseDeparture, btn_chooseDestination;
    private double departureLatitude, departureLongitude, destinationLatitude, destinationLongitude;
    public StationMapFragment(int ticketPosition){
        this.ticketPosition = ticketPosition;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_station_map, container, false);
        tv_DepartureStation = view.findViewById(R.id.tv_DepartureStation);
        btn_chooseDeparture = view.findViewById(R.id.btn_chooseDeparture);
        tv_DestinationStation = view.findViewById(R.id.tv_DestinationStation);
        btn_chooseDestination = view.findViewById(R.id.btn_chooseDestination);
        DatabaseReference ticketRefs = FirebaseDatabase.getInstance().getReference().child("ticket");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Toast.makeText(getContext(),String.valueOf(ticketPosition),Toast.LENGTH_LONG).show();
        ticketRefs.orderByChild("accountEmail").equalTo(currentUser.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for(DataSnapshot ticketSnapshot: snapshot.getChildren()){
                    if (count == ticketPosition){
                        Ticket ticket = ticketSnapshot.getValue(Ticket.class);
                        DatabaseReference seatRef = FirebaseDatabase.getInstance().getReference().child("seatsBooked").child(ticket.getSeatBookedId());
                        seatRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                DatabaseReference trainScheduleRef = FirebaseDatabase.getInstance().getReference().child("trainSchedule").
                                        child(snapshot.child("trainSchedule").getValue(String.class));
                                trainScheduleRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        DatabaseReference stationScheduleRef = FirebaseDatabase.getInstance().getReference().child("stationSchedule").
                                                child(snapshot.child("stationSchedule").getValue(String.class));
                                        stationScheduleRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                DatabaseReference departureStationRef = FirebaseDatabase.getInstance().getReference().child("train station").
                                                        child(snapshot.child("departureStation").getValue(String.class));
                                                departureStationRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String text = snapshot.child("name").getValue(String.class);
                                                        departureLatitude = snapshot.child("latitude").getValue(Double.class);
                                                        departureLongitude = snapshot.child("longitude").getValue(Double.class);
                                                        tv_DepartureStation.setText(text);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                                DatabaseReference destinationStationRef = FirebaseDatabase.getInstance().getReference().child("train station").
                                                        child(snapshot.child("destinationStation").getValue(String.class));
                                                destinationStationRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String text = snapshot.child("name").getValue(String.class);
                                                        destinationLatitude = snapshot.child("latitude").getValue(Double.class);
                                                        destinationLongitude = snapshot.child("longitude").getValue(Double.class);
                                                        tv_DestinationStation.setText(text);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btn_chooseDeparture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_chooseDeparture.setBackgroundColor(Color.parseColor("#FF6319"));
                btn_chooseDeparture.setText("viewing");
                btn_chooseDestination.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btn_chooseDestination.setText("view");
                GetMapFragment(departureLatitude,departureLongitude);
            }
        });
        btn_chooseDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_chooseDestination.setBackgroundColor(Color.parseColor("#FF6319"));
                btn_chooseDestination.setText("viewing");
                btn_chooseDeparture.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btn_chooseDeparture.setText("view");
                GetMapFragment(destinationLatitude,destinationLongitude);
            }
        });
        return view;
    }
    public void GetMapFragment(double latitude, double longitude){
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                LatLng point = new LatLng(latitude,longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(point);
                markerOptions.title(point.latitude + "KG" + point.longitude);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,20));
                googleMap.addMarker(markerOptions);
            }
        });
    }
}

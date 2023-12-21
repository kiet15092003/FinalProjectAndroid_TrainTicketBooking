package com.example.finaltermandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.adapter.TicketAdapter;
import com.example.finaltermandroid.model.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ETicketFragment extends Fragment {
    TextView tv_departureTime, tv_departureDate, tv_departureStation,tv_destinationTime,tv_destinationDate,tv_departureCity,
            tv_destinationCity,
            tv_destinationStation,tv_trainNumber,tv_customerName,tv_seatNumber,tv_serviceName,tv_discountKey,
            tv_seatPrice,tv_servicePrice,tv_discountValue,tv_totalPayment,tv_ticketId;
    private int position;
    public ETicketFragment(int position){
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eticket, container, false);
        tv_departureTime = view.findViewById(R.id.tv_departureTime);
        tv_departureDate = view.findViewById(R.id.tv_departureDate);
        tv_departureStation = view.findViewById(R.id.tv_departureStation);
        tv_destinationTime = view.findViewById(R.id.tv_destinationTime);
        tv_destinationDate = view.findViewById(R.id.tv_destinationDate);
        tv_destinationStation = view.findViewById(R.id.tv_destinationStation);
        tv_trainNumber = view.findViewById(R.id.tv_trainNumber);
        tv_customerName = view.findViewById(R.id.tv_customerName);
        tv_seatNumber = view.findViewById(R.id.tv_seatNumber);
        tv_serviceName = view.findViewById(R.id.tv_serviceName);
        tv_discountKey = view.findViewById(R.id.tv_discountKey);
        tv_seatPrice = view.findViewById(R.id.tv_seatPrice);
        tv_servicePrice = view.findViewById(R.id.tv_servicePrice);
        tv_discountValue = view.findViewById(R.id.tv_discountValue);
        tv_totalPayment = view.findViewById(R.id.tv_totalPayment);
        tv_departureCity = view.findViewById(R.id.tv_departureCity);
        tv_destinationCity = view.findViewById(R.id.tv_destinationCity);
        tv_ticketId = view.findViewById(R.id.tv_ticketId);

        DatabaseReference ticketRefs = FirebaseDatabase.getInstance().getReference().child("ticket");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ticketRefs.orderByChild("accountEmail").equalTo(currentUser.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for(DataSnapshot ticketSnapshot: snapshot.getChildren()){
                    if (count==position){
                        Ticket ticket = ticketSnapshot.getValue(Ticket.class);
                        tv_ticketId.setText(ticketSnapshot.getKey());
                        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("customer").child(ticket.getCustomerId());
                        customerRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tv_customerName.setText(snapshot.child("name").getValue(String.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //Load textview seat
                        DatabaseReference seatRef = FirebaseDatabase.getInstance().getReference().child("seatsBooked").child(ticket.getSeatBookedId());
                        seatRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tv_seatNumber.setText(snapshot.child("seatNumber").getValue(String.class));
                                tv_seatPrice.setText(String.valueOf(snapshot.child("price").getValue(Long.class)/1000)+"k");
                                //Set textview departure and destination time
                                DatabaseReference trainScheduleRef = FirebaseDatabase.getInstance().getReference().child("trainSchedule").
                                        child(snapshot.child("trainSchedule").getValue(String.class));
                                trainScheduleRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        tv_departureTime.setText(snapshot.child("departureTime").getValue(String.class));
                                        tv_destinationTime.setText(snapshot.child("destinationTime").getValue(String.class));
                                        tv_trainNumber.setText(snapshot.child("trainNumber").getValue(String.class));
                                        // Set textview departure and destination date
                                        DatabaseReference stationScheduleRef = FirebaseDatabase.getInstance().getReference().child("stationSchedule").
                                                child(snapshot.child("stationSchedule").getValue(String.class));
                                        stationScheduleRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                tv_departureDate.setText(snapshot.child("departureDate").getValue(String.class));
                                                tv_destinationDate.setText(snapshot.child("destinationDate").getValue(String.class));
                                                //Set text view station
                                                DatabaseReference departureStationRef = FirebaseDatabase.getInstance().getReference().child("train station").
                                                        child(snapshot.child("departureStation").getValue(String.class));
                                                departureStationRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                       tv_departureStation.setText(snapshot.child("name").getValue(String.class));
                                                       tv_departureCity.setText(snapshot.child("city").getValue(String.class));
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
                                                        tv_destinationStation.setText(snapshot.child("name").getValue(String.class));
                                                        tv_destinationCity.setText(snapshot.child("city").getValue(String.class));
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
                        //Load service
                        if (!ticket.getServiceId().equals("Not service")){
                            DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference().child("service").child(ticket.getServiceId());
                            serviceRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    tv_servicePrice.setText(String.valueOf(snapshot.child("price").getValue(Long.class)/1000)+"k");
                                    tv_serviceName.setText(snapshot.child("name").getValue(String.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            tv_servicePrice.setText("0k");
                            tv_serviceName.setText("Not service");
                        }
                        //Load discount
                        if (!ticket.getDiscountId().equals("Choose discount")){
                            DatabaseReference discountRef = FirebaseDatabase.getInstance().getReference().child("discount").child(ticket.getDiscountId());
                            discountRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    tv_discountKey.setText(snapshot.child("discountKey").getValue(String.class));
                                    tv_discountValue.setText(String.valueOf(snapshot.child("discountValue").getValue(Double.class)*100)+"%");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            tv_discountKey.setText("Not discount");
                            tv_discountValue.setText("0%");
                        }
                        tv_totalPayment.setText(String.valueOf(ticket.getTotalMoney()/1000) + "k");
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}

package com.example.finaltermandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.model.Discount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PointFragment extends Fragment {
    TextView tv_totalPayment, tv_point;
    Button btn_chooseDiscount20,btn_chooseDiscount30,btn_chooseDiscount50,btn_chooseDiscount100;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_point, container, false);
        tv_totalPayment = view.findViewById(R.id.tv_totalPayment);
        tv_point = view.findViewById(R.id.tv_point);
        btn_chooseDiscount20 = view.findViewById(R.id.btn_chooseDiscount20);
        btn_chooseDiscount30 = view.findViewById(R.id.btn_chooseDiscount30);
        btn_chooseDiscount50 = view.findViewById(R.id.btn_chooseDiscount50);
        btn_chooseDiscount100 = view.findViewById(R.id.btn_chooseDiscount100);
        tv_totalPayment.setText("0");
        GetTotalPayment(tv_totalPayment);
        GetPoint(tv_point);
        btn_chooseDiscount20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonClickSetPoint(600,0.2);
            }
        });
        btn_chooseDiscount30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonClickSetPoint(900,0.3);
            }
        });
        btn_chooseDiscount50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonClickSetPoint(1400,0.5);
            }
        });
        btn_chooseDiscount100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonClickSetPoint(2500,1);
            }
        });
        return view;
    }
    public void GetTotalPayment(TextView tv){
        DatabaseReference ticketRefs = FirebaseDatabase.getInstance().getReference().child("ticket");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ticketRefs.orderByChild("accountEmail").equalTo(currentUser.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ticketSnapshot: snapshot.getChildren()){
                    processSeat(ticketSnapshot.child("seatBookedId").getValue(String.class), new OnTotalPaymentSumListener() {
                        @Override
                        public void onTotalPaymentSumReceived(long totalPaymentSum) {
                            long textOld = Long.parseLong(tv.getText().toString());
                            long textNew =  textOld + totalPaymentSum;
                            tv.setText(String.valueOf(textNew));
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public interface OnTotalPaymentSumListener {
        void onTotalPaymentSumReceived(long totalPaymentSum);
    }
    public void processSeat(String seatId, OnTotalPaymentSumListener listener) {
        DatabaseReference seatBookedRef = FirebaseDatabase.getInstance().getReference().child("seatsBooked")
                .child(seatId);
        seatBookedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalPaymentSum = snapshot.child("price").getValue(Long.class);
                listener.onTotalPaymentSumReceived(totalPaymentSum);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }
    public void GetPoint(TextView tv){
        DatabaseReference accountRefs = FirebaseDatabase.getInstance().getReference().child("accounts");
        accountRefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String emailUser = currentUser.getEmail();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    if (snapshot1.child("email").getValue(String.class).equals(emailUser)){
                        tv.setText(String.valueOf(snapshot1.child("point").getValue(Long.class)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void SetPoint(long point){
        DatabaseReference accountRefs = FirebaseDatabase.getInstance().getReference().child("accounts");
        accountRefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String emailUser = currentUser.getEmail();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    if (snapshot1.child("email").getValue(String.class).equals(emailUser)){
                        accountRefs.child(snapshot1.getKey()).child("point").setValue(point);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void ButtonClickSetPoint(int pointValue, double discountValue){
        if (Long.parseLong(tv_point.getText().toString())<pointValue){
            Toast.makeText(getContext(),"Your point is not enough",Toast.LENGTH_LONG).show();
        } else {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            DatabaseReference discountRef = FirebaseDatabase.getInstance().getReference().child("discount");
            String discountId = discountRef.push().getKey();
            Discount newDiscount = new Discount(currentUser.getEmail(),discountId,discountValue,true);
            discountRef.child(discountId).setValue(newDiscount);
            SetPoint(Long.parseLong(tv_point.getText().toString())-pointValue);
            tv_point.setText(String.valueOf(Long.parseLong(tv_point.getText().toString())-pointValue));
            Toast.makeText(getContext(),"Change point to discount voucher successfully",Toast.LENGTH_LONG).show();
        }
    }
}

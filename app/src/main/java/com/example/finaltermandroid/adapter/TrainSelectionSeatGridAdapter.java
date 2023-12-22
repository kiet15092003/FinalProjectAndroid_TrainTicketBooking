package com.example.finaltermandroid.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.finaltermandroid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrainSelectionSeatGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mData;
    private String trainInfo;

    public TrainSelectionSeatGridAdapter(Context mContext, List<String> mData, String trainInfo) {
        this.mContext = mContext;
        this.mData = mData;
        this.trainInfo = trainInfo;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.grid_seats_item, null);
        } else {
            gridView = convertView;
        }
        Button button = gridView.findViewById(R.id.gridItemButton);
        String itemName = mData.get(position);
        button.setText(itemName);
        String[] infoTrains = trainInfo.split(" - ");
        String trainNumber = infoTrains[0];
        String departureTime = infoTrains[1];
        String departureDate = infoTrains[2];
        DatabaseReference trainScheduleRefs = FirebaseDatabase.getInstance().getReference("trainSchedule");
        trainScheduleRefs.orderByChild("trainNumber").equalTo(trainNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot trainScheduleSnapshot: snapshot.getChildren()){
                    if (trainScheduleSnapshot.exists()){
                        if (trainScheduleSnapshot.child("departureTime").getValue(String.class).equals(departureTime)){
                            String stationSchedule = trainScheduleSnapshot.child("stationSchedule").getValue(String.class);
                            DatabaseReference stationScheduleRef = FirebaseDatabase.getInstance().getReference("stationSchedule").child(stationSchedule);
                            stationScheduleRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        if (snapshot.child("departureDate").getValue(String.class).equals(departureDate)){
                                            DatabaseReference seatBookedRefs = FirebaseDatabase.getInstance().getReference("seatsBooked");
                                            seatBookedRefs.orderByChild("trainSchedule").equalTo(trainScheduleSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()){
                                                        List<String> seatBookedList = new ArrayList<>();
                                                        for (DataSnapshot seatBookedSnapShot : snapshot.getChildren()){
                                                            String seatNumberValue = seatBookedSnapShot.child("seatNumber").getValue(String.class);
                                                            seatBookedList.add(seatNumberValue);
                                                        }
                                                        String itemName = mData.get(position);
                                                        if (seatBookedList.contains(itemName)){
                                                            button.setBackgroundResource(R.drawable.btn_notselect_seattype);
                                                            button.setEnabled(false);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSeatItemClick(itemName);
                }
            }
        });
        return gridView;
    }
    public interface OnItemSeatClickListener {
        void onSeatItemClick(String seatNumber);
    }
    private TrainSelectionSeatGridAdapter.OnItemSeatClickListener listener;
    public void setOnItemSeatClickListener(TrainSelectionSeatGridAdapter.OnItemSeatClickListener listener) {
        this.listener = listener;
    }
}

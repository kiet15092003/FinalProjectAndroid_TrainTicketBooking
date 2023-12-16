package com.example.finaltermandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.model.TrainSchedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TrainBookingAdapter extends RecyclerView.Adapter<TrainBookingAdapter.myViewHolder>{
    private List<TrainSchedule> trainScheduleList;
    private Context context;

    public TrainBookingAdapter(List<TrainSchedule> trainScheduleList, Context context) {
        this.trainScheduleList = trainScheduleList;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_train, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        TrainSchedule trainSchedule = trainScheduleList.get(position);

        holder.tv_arrivalTime.setText(trainSchedule.getDestinationTime());
        holder.tv_trainNumber.setText(trainSchedule.getTrainNumber());
        DatabaseReference stationScheduleRef = FirebaseDatabase.getInstance().getReference().child("stationSchedule").child(trainSchedule.getStationSchedule());
        stationScheduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tv_departureTime.setText(trainSchedule.getDepartureTime() + " | "
                    + snapshot.child("departureDate").getValue(String.class));
                holder.tv_arrivalTime.setText(trainSchedule.getDestinationTime() + " | "
                        + snapshot.child("destinationDate").getValue(String.class));
                DatabaseReference stationRef = FirebaseDatabase.getInstance().getReference().
                        child("train station").child(snapshot.child("departureStation").getValue(String.class));
                stationRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.tv_departureStation.setText(snapshot.child("name").getValue(String.class));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                DatabaseReference station2Ref = FirebaseDatabase.getInstance().getReference().
                        child("train station").child(snapshot.child("destinationStation").getValue(String.class));
                station2Ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.tv_arrivalStation.setText(snapshot.child("name").getValue(String.class));
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
        holder.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "abc",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return trainScheduleList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_departureTime, tv_arrivalTime,tv_departureStation,tv_arrivalStation,tv_trainNumber,tv_seatAvailable;
        public Button btnChoose;
        public myViewHolder(View view) {
            super(view);
            tv_departureTime = view.findViewById(R.id.tv_departureTime);
            tv_arrivalTime = view.findViewById(R.id.tv_arrivalTime);
            tv_departureStation = view.findViewById(R.id.tv_departureStation);
            tv_arrivalStation = view.findViewById(R.id.tv_arrivalStation);
            tv_trainNumber = view.findViewById(R.id.tv_trainNumber);
            tv_seatAvailable = view.findViewById(R.id.tv_seatAvailable);
            btnChoose = view.findViewById(R.id.btnChoose);

        }
    }
}

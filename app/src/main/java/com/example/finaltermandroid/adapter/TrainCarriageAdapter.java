package com.example.finaltermandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.model.TrainCarriage;
import com.example.finaltermandroid.model.TrainSchedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TrainCarriageAdapter extends RecyclerView.Adapter<TrainCarriageAdapter.ViewHolder> {
    private List<TrainCarriage> mData;
    private LayoutInflater mInflater;
    private String selectedInfoTrain;

    public TrainCarriageAdapter(Context context, List<TrainCarriage> mData,String selectedInfoTrain) {
        this.mData = mData;
        this.mInflater =  LayoutInflater.from(context);
        this.selectedInfoTrain = selectedInfoTrain;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_item_carriage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TrainCarriage trainCarriage = mData.get(position);
        String[] infoTrains = selectedInfoTrain.split(" - ");
        String trainNumber = infoTrains[0];
        String departureTime = infoTrains[1];
        String departureDate = infoTrains[2];
        DatabaseReference trainScheduleRefs = FirebaseDatabase.getInstance().getReference("trainSchedule");
        trainScheduleRefs.orderByChild("trainNumber").equalTo(trainNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot trainScheduleSnapshot: snapshot.getChildren()){
                    if (trainScheduleSnapshot.child("departureTime").getValue(String.class).equals(departureTime)){
                        String stationSchedule = trainScheduleSnapshot.child("stationSchedule").getValue(String.class);
                        DatabaseReference stationScheduleRef = FirebaseDatabase.getInstance().getReference("stationSchedule").child(stationSchedule);
                        stationScheduleRef.orderByChild("departureDate").equalTo(departureDate).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                DatabaseReference seatTypeRef = FirebaseDatabase.getInstance().getReference("seatTypes").child(trainCarriage.getSeatType());
                                seatTypeRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            Double coefficient = snapshot.child("coefficient").getValue(Double.class);
                                            double price = Integer.parseInt(trainScheduleSnapshot.child("price").getValue(Long.class).toString()) * coefficient;
                                            holder.tv_price.setText(String.valueOf((int) price / 1000) + "k" );
                                            String name = snapshot.child("name").getValue(String.class);
                                            holder.tv_SeatType.setText(name);
                                        }
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.tv_Carriage.setText("Coach " + String.valueOf(trainCarriage.getCarriageNumber()) + ": ");
        holder.btn_chooseCoach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCarriageItemClick(trainCarriage.getSeatType(), trainCarriage.getCarriageNumber(), holder.tv_price.getText().toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_Carriage,tv_SeatType,tv_price;
        LinearLayout btn_chooseCoach;

        ViewHolder(View itemView) {
            super(itemView);
            tv_Carriage = itemView.findViewById(R.id.tv_Carriage);
            tv_SeatType = itemView.findViewById(R.id.tv_SeatType);
            tv_price = itemView.findViewById(R.id.tv_price);
            btn_chooseCoach = itemView.findViewById(R.id.btn_chooseCoach);
        }
    }

    public interface OnItemClickListener {
        void onCarriageItemClick(String seatType, int carriageNumber, String price);
    }
    private OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

package com.example.finaltermandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.model.Ticket;
import com.example.finaltermandroid.model.TrainSchedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.myViewHolder>{
    private List<Ticket> ticketList;
    private Context context;
    public TicketAdapter(List<Ticket> ticketList, Context context) {
        this.ticketList = ticketList;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_ticket, parent, false);
        return new TicketAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        //Load textview Customer
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("customer").child(ticket.getCustomerId());
        customerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tv_customerName.setText(snapshot.child("name").getValue(String.class));
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
                holder.tv_seatNumber.setText(snapshot.child("seatNumber").getValue(String.class));
                holder.tv_seatPrice.setText(String.valueOf(snapshot.child("price").getValue(Long.class)/1000)+"k");
                //Set textview departure and destination time
                DatabaseReference trainScheduleRef = FirebaseDatabase.getInstance().getReference().child("trainSchedule").
                        child(snapshot.child("trainSchedule").getValue(String.class));
                trainScheduleRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.tv_departureTime.setText(snapshot.child("departureTime").getValue(String.class));
                        holder.tv_destinationTime.setText(snapshot.child("destinationTime").getValue(String.class));
                        holder.tv_trainNumber.setText(snapshot.child("trainNumber").getValue(String.class));
                        // Set textview departure and destination date
                        DatabaseReference stationScheduleRef = FirebaseDatabase.getInstance().getReference().child("stationSchedule").
                                child(snapshot.child("stationSchedule").getValue(String.class));
                        stationScheduleRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.tv_departureDate.setText(snapshot.child("departureDate").getValue(String.class));
                                holder.tv_destinationDate.setText(snapshot.child("destinationDate").getValue(String.class));
                                //Set text view station
                                DatabaseReference departureStationRef = FirebaseDatabase.getInstance().getReference().child("train station").
                                        child(snapshot.child("departureStation").getValue(String.class));
                                departureStationRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        holder.tv_departureStation.setText(snapshot.child("name").getValue(String.class));
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
                                        holder.tv_destinationStation.setText(snapshot.child("name").getValue(String.class));
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
                    holder.tv_servicePrice.setText(String.valueOf(snapshot.child("price").getValue(Long.class)/1000)+"k");
                    holder.tv_serviceName.setText(snapshot.child("name").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            holder.tv_servicePrice.setText("0k");
            holder.tv_serviceName.setText("Not service");
        }
        //Load discount
        if (!ticket.getDiscountId().equals("Choose discount")){
            DatabaseReference discountRef = FirebaseDatabase.getInstance().getReference().child("discount").child(ticket.getDiscountId());
            discountRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    holder.tv_discountKey.setText(snapshot.child("discountKey").getValue(String.class));
                    holder.tv_discountValue.setText(String.valueOf(snapshot.child("discountValue").getValue(Double.class)*100)+"%");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            holder.tv_discountKey.setText("Not discount");
            holder.tv_discountValue.setText("0%");
        }
        holder.tv_totalPayment.setText(String.valueOf(ticket.getTotalMoney()/1000) + "k");
        int positionTicket = position;
        holder.btnViewETicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnButtonETicketClick(positionTicket);
                }
            }
        });
        holder.btn_StationMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listenerViewMap.OnButtonViewMapClick(positionTicket);
                }
            }
        });
        holder.btn_shareTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                String textShare = "I have a journey from " + holder.tv_departureStation.getText().toString() + " in "
                        + holder.tv_departureDate.getText().toString() + " at " + holder.tv_departureTime.getText().toString()
                        + " to " + holder.tv_destinationStation.getText().toString() + " in "
                        + holder.tv_destinationDate.getText().toString() + " at " + holder.tv_destinationTime.getText().toString()
                        + ". I will go on train number " + holder.tv_trainNumber.getText().toString() + " and enjoy the journey.";
                intent.putExtra(Intent.EXTRA_TEXT, textShare);
                intent.setType("text/plain");
                if (intent.resolveActivity(context.getPackageManager())!=null){
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView tv_departureTime, tv_departureDate, tv_departureStation,tv_destinationTime,tv_destinationDate,
                tv_destinationStation,tv_trainNumber,tv_customerName,tv_seatNumber,tv_serviceName,tv_discountKey,
                tv_seatPrice,tv_servicePrice,tv_discountValue,tv_totalPayment;
        LinearLayout detailView;
        Button btn_shareTrain,btn_StationMap,btnViewETicket;
        CardView recCard;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_departureTime = itemView.findViewById(R.id.tv_departureTime);
            tv_departureDate = itemView.findViewById(R.id.tv_departureDate);
            tv_departureStation = itemView.findViewById(R.id.tv_departureStation);
            tv_destinationTime = itemView.findViewById(R.id.tv_destinationTime);
            tv_destinationDate = itemView.findViewById(R.id.tv_destinationDate);
            tv_destinationStation = itemView.findViewById(R.id.tv_destinationStation);
            tv_trainNumber = itemView.findViewById(R.id.tv_trainNumber);
            tv_customerName = itemView.findViewById(R.id.tv_customerName);
            tv_seatNumber = itemView.findViewById(R.id.tv_seatNumber);
            tv_serviceName = itemView.findViewById(R.id.tv_serviceName);
            tv_discountKey = itemView.findViewById(R.id.tv_discountKey);
            tv_seatPrice = itemView.findViewById(R.id.tv_seatPrice);
            tv_servicePrice = itemView.findViewById(R.id.tv_servicePrice);
            tv_discountValue = itemView.findViewById(R.id.tv_discountValue);
            tv_totalPayment = itemView.findViewById(R.id.tv_totalPayment);
            btn_shareTrain = itemView.findViewById(R.id.btn_shareTrain);
            btnViewETicket = itemView.findViewById(R.id.btnViewETicket);
            btn_StationMap = itemView.findViewById(R.id.btn_StationMap);
            detailView = itemView.findViewById(R.id.detailView);
            recCard = itemView.findViewById(R.id.recCard);
        }
    }
    public interface OnButtonETicketClickListener {
        void OnButtonETicketClick(int position);
    }
    private TicketAdapter.OnButtonETicketClickListener listener;
    public void setOnButtonETicketClickListener(TicketAdapter.OnButtonETicketClickListener listener) {
        this.listener = listener;
    }

    public interface OnButtonViewMapClickListener {
        void OnButtonViewMapClick(int position);
    }
    private TicketAdapter.OnButtonViewMapClickListener listenerViewMap;
    public void setOnButtonViewMapClickListener(TicketAdapter.OnButtonViewMapClickListener listener) {
        this.listenerViewMap = listener;
    }
}

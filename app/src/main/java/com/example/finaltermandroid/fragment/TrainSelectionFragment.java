package com.example.finaltermandroid.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.adapter.TrainBookingAdapter;
import com.example.finaltermandroid.model.TrainSchedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrainSelectionFragment extends Fragment  implements TrainBookingAdapter.OnButtonClickListener  {
    RecyclerView rv_TrainSelection;
    SwitchCompat switchCompatReturn;
    LinearLayout ll_arrival;
    private String selectedDepartureStationSchedule,selectedArrivalStationSchedule;
    TextView tv_DepartureSelection,tv_ArrivalSelection;
    Button btnChooseSeat;
    private boolean isReturn;

    public TrainSelectionFragment(String selectedDepartureStationSchedule, boolean isReturn){
        this.selectedDepartureStationSchedule =selectedDepartureStationSchedule;
        this.isReturn = isReturn;
    }
    public TrainSelectionFragment(String selectedDepartureStationSchedule, String selectedArrivalStationSchedule,  boolean isReturn){
        this.selectedArrivalStationSchedule =selectedArrivalStationSchedule;
        this.selectedDepartureStationSchedule = selectedDepartureStationSchedule;
        this.isReturn = isReturn;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train_selection, container, false);
        rv_TrainSelection = view.findViewById(R.id.rv_TrainSelection);
        ll_arrival = view.findViewById(R.id.ll_arrival);
        switchCompatReturn = view.findViewById(R.id.switchCompatReturn);
        tv_DepartureSelection = view.findViewById(R.id.tv_DepartureSelection);
        tv_ArrivalSelection = view.findViewById(R.id.tv_ArrivalSelection);
        btnChooseSeat = view.findViewById(R.id.btnChooseSeat);
        if (isReturn){
            ll_arrival.setVisibility(View.VISIBLE);
            switchCompatReturn.setVisibility(View.VISIBLE);
        } else{
            switchCompatReturn.setVisibility(View.GONE);
            ll_arrival.setVisibility(View.GONE);
            LoadRecyclerViewInfoDeparture(rv_TrainSelection,selectedDepartureStationSchedule);
        }
        LoadRecyclerViewInfoDeparture(rv_TrainSelection,selectedDepartureStationSchedule);
        switchCompatReturn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    //User choose arrival
                    LoadRecyclerViewInfoDeparture(rv_TrainSelection,selectedArrivalStationSchedule);
                } else{
                    LoadRecyclerViewInfoDeparture(rv_TrainSelection,selectedDepartureStationSchedule);
                }
            }
        });
        btnChooseSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReturn){
                    String departureSelectedValue = tv_DepartureSelection.getText().toString();
                    String arrivalSelectedValue = tv_ArrivalSelection.getText().toString();
                    if (departureSelectedValue.equals("Not select") || arrivalSelectedValue.equals("Not select")){
                        Toast.makeText(getContext(),"Please select all departure and arrival",Toast.LENGTH_LONG).show();
                    } else {
                        //Move to choose seat intent
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, new SeatSelectionFragment(
                                departureSelectedValue,arrivalSelectedValue,isReturn,selectedDepartureStationSchedule,selectedArrivalStationSchedule
                        ));
                        fragmentTransaction.commit();
                    }
                } else{
                    String departureSelectedValue = tv_DepartureSelection.getText().toString();
                    if (departureSelectedValue.equals("Not select")){
                        Toast.makeText(getContext(),"Please select departure",Toast.LENGTH_LONG).show();
                    } else {
                        //Move to choose seat intent
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, new SeatSelectionFragment(
                                departureSelectedValue,"",isReturn,selectedDepartureStationSchedule,""
                        ));
                        fragmentTransaction.commit();
                    }
                }
            }
        });
        return view;
    }
    public void LoadRecyclerViewInfoDeparture(RecyclerView rv_TrainSelection, String stationSchedule){
        if (stationSchedule!=""){
            rv_TrainSelection.setLayoutManager(new LinearLayoutManager(getContext()));
            DatabaseReference trainScheduleRefs = FirebaseDatabase.getInstance().getReference("trainSchedule");
            trainScheduleRefs.orderByChild("stationSchedule").equalTo(stationSchedule).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        List<TrainSchedule> trainScheduleList = new ArrayList<>();
                        for (DataSnapshot trainScheduleSnapshot : snapshot.getChildren()) {
                            TrainSchedule trainSchedule = trainScheduleSnapshot.getValue(TrainSchedule.class);
                            trainScheduleList.add(trainSchedule);
                        }
                        TrainBookingAdapter adapter = new TrainBookingAdapter(trainScheduleList,TrainSelectionFragment.this.getContext());
                        rv_TrainSelection.setAdapter(adapter);
                        adapter.setOnButtonClickListener(TrainSelectionFragment.this);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else{
            TrainBookingAdapter adapter = new TrainBookingAdapter(new ArrayList<>(),TrainSelectionFragment.this.getContext());
            rv_TrainSelection.setAdapter(adapter);
        }
    }
    @Override
    public void onButtonClick(String buttonText) {
        if (isReturn){
            if (switchCompatReturn.isChecked()) {
                tv_ArrivalSelection.setText(buttonText);
            } else{
                tv_DepartureSelection.setText(buttonText);
            }
        } else {
            tv_DepartureSelection.setText(buttonText);
        }
    }
}
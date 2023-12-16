package com.example.finaltermandroid.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finaltermandroid.MainActivity;
import com.example.finaltermandroid.R;
import com.example.finaltermandroid.activity.LoginActivity;
import com.example.finaltermandroid.adapter.TrainStationAdapter;
import com.example.finaltermandroid.model.TrainStation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    Spinner departureSpinner, destinationSpinner;
    TextView btnChooseDateDeparture, btnChooseDateArrival;
    LinearLayout roundTrip;
    SwitchCompat isReturnSwitch;
    Button btnSearchTrain;
    private String selectedDepartureStationId;
    private String selectedDestinationStationId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        departureSpinner = view.findViewById(R.id.DepartureSpinner);
        destinationSpinner = view.findViewById(R.id.DestinationSpinner);
        btnChooseDateDeparture = view.findViewById(R.id.btnChooseDateDeparture);
        btnChooseDateArrival = view.findViewById(R.id.btnChooseDateArrival);
        roundTrip = view.findViewById(R.id.roundTrip);
        isReturnSwitch = view.findViewById(R.id.isReturnSwitch);
        btnSearchTrain = view.findViewById(R.id.btnSearchTrain);
        roundTrip.setVisibility(View.GONE);
        SetAdapterSpinner(departureSpinner);
        SetAdapterSpinner(destinationSpinner);
        departureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TrainStation selectedTrainStation = (TrainStation) parent.getItemAtPosition(position);
                selectedDepartureStationId = selectedTrainStation.getId();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TrainStation selectedTrainStation = (TrainStation) parent.getItemAtPosition(position);
                selectedDestinationStationId = selectedTrainStation.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnChooseDateDeparture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(btnChooseDateDeparture);
            }
        });
        isReturnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //User choose return
                    roundTrip.setVisibility(View.VISIBLE);
                    btnChooseDateArrival.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDatePickerDialog(btnChooseDateArrival);
                        }
                    });
                } else {
                    roundTrip.setVisibility(View.GONE);
                }
            }
        });
        btnSearchTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference stationScheduleRefs = FirebaseDatabase.getInstance().getReference().child("stationSchedule");
                stationScheduleRefs.orderByChild("departureStation").equalTo(selectedDepartureStationId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot stationScheduleSnapshot : snapshot.getChildren()) {
                                String stationScheduleId = stationScheduleSnapshot.getKey();
                                DatabaseReference stationScheduleRef = FirebaseDatabase.getInstance().getReference().child("stationSchedule").child(stationScheduleId);
                                stationScheduleRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            if (snapshot.child("destinationStation").getValue(String.class).equals(selectedDestinationStationId)
                                                    && snapshot.child("departureDate").getValue(String.class).equals(btnChooseDateDeparture.getText().toString())){
                                                processData(snapshot.getKey());
                                            } else{
                                                processData("");
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                            }
                        } else {
                            processData("");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        });
        return view;
    }
    public void SetAdapterSpinner(Spinner spinner){
        DatabaseReference trainStationRefs = FirebaseDatabase.getInstance().getReference().child("train station");
        trainStationRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TrainStation> trainStationList = new ArrayList<>();
                for (DataSnapshot stationSnapshot : snapshot.getChildren()) {
                    TrainStation station = stationSnapshot.getValue(TrainStation.class);
                    trainStationList.add(station);
                }
                // add adapter in spinner
                TrainStationAdapter trainStationAdapter = new TrainStationAdapter(getContext(), trainStationList);
                spinner.setAdapter(trainStationAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void showDatePickerDialog(TextView tv) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Handle the chosen date
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        // You can use the selectedDate as needed
                        tv.setText(selectedDate);
                    }
                },
                year,
                month,
                day
        );
        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void processData(String data) {
        if (isReturnSwitch.isChecked()){
            DatabaseReference stationScheduleArrivalRefs = FirebaseDatabase.getInstance().getReference().child("stationSchedule");
            stationScheduleArrivalRefs.orderByChild("departureStation").equalTo(selectedDestinationStationId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot stationScheduleSnapshot : snapshot.getChildren()) {
                            String stationScheduleId = stationScheduleSnapshot.getKey();
                            DatabaseReference stationScheduleRef = FirebaseDatabase.getInstance().getReference().child("stationSchedule").child(stationScheduleId);
                            stationScheduleRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        if (snapshot.child("destinationStation").getValue(String.class).equals(selectedDepartureStationId)
                                                && snapshot.child("departureDate").getValue(String.class).equals(btnChooseDateArrival.getText().toString())){
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.frame_layout, new TrainSelectionFragment(data, snapshot.getKey(), true));
                                            fragmentTransaction.commit();
                                        } else {
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.frame_layout, new TrainSelectionFragment(data, "", true));
                                            fragmentTransaction.commit();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }
                    } else{
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, new TrainSelectionFragment(data, "", true));
                        fragmentTransaction.commit();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } else{
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new TrainSelectionFragment(data, false));
            fragmentTransaction.commit();
        }
    }
}

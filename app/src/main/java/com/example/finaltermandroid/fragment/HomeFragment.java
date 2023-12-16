package com.example.finaltermandroid.fragment;

import android.app.DatePickerDialog;
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
import com.example.finaltermandroid.R;
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
    List<String> stationScheduleList = new ArrayList<>();
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
                        for (DataSnapshot stationScheduleSnapshot : snapshot.getChildren()) {
                            String stationScheduleId = stationScheduleSnapshot.getKey();
                            DatabaseReference stationScheduleRef = FirebaseDatabase.getInstance().getReference().child("stationSchedule").child(stationScheduleId);
                            stationScheduleRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        if (snapshot.child("destinationStation").getValue(String.class).equals(selectedDestinationStationId)
                                            && snapshot.child("departureDate").getValue(String.class).equals(btnChooseDateDeparture.getText().toString())){
                                            Toast.makeText(getContext(),snapshot.getKey(),Toast.LENGTH_LONG).show();
                                            //stationScheduleList.add(snapshot.getKey());
                                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("stationSchedule", getContext().MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("departureStationSchedule",snapshot.getKey());
                                            editor.apply();
                                        };
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
                if (isReturnSwitch.isChecked()){
                    DatabaseReference stationScheduleArrivalRefs = FirebaseDatabase.getInstance().getReference().child("stationSchedule");
                    stationScheduleArrivalRefs.orderByChild("departureStation").equalTo(selectedDestinationStationId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot stationScheduleSnapshot : snapshot.getChildren()) {
                                String stationScheduleId = stationScheduleSnapshot.getKey();
                                DatabaseReference stationScheduleRef = FirebaseDatabase.getInstance().getReference().child("stationSchedule").child(stationScheduleId);
                                stationScheduleRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            if (snapshot.child("destinationStation").getValue(String.class).equals(selectedDepartureStationId)
                                                    && snapshot.child("departureDate").getValue(String.class).equals(btnChooseDateArrival.getText().toString())){
                                                Toast.makeText(getContext(),snapshot.getKey(),Toast.LENGTH_LONG).show();
                                                //stationScheduleList.add(snapshot.getKey());
                                                SharedPreferences sharedPreferences = getContext().getSharedPreferences("stationSchedule", getContext().MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("arrivalStationSchedule",snapshot.getKey());
                                                editor.apply();
                                            };
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("stationSchedule", getContext().MODE_PRIVATE);
                String arrivalStationSchedule = sharedPreferences.getString("arrivalStationSchedule", null);
                String departureStationSchedule = sharedPreferences.getString("departureStationSchedule", null);

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
}

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
import android.widget.ImageView;
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

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    Spinner departureSpinner, destinationSpinner;
    TextView btnChooseDateDeparture, btnChooseDateArrival, tv_numberOfCustomer;
    LinearLayout roundTrip;
    SwitchCompat isReturnSwitch;
    Button btnSearchTrain;
    ImageView btn_subtract,btn_plus;
    private String selectedDepartureStationId;
    private String selectedDestinationStationId;
    private boolean isReturn = false;
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
        tv_numberOfCustomer = view.findViewById(R.id.tv_numberOfCustomer);
        btn_subtract = view.findViewById(R.id.btn_subtract);
        btn_plus = view.findViewById(R.id.btn_plus);
        roundTrip.setVisibility(View.GONE);
        SetAdapterSpinner(departureSpinner);
        SetAdapterSpinner(destinationSpinner);
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(currentDate);
        btnChooseDateDeparture.setText(formattedDate);
        btnChooseDateArrival.setText(formattedDate);
        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_numberOfCustomer.setText(String.valueOf(Integer.parseInt(tv_numberOfCustomer.getText().toString())+1));
            }
        });
        btn_subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(tv_numberOfCustomer.getText().toString())>1){
                    tv_numberOfCustomer.setText(String.valueOf(Integer.parseInt(tv_numberOfCustomer.getText().toString())-1));
                }
            }
        });
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
                    isReturn = true;
                    //User choose return
                    roundTrip.setVisibility(View.VISIBLE);
                    btnChooseDateArrival.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDatePickerDialog(btnChooseDateArrival);
                        }
                    });
                } else {
                    isReturn = false;
                    roundTrip.setVisibility(View.GONE);
                }
            }
        });
        btnSearchTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDate departureDate = LocalDate.parse(btnChooseDateDeparture.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate currentDate = LocalDate.now();
                long daysDifference1 = ChronoUnit.DAYS.between(currentDate, departureDate);
                long daysDifference2 = 10000;
                long daysDifference3 = 10000;
                if (isReturn){
                    LocalDate arrivalDate = LocalDate.parse(btnChooseDateArrival.getText().toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    daysDifference2 = ChronoUnit.DAYS.between(currentDate, arrivalDate);
                    daysDifference3 = ChronoUnit.DAYS.between(departureDate,arrivalDate);
                }
                if (daysDifference1<0 || daysDifference2<0 || daysDifference3<0){
                    //Toast.makeText(getContext(),String.valueOf(daysDifference2),Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(),"Please choose your day again",Toast.LENGTH_LONG).show();
                } else {
                    DatabaseReference stationScheduleRefs = FirebaseDatabase.getInstance().getReference().child("stationSchedule");
                    stationScheduleRefs.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String selectedStationScheduleId = "";
                            for (DataSnapshot stationScheduleSnapshot : snapshot.getChildren()) {
                                String stationScheduleId = stationScheduleSnapshot.getKey();
                                String destinationStationValue =  stationScheduleSnapshot.child("destinationStation").getValue(String.class);
                                String departureStationValue = stationScheduleSnapshot.child("departureStation").getValue(String.class);
                                String departureDateValue = stationScheduleSnapshot.child("departureDate").getValue(String.class);
                                if (destinationStationValue.equals(selectedDestinationStationId) && departureStationValue.equals(selectedDepartureStationId)
                                    && departureDateValue.equals(btnChooseDateDeparture.getText().toString())){
                                    selectedStationScheduleId = stationScheduleId;
                                }
                            }
                            if (selectedStationScheduleId.equals("")){
                                processData("");
                            } else {
                                processData(selectedStationScheduleId);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
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
            DatabaseReference stationScheduleRefs = FirebaseDatabase.getInstance().getReference().child("stationSchedule");
            stationScheduleRefs.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String selectedStationScheduleId = "";
                    for (DataSnapshot stationScheduleSnapshot : snapshot.getChildren()) {
                        String stationScheduleId = stationScheduleSnapshot.getKey();
                        String destinationStationValue =  stationScheduleSnapshot.child("destinationStation").getValue(String.class);
                        String departureStationValue = stationScheduleSnapshot.child("departureStation").getValue(String.class);
                        String departureDateValue = stationScheduleSnapshot.child("departureDate").getValue(String.class);
                        if (destinationStationValue.equals(selectedDepartureStationId) && departureStationValue.equals(selectedDestinationStationId)
                                && departureDateValue.equals(btnChooseDateArrival.getText().toString())){
                            selectedStationScheduleId = stationScheduleId;
                        }
                    }
                    if (selectedStationScheduleId.equals("")){
                        //not have train arrival
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, new TrainSelectionFragment(data, "", true,
                                Integer.parseInt(tv_numberOfCustomer.getText().toString()),1));
                        fragmentTransaction.commit();
                    } else {
                        //have train arrival
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, new TrainSelectionFragment(data, selectedStationScheduleId, true,
                                Integer.parseInt(tv_numberOfCustomer.getText().toString()),1));
                        fragmentTransaction.commit();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } else{
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new TrainSelectionFragment(data, false,Integer.parseInt(tv_numberOfCustomer.getText().toString()),1));
            fragmentTransaction.commit();
        }
    }
}

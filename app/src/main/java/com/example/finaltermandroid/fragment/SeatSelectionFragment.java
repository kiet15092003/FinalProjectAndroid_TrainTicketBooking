package com.example.finaltermandroid.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.adapter.TrainBookingAdapter;
import com.example.finaltermandroid.adapter.TrainCarriageAdapter;
import com.example.finaltermandroid.adapter.TrainSelectionSeatGridAdapter;
import com.example.finaltermandroid.model.TrainCarriage;
import com.example.finaltermandroid.model.TrainSchedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SeatSelectionFragment extends Fragment implements TrainCarriageAdapter.OnItemClickListener, TrainSelectionSeatGridAdapter.OnItemSeatClickListener{
    GridView gridView;
    RecyclerView rv_TrainCarriage;
    TextView tv_DepartureSelection,tv_ArrivalSelection,tv_CoachInfo;
    LinearLayout ll_arrival;
    Button btnChooseService;
    private String selectedDepartureInfoTrain,selectedArrivalInfoTrain;
    private boolean isReturn;
    private boolean isChoseArrival = false;
    public SeatSelectionFragment(String selectedDepartureInfoTrain,String selectedArrivalInfoTrain, boolean isReturn){
        this.selectedArrivalInfoTrain = selectedArrivalInfoTrain;
        this.selectedDepartureInfoTrain = selectedDepartureInfoTrain;
        this.isReturn = isReturn;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seat_selection, container, false);
        gridView = view.findViewById(R.id.gridView);
        rv_TrainCarriage = view.findViewById(R.id.rv_TrainCarriage);
        tv_DepartureSelection = view.findViewById(R.id.tv_DepartureSelection);
        tv_ArrivalSelection = view.findViewById(R.id.tv_ArrivalSelection);
        ll_arrival = view.findViewById(R.id.ll_arrival);
        tv_CoachInfo = view.findViewById(R.id.tv_CoachInfo);
        btnChooseService = view.findViewById(R.id.btnChooseService);
        if (isReturn){
            ll_arrival.setVisibility(View.VISIBLE);
            tv_ArrivalSelection.setText(selectedArrivalInfoTrain);
            tv_DepartureSelection.setText(selectedDepartureInfoTrain);
        } else{
            tv_DepartureSelection.setText(selectedDepartureInfoTrain);
            ll_arrival.setVisibility(View.GONE);
        }
        LoadRecyclerViewCarriage(rv_TrainCarriage,selectedDepartureInfoTrain);
        tv_ArrivalSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChoseArrival = true;
                LoadRecyclerViewCarriage(rv_TrainCarriage,selectedArrivalInfoTrain);
            }
        });
        tv_DepartureSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChoseArrival = false;
                LoadRecyclerViewCarriage(rv_TrainCarriage,selectedDepartureInfoTrain);
            }
        });
        List<String> data = generateGridDataSleeper(3);
        TrainSelectionSeatGridAdapter gridAdapter = new TrainSelectionSeatGridAdapter(getContext(), data, tv_DepartureSelection.getText().toString());
        gridAdapter.setOnItemSeatClickListener(SeatSelectionFragment.this);
        gridView.setAdapter(gridAdapter);

        btnChooseService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReturn){
                    String[] departureInfo = tv_DepartureSelection.getText().toString().split(" - ");
                    String[] arrivalInfo = tv_ArrivalSelection.getText().toString().split(" - ");
                    if (departureInfo.length>3 && arrivalInfo.length>3){
                        //move to service
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, new ServiceSelectionFragment(tv_DepartureSelection.getText().toString(),
                                tv_ArrivalSelection.getText().toString(),true));
                        fragmentTransaction.commit();
                    } else{
                        Toast.makeText(getContext(),"Please choose seat for departure and arrival journey",Toast.LENGTH_LONG).show();
                    }
                } else{
                    String[] departureInfo = tv_DepartureSelection.getText().toString().split(" - ");
                    if (departureInfo.length>3){
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, new ServiceSelectionFragment(tv_DepartureSelection.getText().toString(),
                                "",false));
                        fragmentTransaction.commit();
                    }else{
                        Toast.makeText(getContext(),"Please choose seat for departure",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return view;
    }
    private List<String> generateGridDataACSoftSeat(int carriageNumber){
        List<String> data = new ArrayList<>();
        char[] cols = {'A', 'B', 'C', 'D'};
        for (char col : cols) {
            if (carriageNumber == 6){
                for (int row = 7; row <= 12; row++) {
                    data.add(String.format("%c%d", col, row));
                }
            } else if (carriageNumber == 7){
                for (int row = 13; row <= 18; row++) {
                    data.add(String.format("%c%d", col, row));
                }
            } else{
                for (int row = 19; row <= 24; row++) {
                    data.add(String.format("%c%d", col, row));
                }
            }
        }
        String[][] originalMatrix = convertListToMatrix(data, 4, 6);
        String[][] rotatedMatrix = rotateMatrix180Horizontal(rotateMatrixLeft(originalMatrix));
        List<String> finalList = convertMatrixToList(rotatedMatrix);
        return finalList;
    }
    private List<String> generateGridDataSleeper(int carriageNumber){
        List<String> data = new ArrayList<>();
        char[] cols = {'A', 'B', 'C', 'D'};
        for (char col : cols) {
            if (carriageNumber == 3){
                for (int row = 1; row <= 2; row++) {
                    data.add(String.format("%c%d", col, row));
                }
            } else if (carriageNumber == 4){
                for (int row = 3; row <= 4; row++) {
                    data.add(String.format("%c%d", col, row));
                }
            } else{
                for (int row = 5; row <= 6; row++) {
                    data.add(String.format("%c%d", col, row));
                }
            }
        }
        String[][] originalMatrix = convertListToMatrix(data, 4, 2);
        String[][] rotatedMatrix = rotateMatrix180Horizontal(rotateMatrixLeft(originalMatrix));
        List<String> finalList = convertMatrixToList(rotatedMatrix);
        return finalList;
        //return data;
    }
    public void LoadRecyclerViewCarriage(RecyclerView rv_TrainCarriage, String selectedInfoTrain){
        LinearLayoutManager layoutManager = new LinearLayoutManager(SeatSelectionFragment.this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_TrainCarriage.setLayoutManager(layoutManager);
        DatabaseReference trainCarriageRefs = FirebaseDatabase.getInstance().getReference("trainCarriages");
        trainCarriageRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    List<TrainCarriage> trainCarriageList = new ArrayList<>();
                    for (DataSnapshot trainCarriageSnapshot : snapshot.getChildren()) {
                        TrainCarriage trainCarriage = trainCarriageSnapshot.getValue(TrainCarriage.class);
                        trainCarriageList.add(trainCarriage);
                    }
                    TrainCarriageAdapter adapter = new TrainCarriageAdapter(SeatSelectionFragment.this.getContext(),trainCarriageList,selectedInfoTrain);
                    adapter.setOnItemClickListener(SeatSelectionFragment.this);
                    rv_TrainCarriage.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onCarriageItemClick(String seatType, int carriageNumber, String price) {
        if (isChoseArrival){
            if (carriageNumber>=3 && carriageNumber<=5){
                List<String> data = generateGridDataSleeper(carriageNumber);
                TrainSelectionSeatGridAdapter gridAdapter = new TrainSelectionSeatGridAdapter(getContext(), data, tv_ArrivalSelection.getText().toString());
                gridAdapter.setOnItemSeatClickListener(SeatSelectionFragment.this);
                gridView.setAdapter(gridAdapter);
            } else{
                List<String> data = generateGridDataACSoftSeat(carriageNumber);
                TrainSelectionSeatGridAdapter gridAdapter = new TrainSelectionSeatGridAdapter(getContext(), data, tv_ArrivalSelection.getText().toString());
                gridAdapter.setOnItemSeatClickListener(SeatSelectionFragment.this);
                gridView.setAdapter(gridAdapter);
            }
        } else {
            if (carriageNumber>=3 && carriageNumber<=5){
                List<String> data = generateGridDataSleeper(carriageNumber);
                TrainSelectionSeatGridAdapter gridAdapter = new TrainSelectionSeatGridAdapter(getContext(), data, tv_DepartureSelection.getText().toString());
                gridAdapter.setOnItemSeatClickListener(SeatSelectionFragment.this);
                gridView.setAdapter(gridAdapter);
            } else{
                List<String> data = generateGridDataACSoftSeat(carriageNumber);
                TrainSelectionSeatGridAdapter gridAdapter = new TrainSelectionSeatGridAdapter(getContext(), data, tv_DepartureSelection.getText().toString());
                gridAdapter.setOnItemSeatClickListener(SeatSelectionFragment.this);
                gridView.setAdapter(gridAdapter);
            }
        }
        DatabaseReference seatTypeRef = FirebaseDatabase.getInstance().getReference("seatTypes").child(seatType);
        seatTypeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_CoachInfo.setText("Coach " + String.valueOf(carriageNumber) + " - " +snapshot.child("name").getValue(String.class) + " - " + price);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onSeatItemClick(String seatNumber) {
        try {
            if (isChoseArrival){
                String[] coachInfo = tv_CoachInfo.getText().toString().split(" - ");
                String[] infoTrainBooked = tv_ArrivalSelection.getText().toString().split(" - ");
                tv_ArrivalSelection.setText(infoTrainBooked[0] + " - " + infoTrainBooked[1] + " - " + infoTrainBooked[2]
                        + " - " + coachInfo[0] + " - " + coachInfo[1] + " - " + seatNumber  + " - " + coachInfo[2]);
            } else {
                String[] coachInfo = tv_CoachInfo.getText().toString().split(" - ");
                String[] infoTrainBooked = tv_DepartureSelection.getText().toString().split(" - ");
                tv_DepartureSelection.setText(infoTrainBooked[0] + " - " + infoTrainBooked[1] + " - " + infoTrainBooked[2]
                        + " - " + coachInfo[0] + " - " + coachInfo[1] + " - " + seatNumber  + " - " + coachInfo[2]);
            }
        } catch (Exception e){
            Toast.makeText(getContext(),"Please choose coach first",Toast.LENGTH_LONG).show();
        }
    }
    private static String[][] convertListToMatrix(List<String> list, int rows, int cols) {
        String[][] matrix = new String[rows][cols];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (index < list.size()) {
                    matrix[i][j] = list.get(index++);
                } else {
                    matrix[i][j] = "";
                }
            }
        }
        return matrix;
    }
    private static String[][] rotateMatrixLeft(String[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        String[][] rotatedMatrix = new String[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotatedMatrix[j][rows - 1 - i] = matrix[i][j];
            }
        }
        return rotatedMatrix;
    }
    private static String[][] rotateMatrix180Horizontal(String[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        String[][] rotatedMatrix = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotatedMatrix[i][cols - 1 - j] = matrix[i][j];
            }
        }
        return rotatedMatrix;
    }
    private static List<String> convertMatrixToList(String[][] matrix) {
        List<String> list = new ArrayList<>();
        for (String[] row : matrix) {
            for (String value : row) {
                list.add(value);
            }
        }
        return list;
    }
}
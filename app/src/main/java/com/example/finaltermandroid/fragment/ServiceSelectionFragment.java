package com.example.finaltermandroid.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.adapter.TrainBookingAdapter;
import com.example.finaltermandroid.adapter.TrainServiceAdapter;
import com.example.finaltermandroid.model.TrainSchedule;
import com.example.finaltermandroid.model.TrainService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ServiceSelectionFragment extends Fragment implements TrainServiceAdapter.OnButtonClickListener{
    RecyclerView rv_service;
    LinearLayout ll_arrival;
    TextView tv_DepartureSelection,tv_ArrivalSelection,tv_CustomerCurrent, tv_CustomerTotal;
    Button btn_chooseArrival, btn_chooseDeparture, btnCustomer;
    private boolean isReturn;
    private boolean isChoseArrival = false;
    private String selectedDepartureInfoTrain,selectedArrivalInfoTrain;
    private String selectedDepartureStationSchedule,selectedArrivalStationSchedule;
    private String backSelectedDepartureInfoTrain, backSelectedArrivalInfoTrain;
    private int numberOfCustomer;
    private int currentCustomer;
    private int totalMoney;
    public ServiceSelectionFragment(String selectedDepartureInfoTrain, String selectedArrivalInfoTrain, boolean isReturn, String selectedDepartureStationSchedule,
                                    String selectedArrivalStationSchedule,
                                    String backSelectedDepartureInfoTrain, String backSelectedArrivalInfoTrain, int currentCustomer, int numberOfCustomer, int totalMoney){
        this.selectedDepartureInfoTrain =selectedDepartureInfoTrain;
        this.selectedArrivalInfoTrain = selectedArrivalInfoTrain;
        this.isReturn = isReturn;
        this.selectedDepartureStationSchedule = selectedDepartureStationSchedule;
        this.selectedArrivalStationSchedule = selectedArrivalStationSchedule;
        this.currentCustomer = currentCustomer;
        this.numberOfCustomer = numberOfCustomer;
        this.backSelectedDepartureInfoTrain= backSelectedDepartureInfoTrain;
        this.backSelectedArrivalInfoTrain = backSelectedArrivalInfoTrain;
        this.totalMoney = totalMoney;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_selection, container, false);
        rv_service = view.findViewById(R.id.rv_service);
        ll_arrival = view.findViewById(R.id.ll_arrival);
        tv_DepartureSelection = view.findViewById(R.id.tv_DepartureSelection);
        tv_ArrivalSelection = view.findViewById(R.id.tv_ArrivalSelection);
        btn_chooseArrival = view.findViewById(R.id.btn_chooseArrival);
        btn_chooseDeparture = view.findViewById(R.id.btn_chooseDeparture);
        btnCustomer = view.findViewById(R.id.btnCustomer);
        LoadRecyclerViewService(rv_service);

        tv_CustomerCurrent = view.findViewById(R.id.tv_CustomerCurrent);
        tv_CustomerTotal = view.findViewById(R.id.tv_CustomerTotal);
        tv_CustomerCurrent.setText(String.valueOf(currentCustomer));
        tv_CustomerTotal.setText(String.valueOf(numberOfCustomer));

        if (isReturn){
            ll_arrival.setVisibility(View.VISIBLE);
            String[] selectedArrivalInfoTrainArray = selectedArrivalInfoTrain.split(" - ");
            String selectedArrivalNewInfo = "- Train Number: "+selectedArrivalInfoTrainArray[0] + "\n"
                    + "- Departure Date: " +selectedArrivalInfoTrainArray[2] + "\n"
                    + "- Departure Time: " + selectedArrivalInfoTrainArray[1] + "\n"
                    + "- Coach: " + selectedArrivalInfoTrainArray[3] + ": " + selectedArrivalInfoTrainArray[4]  + "\n"
                    + "- Seat Number: " + selectedArrivalInfoTrainArray[5]  + "\n"
                    + "- Seat Price: " + selectedArrivalInfoTrainArray[6] + "\n";
            tv_ArrivalSelection.setText(selectedArrivalNewInfo);
            String[] selectedDepartureInfoTrainArray = selectedDepartureInfoTrain.split(" - ");
            String selectedDepartureNewInfo = "Train Number: "+selectedDepartureInfoTrainArray[0] + "\n"
                    + "- Departure Date: " +selectedDepartureInfoTrainArray[2] + "\n"
                    + "- Departure Time: " + selectedDepartureInfoTrainArray[1] + "\n"
                    + "- Coach: " + selectedDepartureInfoTrainArray[3] + ": " + selectedDepartureInfoTrainArray[4] + "\n"
                    + "- Seat Number: " + selectedDepartureInfoTrainArray[5] + "\n"
                    + "- Seat Price: " + selectedDepartureInfoTrainArray[6] + "\n";
            tv_ArrivalSelection.setText(selectedArrivalNewInfo);
            tv_DepartureSelection.setText(selectedDepartureNewInfo);
        } else{
            ll_arrival.setVisibility(View.GONE);
            String[] selectedDepartureInfoTrainArray = selectedDepartureInfoTrain.split(" - ");
            String selectedDepartureNewInfo = "- Train Number: "+selectedDepartureInfoTrainArray[0] + "\n"
                    + "- Departure Date: " +selectedDepartureInfoTrainArray[2] + "\n"
                    + "- Departure Time: " + selectedDepartureInfoTrainArray[1] + "\n"
                    + "- Coach: " + selectedDepartureInfoTrainArray[3] + ": " + selectedDepartureInfoTrainArray[4] + "\n"
                    + "- Seat Number: " + selectedDepartureInfoTrainArray[5] + "\n"
                    + "- Seat Price: " + selectedDepartureInfoTrainArray[6] + "\n";
            tv_DepartureSelection.setText(selectedDepartureNewInfo);
        }
        btn_chooseDeparture.setBackgroundColor(Color.parseColor("#FF6319"));
        btn_chooseDeparture.setText("choosing");
        btn_chooseDeparture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_chooseDeparture.setBackgroundColor(Color.parseColor("#FF6319"));
                btn_chooseDeparture.setText("choosing");
                btn_chooseArrival.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btn_chooseArrival.setText("choose");
                isChoseArrival = false;
            }
        });
        btn_chooseArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_chooseArrival.setBackgroundColor(Color.parseColor("#FF6319"));
                btn_chooseArrival.setText("choosing");
                btn_chooseDeparture.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btn_chooseDeparture.setText("choose");
                isChoseArrival = true;
            }
        });
        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to new fragment
                if (isReturn){
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new CustomerSelectionFragment(tv_DepartureSelection.getText().toString(),
                            tv_ArrivalSelection.getText().toString(),true,selectedDepartureStationSchedule,selectedArrivalStationSchedule,
                            backSelectedDepartureInfoTrain, backSelectedArrivalInfoTrain, currentCustomer, numberOfCustomer, totalMoney));
                    fragmentTransaction.commit();
                } else {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new CustomerSelectionFragment(tv_DepartureSelection.getText().toString(),
                            "",false,selectedDepartureStationSchedule,"",
                            backSelectedDepartureInfoTrain, backSelectedArrivalInfoTrain, currentCustomer, numberOfCustomer,totalMoney
                            ));
                    fragmentTransaction.commit();
                }
            }
        });
        return view;
    }
    public void LoadRecyclerViewService(RecyclerView rv_service){
        rv_service.setLayoutManager(new LinearLayoutManager(getContext()));
        DatabaseReference serviceRefs = FirebaseDatabase.getInstance().getReference("service");
        serviceRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TrainService> trainServiceList = new ArrayList<>();
                for (DataSnapshot serviceSnapshot: snapshot.getChildren()){
                    TrainService trainService = serviceSnapshot.getValue(TrainService.class);
                    trainServiceList.add(trainService);
                }
                TrainServiceAdapter adapter = new TrainServiceAdapter(ServiceSelectionFragment.this.getContext(),trainServiceList);
                rv_service.setAdapter(adapter);
                adapter.setOnButtonClickListener(ServiceSelectionFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onButtonClick(String serviceName, long price) {
        if (isChoseArrival){
            String[] selectedArrivalInfoTrainArray = selectedArrivalInfoTrain.split(" - ");
            String selectedArrivalNewInfo = "- Train Number: "+selectedArrivalInfoTrainArray[0] + "\n"
                    + "- Departure Date: " +selectedArrivalInfoTrainArray[2] + "\n"
                    + "- Departure Time: " + selectedArrivalInfoTrainArray[1] + "\n"
                    + "- Coach: " + selectedArrivalInfoTrainArray[3] + ": " + selectedArrivalInfoTrainArray[4]  + "\n"
                    + "- Seat Number: " + selectedArrivalInfoTrainArray[5]  + "\n"
                    + "- Seat Price: " + selectedArrivalInfoTrainArray[6] + "\n"
                    + "- Service: " + serviceName + "\n"
                    + "- Service Price: " + String.valueOf(price/1000) + "k";
            tv_ArrivalSelection.setText(selectedArrivalNewInfo);
        } else{
            String[] selectedDepartureInfoTrainArray = selectedDepartureInfoTrain.split(" - ");
            String selectedDepartureNewInfo = "- Train Number: "+selectedDepartureInfoTrainArray[0] + "\n"
                    + "- Departure Date: " +selectedDepartureInfoTrainArray[2] + "\n"
                    + "- Departure Time: " + selectedDepartureInfoTrainArray[1] + "\n"
                    + "- Coach: " + selectedDepartureInfoTrainArray[3] + ": " + selectedDepartureInfoTrainArray[4] + "\n"
                    + "- Seat Number: " + selectedDepartureInfoTrainArray[5] + "\n"
                    + "- Seat Price: " + selectedDepartureInfoTrainArray[6] + "\n"
                    + "- Service: " + serviceName + "\n"
                    + "- Service Price: " + String.valueOf(price/1000) + "k";
            tv_DepartureSelection.setText(selectedDepartureNewInfo);
        }
    }
}

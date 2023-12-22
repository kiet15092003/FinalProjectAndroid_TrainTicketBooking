package com.example.finaltermandroid.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.fragment.HomeFragment;
import com.example.finaltermandroid.fragment.ProfileFragment;
import com.example.finaltermandroid.fragment.SeatSelectionFragment;

public class PaymentSuccessDialog extends DialogFragment {
    Button okButton;
    TextView tv_TextView1, tv_TextView2;
    private String selectedDepartureInfoTrain;
    private String selectedArrivalInfoTrain;
    private boolean isReturn;
    private String selectedDepartureStationSchedule;
    private String selectedArrivalStationSchedule;
    private int currentCustomer;
    private int numberOfCustomer;
    public PaymentSuccessDialog(String selectedDepartureInfoTrain, String selectedArrivalInfoTrain, boolean isReturn,
                                String selectedDepartureStationSchedule, String selectedArrivalStationSchedule,
                                int currentCustomer, int numberOfCustomer){
        this.selectedArrivalInfoTrain = selectedArrivalInfoTrain;
        this.selectedDepartureInfoTrain = selectedDepartureInfoTrain;
        this.isReturn = isReturn;
        this.selectedDepartureStationSchedule = selectedDepartureStationSchedule;
        this.selectedArrivalStationSchedule = selectedArrivalStationSchedule;
        this.currentCustomer = currentCustomer;
        this.numberOfCustomer = numberOfCustomer;
    }
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_payment_success, container, false);
        okButton = view.findViewById(R.id.okButton);
        tv_TextView1 = view.findViewById(R.id.tv_TextView1);
        tv_TextView2 = view.findViewById(R.id.tv_TextView2);
        if (currentCustomer == numberOfCustomer){
            tv_TextView1.setText("Your all tickets are booked and paid successfully");
            tv_TextView2.setText("Please touch OK to back to home");
        } else {
            tv_TextView1.setText("The ticket for passenger " + String.valueOf(currentCustomer) + " / " + String.valueOf(numberOfCustomer) + " is booked and paid successfully");
            tv_TextView2.setText("Please touch OK to book for the next passenger");
        }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentCustomer == numberOfCustomer){
                    //Back to home fragment
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
                    fragmentTransaction.commit();
                } else {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new SeatSelectionFragment(
                            selectedDepartureInfoTrain,selectedArrivalInfoTrain,isReturn,selectedDepartureStationSchedule,selectedArrivalStationSchedule,currentCustomer+1,numberOfCustomer
                    ));
                    fragmentTransaction.commit();
                }
            }
        });
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Adjust the width of the dialog window
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}

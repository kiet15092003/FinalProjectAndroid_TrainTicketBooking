package com.example.finaltermandroid.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finaltermandroid.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CustomerSelectionFragment extends Fragment {

    private String selectedDepartureInfoTrain,selectedArrivalInfoTrain;
    private boolean isReturn;
    private boolean checkLengthFalse = false;
    TextView tv_DepartureSelection, tv_ArrivalSelection;
    TextInputLayout nameInputLayout,phoneInputLayout,addressInputLayout;
    TextInputEditText addressEditText, phoneEditText, nameEditText;
    LinearLayout ll_arrival;
    public CustomerSelectionFragment(String selectedDepartureInfoTrain, String selectedArrivalInfoTrain, boolean isReturn){
        this.selectedArrivalInfoTrain = selectedArrivalInfoTrain;
        this.selectedDepartureInfoTrain = selectedDepartureInfoTrain;
        this.isReturn = isReturn;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_selection, container, false);
        tv_DepartureSelection = view.findViewById(R.id.tv_DepartureSelection);
        tv_ArrivalSelection = view.findViewById(R.id.tv_ArrivalSelection);
        nameInputLayout = view.findViewById(R.id.nameInputLayout);
        phoneInputLayout = view.findViewById(R.id.phoneInputLayout);
        addressInputLayout = view.findViewById(R.id.addressInputLayout);
        addressEditText = view.findViewById(R.id.addressEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        ll_arrival = view.findViewById(R.id.ll_arrival);

        LoadEditTextAndInputLayout(nameInputLayout,nameEditText);
        LoadEditTextAndInputLayout(phoneInputLayout,phoneEditText);
        LoadEditTextAndInputLayout(addressInputLayout,addressEditText);
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        if (isReturn){
            ll_arrival.setVisibility(View.VISIBLE);
            tv_ArrivalSelection.setText(selectedArrivalInfoTrain);
            tv_DepartureSelection.setText(selectedDepartureInfoTrain);
        } else{
            ll_arrival.setVisibility(View.GONE);
            tv_DepartureSelection.setText(selectedDepartureInfoTrain);
        }
        return view;
    }
    public void LoadEditTextAndInputLayout(TextInputLayout textInputLayout, TextInputEditText textInputEditText){
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputEditText.setText("");
            }
        });
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > textInputLayout.getCounterMaxLength()){
                    textInputLayout.setError("Max character length is " + textInputLayout.getCounterMaxLength());
                    checkLengthFalse = true;
                }
                else{
                    checkLengthFalse = false;
                    textInputLayout.setError(null);
                }
            }
        });
    }
}

package com.example.finaltermandroid.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.adapter.SpinnerDiscountAdapter;
import com.example.finaltermandroid.adapter.SpinnerPaymentMethodsAdapter;
import com.example.finaltermandroid.adapter.TrainStationAdapter;
import com.example.finaltermandroid.dialog.EditPhoneDialog;
import com.example.finaltermandroid.dialog.PaymentSuccessDialog;
import com.example.finaltermandroid.model.Account;
import com.example.finaltermandroid.model.Customer;
import com.example.finaltermandroid.model.Discount;
import com.example.finaltermandroid.model.SeatBooked;
import com.example.finaltermandroid.model.Ticket;
import com.example.finaltermandroid.model.TrainStation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerSelectionFragment extends Fragment {

    private String selectedDepartureInfoTrain,selectedArrivalInfoTrain;
    private boolean isReturn;
    private boolean checkLengthFalse = false;
    TextView tv_DepartureSelection, tv_ArrivalSelection;
    TextInputLayout nameInputLayout,phoneInputLayout,addressInputLayout;
    TextInputEditText addressEditText, phoneEditText, nameEditText;
    LinearLayout ll_arrival;
    Spinner PaymentMethodsSpinner,DiscountSpinner;
    Button btnPayment;
    EditText edtTotalMoney;
    private double selectedDiscount = -1;
    private String paymentOption = "";
    private int totalMoneyValue;
    private String discountKey = "";
    private String selectedDepartureStationSchedule,selectedArrivalStationSchedule;
    public CustomerSelectionFragment(String selectedDepartureInfoTrain, String selectedArrivalInfoTrain, boolean isReturn, String selectedDepartureStationSchedule ,String selectedArrivalStationSchedule){
        this.selectedArrivalInfoTrain = selectedArrivalInfoTrain;
        this.selectedDepartureInfoTrain = selectedDepartureInfoTrain;
        this.isReturn = isReturn;
        this.selectedDepartureStationSchedule = selectedDepartureStationSchedule;
        this.selectedArrivalStationSchedule = selectedArrivalStationSchedule;
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
        PaymentMethodsSpinner = view.findViewById(R.id.PaymentMethodsSpinner);
        DiscountSpinner = view.findViewById(R.id.DiscountSpinner);
        edtTotalMoney = view.findViewById(R.id.edtTotalMoney);
        btnPayment = view.findViewById(R.id.btnPayment);

        edtTotalMoney.setEnabled(false);
        LoadEditTextAndInputLayout(nameInputLayout,nameEditText);
        LoadEditTextAndInputLayout(phoneInputLayout,phoneEditText);
        LoadEditTextAndInputLayout(addressInputLayout,addressEditText);
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        if (isReturn){
            ll_arrival.setVisibility(View.VISIBLE);
            tv_ArrivalSelection.setText(selectedArrivalInfoTrain);
            tv_DepartureSelection.setText(selectedDepartureInfoTrain);
            double seatPriceArrival = extractNumericValue(selectedArrivalInfoTrain, "Seat Price: (.+)");
            double servicePriceArrival = extractNumericValue(selectedArrivalInfoTrain, "Service Price: (.+)");
            double seatPriceDeparture = extractNumericValue(selectedDepartureInfoTrain, "Seat Price: (.+)");
            double servicePriceDeparture = extractNumericValue(selectedDepartureInfoTrain, "Service Price: (.+)");
            totalMoneyValue = (int)(seatPriceArrival + servicePriceArrival + seatPriceDeparture + servicePriceDeparture);
            edtTotalMoney.setText(String.valueOf(totalMoneyValue) + "k");
        } else{
            ll_arrival.setVisibility(View.GONE);
            tv_DepartureSelection.setText(selectedDepartureInfoTrain);
            double seatPrice = extractNumericValue(selectedDepartureInfoTrain, "Seat Price: (.+)");
            double servicePrice = extractNumericValue(selectedDepartureInfoTrain, "Service Price: (.+)");
            totalMoneyValue = (int)(seatPrice + servicePrice);
            edtTotalMoney.setText(String.valueOf(totalMoneyValue) + "k");
        }
        SetAdapterDiscountSpinner(DiscountSpinner);
        SetAdapterPaymentMethodsSpinner(PaymentMethodsSpinner);
        DiscountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Discount selectedDiscountValue = (Discount) parent.getItemAtPosition(position);
                selectedDiscount = selectedDiscountValue.getDiscountValue();
                discountKey = selectedDiscountValue.getDiscountKey();
                int totalMoneyAfterSale = (int)(totalMoneyValue - totalMoneyValue*selectedDiscount);
                edtTotalMoney.setText(String.valueOf(totalMoneyAfterSale) + "k");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        PaymentMethodsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String paymentOptionValue = (String) parent.getItemAtPosition(position);
                paymentOption = paymentOptionValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String address = addressEditText.getText().toString();
                if (name.isEmpty() || phone.isEmpty() || address.isEmpty()){
                    Toast.makeText(getContext(),"Please enter all of customer information",Toast.LENGTH_LONG).show();
                } else {
                    if (checkLengthFalse){
                        Toast.makeText(getContext(),"Please check the length of your input for customer information",Toast.LENGTH_LONG).show();
                    } else if (paymentOption.equals("")){
                        Toast.makeText(getContext(),"Please choose the payment option",Toast.LENGTH_LONG).show();
                    } else {
                        //Show dialog and save to firebase
                        showPaymentSuccessDialog();
                        //Save customer info
                        DatabaseReference customerRefs = FirebaseDatabase.getInstance().getReference().child("customer");
                        customerRefs.orderByChild("phoneNumber").equalTo(phone).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String i="a";
                                if (snapshot.exists()){
                                    i="b";
                                    for (DataSnapshot customerSnapShot: snapshot.getChildren()){
                                        String customerId = customerSnapShot.getKey();
                                        Toast.makeText(getContext(),customerId,Toast.LENGTH_LONG).show();
                                        ProcessDataSaveNewSeat(customerId,tv_DepartureSelection.getText().toString(),selectedDepartureStationSchedule,selectedDepartureInfoTrain);
                                        if (isReturn){
                                            ProcessDataSaveNewSeat(customerId,tv_ArrivalSelection.getText().toString(),selectedArrivalStationSchedule,selectedArrivalInfoTrain);
                                        }
                                    }
                                } else {
                                    String customerId = customerRefs.push().getKey();
                                    ProcessDataSaveNewCustomer(i,customerId,address,name,phone);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //Delete discount
                        if (!discountKey.equals("")){
                            DatabaseReference discountReference = FirebaseDatabase.getInstance().getReference("discount");
                            discountReference.child(discountKey).removeValue();
                        }
                    }
                }
                btnPayment.setEnabled(false);
            }
        });
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
    public void SetAdapterDiscountSpinner(Spinner spinner){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            DatabaseReference discountRefs = FirebaseDatabase.getInstance().getReference().child("discount");
            discountRefs.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Discount> discountList = new ArrayList<>();
                    Discount chooseDiscount = new Discount();
                    chooseDiscount.setDiscountKey("Choose discount");
                    chooseDiscount.setDiscountValue(0);
                    discountList.add(0, chooseDiscount);
                    for (DataSnapshot discountSnapshot : snapshot.getChildren()) {
                        if (discountSnapshot.child("accountEmail").getValue(String.class).toString().equals(email)){
                            Discount discount = discountSnapshot.getValue(Discount.class);
                            discountList.add(discount);
                        }
                        SpinnerDiscountAdapter spinnerDiscountAdapter = new SpinnerDiscountAdapter(getContext(), discountList);
                        spinner.setAdapter(spinnerDiscountAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    public void SetAdapterPaymentMethodsSpinner(Spinner spinner){
        List<String> paymentOptions = Arrays.asList("Pay by PayPal", "Pay by International debit card", "Pay upon arrival at the station");
        SpinnerPaymentMethodsAdapter spinnerPaymentMethodsAdapter = new SpinnerPaymentMethodsAdapter(getContext(), paymentOptions);
        spinner.setAdapter(spinnerPaymentMethodsAdapter);
    }
    private static double extractNumericValue(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String value = matcher.group(1).trim();
            return Double.parseDouble(value.substring(0, value.length() - 1));
        } else {
            return 0.0;
        }
    }
    public void showPaymentSuccessDialog() {
        PaymentSuccessDialog paymentSuccessDialog = new PaymentSuccessDialog();
        paymentSuccessDialog.show(getChildFragmentManager(), "PaymentSuccessDialogFragment");
    }
    private Map<String, String> extractInformation(String inputString) {
        String[] selectedDepartureInfoTrainArray = inputString.split("- ");
        Map<String, String> departureInfoMap = new HashMap<>();
        for (String info : selectedDepartureInfoTrainArray) {
            String[] keyValue = info.split(": ");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                departureInfoMap.put(key, value);
            }
        }
        return departureInfoMap;
    }
    private void ProcessData(String customerId, String discountKey, String seatBookedId, String serviceId, long totalMoney){
        Ticket newTicket = new Ticket(customerId,discountKey,seatBookedId,serviceId,totalMoney);
        DatabaseReference ticketRefs = FirebaseDatabase.getInstance().getReference().child("ticket");
        String ticketId = ticketRefs.push().getKey();
        ticketRefs.child(ticketId).setValue(newTicket);
    }
    private void ProcessDataSaveNewSeat(String customerId, String departureSelection, String selectedStationSchedule, String selectedInfoTrain){
        Map<String, String> departureInfoMap = extractInformation(departureSelection);
        String departureTime = departureInfoMap.get("Departure Time");
        long departureSeatPrice = Long.parseLong(departureInfoMap.get("Seat Price").substring(0, departureInfoMap.get("Seat Price").length() - 1))*1000;
        String departureSeatNumber = departureInfoMap.get("Seat Number");
        String departureTrainNumber = departureInfoMap.get("Train Number");
        DatabaseReference trainScheduleRefs = FirebaseDatabase.getInstance().getReference().child("trainSchedule");
        trainScheduleRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot trainScheduleSnapshot: snapshot.getChildren()){
                    String departureTimeValue = trainScheduleSnapshot.child("departureTime").getValue(String.class);
                    String trainNumberValue = trainScheduleSnapshot.child("trainNumber").getValue(String.class);
                    String stationScheduleValue = trainScheduleSnapshot.child("stationSchedule").getValue(String.class);
                    if (departureTimeValue.equals(departureTime) && trainNumberValue.equals(departureTrainNumber)
                            && stationScheduleValue.equals(selectedStationSchedule)){
                        Toast.makeText(getContext(),trainScheduleSnapshot.getKey(),Toast.LENGTH_LONG).show();
                        //Save new seatBooked
                        String trainScheduleId = trainScheduleSnapshot.getKey();
                        SeatBooked newSeatBooked = new SeatBooked(trainScheduleId,departureSeatNumber,departureSeatPrice);
                        DatabaseReference seatRefs = FirebaseDatabase.getInstance().getReference().child("seatsBooked");
                        String seatId = seatRefs.push().getKey();
                        seatRefs.child(seatId).setValue(newSeatBooked);
                        //Save new ticket
                        ////Get totalMoney
                        long seatPrice = (long) extractNumericValue(selectedInfoTrain, "Seat Price: (.+)")*1000;
                        long servicePrice = (long) extractNumericValue(selectedInfoTrain, "Service Price: (.+)")*1000;
                        long totalMoney = (long) ((long) (seatPrice+servicePrice) - (seatPrice+servicePrice)*selectedDiscount);
                        ////Get serviceId
                        if (servicePrice==0){
                            ProcessData(customerId,discountKey,seatId,"Not service",totalMoney);
                        } else {
                            DatabaseReference serviceRefs = FirebaseDatabase.getInstance().getReference().child("service");
                            serviceRefs.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                        if (dataSnapshot.child("price").getValue(Long.class)==servicePrice){
                                            String serviceId = dataSnapshot.getKey();
                                            ProcessData(customerId,discountKey,seatId,serviceId,totalMoney);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void ProcessDataSaveNewCustomer(String i,String customerId,String address,String name, String phone){
        Customer newCustomer = new Customer(address,name, phone);
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("customer");
        customerRef.child(customerId).setValue(newCustomer);
        ProcessDataSaveNewSeat(customerId,tv_DepartureSelection.getText().toString(),selectedDepartureStationSchedule,selectedDepartureInfoTrain);
        if (isReturn){
            ProcessDataSaveNewSeat(customerId,tv_ArrivalSelection.getText().toString(),selectedArrivalStationSchedule,selectedArrivalInfoTrain);
        }
        //Toast.makeText(getContext(),i,Toast.LENGTH_LONG).show();
    }
}

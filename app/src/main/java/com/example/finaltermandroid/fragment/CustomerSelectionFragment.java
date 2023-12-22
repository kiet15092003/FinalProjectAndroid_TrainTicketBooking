package com.example.finaltermandroid.fragment;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

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
import com.example.finaltermandroid.model.Notification;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerSelectionFragment extends Fragment {

    private String selectedDepartureInfoTrain,selectedArrivalInfoTrain;
    private boolean isReturn;
    private boolean checkLengthFalse = false;
    TextView tv_DepartureSelection, tv_ArrivalSelection,tv_CustomerCurrent, tv_CustomerTotal;
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
    private String backSelectedDepartureInfoTrain, backSelectedArrivalInfoTrain;
    private int numberOfCustomer;
    private int currentCustomer;
    public CustomerSelectionFragment(String selectedDepartureInfoTrain, String selectedArrivalInfoTrain, boolean isReturn, String selectedDepartureStationSchedule ,String selectedArrivalStationSchedule,
                                     String backSelectedDepartureInfoTrain, String backSelectedArrivalInfoTrain,
                                     int currentCustomer, int numberOfCustomer){
        this.selectedArrivalInfoTrain = selectedArrivalInfoTrain;
        this.selectedDepartureInfoTrain = selectedDepartureInfoTrain;
        this.isReturn = isReturn;
        this.selectedDepartureStationSchedule = selectedDepartureStationSchedule;
        this.selectedArrivalStationSchedule = selectedArrivalStationSchedule;
        this.currentCustomer = currentCustomer;
        this.numberOfCustomer = numberOfCustomer;
        this.backSelectedDepartureInfoTrain= backSelectedDepartureInfoTrain;
        this.backSelectedArrivalInfoTrain = backSelectedArrivalInfoTrain;
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

        tv_CustomerCurrent = view.findViewById(R.id.tv_CustomerCurrent);
        tv_CustomerTotal = view.findViewById(R.id.tv_CustomerTotal);
        tv_CustomerCurrent.setText(String.valueOf(currentCustomer));
        tv_CustomerTotal.setText(String.valueOf(numberOfCustomer));

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
                        ProcessMoveToSave(phone, selectedDiscount, discountKey,address,name);
                        //Delete discount
                        if (!discountKey.equals("Choose discount")){
                            DatabaseReference discountReference = FirebaseDatabase.getInstance().getReference("discount");
                            discountReference.child(discountKey).child("status").setValue(false);
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
                        if (discountSnapshot.child("accountEmail").getValue(String.class).toString().equals(email)
                            && discountSnapshot.child("status").getValue(Boolean.class)==true){
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
        PaymentSuccessDialog paymentSuccessDialog = new PaymentSuccessDialog(
                backSelectedDepartureInfoTrain, backSelectedArrivalInfoTrain,isReturn,
                selectedDepartureStationSchedule,selectedArrivalStationSchedule,
                currentCustomer, numberOfCustomer);
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
    private void ProcessMoveToSave(String phone, double selectedDiscount, String discountKey, String address, String name){
        DatabaseReference customerRefs = FirebaseDatabase.getInstance().getReference().child("customer");
        customerRefs.orderByChild("phoneNumber").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String i="a";
                if (snapshot.exists()){
                    for (DataSnapshot customerSnapShot: snapshot.getChildren()){
                        String customerId = customerSnapShot.getKey();
                        ProcessDataSaveNewSeat(selectedDiscount, discountKey,customerId,tv_DepartureSelection.getText().toString(),selectedDepartureStationSchedule,selectedDepartureInfoTrain);
                        if (isReturn){
                            ProcessDataSaveNewSeat(selectedDiscount,discountKey,customerId,tv_ArrivalSelection.getText().toString(),selectedArrivalStationSchedule,selectedArrivalInfoTrain);
                        }
                    }
                } else {
                    i = "c";
                    String customerId = customerRefs.push().getKey();
                    ProcessDataSaveNewCustomer(i,selectedDiscount,discountKey,customerId,address,name,phone);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void ProcessData(String customerId, double selectedDiscount, String discountKey, String seatBookedId, String serviceId, long totalMoney,String departureSelection){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Ticket newTicket = new Ticket(customerId,discountKey,seatBookedId,serviceId,totalMoney,currentUser.getEmail());
        DatabaseReference ticketRefs = FirebaseDatabase.getInstance().getReference().child("ticket");
        String ticketId = ticketRefs.push().getKey();
        ticketRefs.child(ticketId).setValue(newTicket);

        //Save new notification
        String message1 = "You have successfully booked the ticket with code " + ticketId;
        ProcessSaveNewNotification(message1);
        //Save next notification
        Map<String, String> departureInfoMap = extractInformation(departureSelection);
        String departureDate = departureInfoMap.get("Departure Date");
        LocalDate departureDates = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate currentDates = LocalDate.now();
        long daysDifference = ChronoUnit.DAYS.between(currentDates, departureDates);
        if (daysDifference==0){
            String message3 = "You have the journey leaving today, please focus your ticket " + ticketId;
            ProcessSaveNewNotification(message3);
        } else{
            String message3 = "You have the journey leaving in " + String.valueOf(daysDifference) + " days, please focus your ticket " + ticketId;
            ProcessSaveNewNotification(message3);
        }
    }
    private void ProcessDataSaveNewSeat(double selectedDiscount, String discountKey, String customerId, String departureSelection, String selectedStationSchedule, String selectedInfoTrain){
        Map<String, String> departureInfoMap = extractInformation(departureSelection);
        String departureTime = departureInfoMap.get("Departure Time");
        long departureSeatPrice = Long.parseLong(departureInfoMap.get("Seat Price").substring(0, departureInfoMap.get("Seat Price").length() - 1))*1000;
        //set account new point
        DatabaseReference accountRefs = FirebaseDatabase.getInstance().getReference().child("accounts");
        accountRefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String emailUser = currentUser.getEmail();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    if (snapshot1.child("email").getValue(String.class).equals(emailUser)){
                        accountRefs.child(snapshot1.getKey()).child("point").setValue(
                                snapshot1.child("point").getValue(Integer.class) + (int)(departureSeatPrice/7070));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                            ProcessData(customerId,selectedDiscount,discountKey,seatId,"Not service",totalMoney,departureSelection);
                        } else {
                            DatabaseReference serviceRefs = FirebaseDatabase.getInstance().getReference().child("service");
                            serviceRefs.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                        if (dataSnapshot.child("price").getValue(Long.class)==servicePrice){
                                            String serviceId = dataSnapshot.getKey();
                                            ProcessData(customerId,selectedDiscount,discountKey,seatId,serviceId,totalMoney,departureSelection);
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
    private void ProcessDataSaveNewCustomer(String i,double selectedDiscount, String discountKey, String customerId,String address,String name, String phone){
        Customer newCustomer = new Customer(address,name, phone);
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("customer");
        customerRef.child(customerId).setValue(newCustomer);
        ProcessDataSaveNewSeat(selectedDiscount,discountKey,customerId,tv_DepartureSelection.getText().toString(),selectedDepartureStationSchedule,selectedDepartureInfoTrain);
        if (isReturn){
            ProcessDataSaveNewSeat(selectedDiscount,discountKey,customerId,tv_ArrivalSelection.getText().toString(),selectedArrivalStationSchedule,selectedArrivalInfoTrain);
        }
    }
    private void ProcessSaveNewNotification(String message){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String accountEmail = currentUser.getEmail();
        Date currentTime = new Date();
        String dateFormat = getCurrentDateTime();
        Notification notification = new Notification(accountEmail,message,dateFormat);
        DatabaseReference notificationRefs = FirebaseDatabase.getInstance().getReference().child("notification");
        String notificationId = notificationRefs.push().getKey();
        notificationRefs.child(notificationId).setValue(notification);
    }
    public static String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(currentDate);
    }
}

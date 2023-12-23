package com.example.finaltermandroid.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.adapter.SpinnerPaymentMethodsAdapter;
import com.example.finaltermandroid.fragment.HomeFragment;
import com.example.finaltermandroid.fragment.ProfileFragment;
import com.example.finaltermandroid.fragment.SeatSelectionFragment;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class PaymentSuccessDialog extends DialogFragment {
    Button okButton;
    TextView tv_TextView1, tv_TextView2, tv_TextView3;
    private String selectedDepartureInfoTrain;
    private String selectedArrivalInfoTrain;
    private boolean isReturn;
    private String selectedDepartureStationSchedule;
    private String selectedArrivalStationSchedule;
    private int currentCustomer;
    private int numberOfCustomer;
    private int totalMoneyBack, totalMoneyNow;
    private String paymentOption;
    Spinner PaymentMethodsSpinner;
    String clientId = "AevdsYooqR_vsm4Ogj79kRW7H6iji8f-jBrPYsKQnv1ZU0wWHcwVkInqyttYWLYr-xoyxTROU7DB4pBT";
    int PAYPAL_REQUEST_CODE = 123;
    public PaymentSuccessDialog(String selectedDepartureInfoTrain, String selectedArrivalInfoTrain, boolean isReturn,
                                String selectedDepartureStationSchedule, String selectedArrivalStationSchedule,
                                int currentCustomer, int numberOfCustomer, int totalMoneyBack,int totalMoneyNow){
        this.selectedArrivalInfoTrain = selectedArrivalInfoTrain;
        this.selectedDepartureInfoTrain = selectedDepartureInfoTrain;
        this.isReturn = isReturn;
        this.selectedDepartureStationSchedule = selectedDepartureStationSchedule;
        this.selectedArrivalStationSchedule = selectedArrivalStationSchedule;
        this.currentCustomer = currentCustomer;
        this.numberOfCustomer = numberOfCustomer;
        this.totalMoneyBack = totalMoneyBack;
        this.totalMoneyNow = totalMoneyNow;
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
        tv_TextView3 = view.findViewById(R.id.tv_TextView3);
        PaymentMethodsSpinner = view.findViewById(R.id.PaymentMethodsSpinner);
        SetAdapterPaymentMethodsSpinner(PaymentMethodsSpinner);

        tv_TextView3.setText(String.valueOf(totalMoneyNow+totalMoneyBack) + "k");
        if (currentCustomer == numberOfCustomer){
            tv_TextView1.setText("Your all tickets are booked successfully");
            tv_TextView2.setText("Please touch OK to pay all your money and back to home");
        } else {
            tv_TextView1.setText("The ticket for passenger " + String.valueOf(currentCustomer) + " / " + String.valueOf(numberOfCustomer) + " is booked successfully");
            tv_TextView2.setText("Please touch OK to book for the next passenger");
        }
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
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentCustomer == numberOfCustomer){
                    //Back to home fragment
                    if (paymentOption.equals("Pay by PayPal")){
                        PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                                .clientId(clientId);
                        String totalPayment = tv_TextView3.getText().toString().substring(0,  tv_TextView3.getText().toString().length() - 1);
                        String totalPaymentStr = String.valueOf((int)(Double.parseDouble(totalPayment)/24.25));
                        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(totalPaymentStr)), "USD", "Confirm your payment", PayPalPayment.PAYMENT_INTENT_SALE);
                        Intent intent = new Intent(getContext(), PaymentActivity.class);
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
                    }
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
                    fragmentTransaction.commit();
                } else {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new SeatSelectionFragment(
                            selectedDepartureInfoTrain,selectedArrivalInfoTrain,isReturn,selectedDepartureStationSchedule,selectedArrivalStationSchedule,currentCustomer+1,numberOfCustomer,
                            totalMoneyNow+totalMoneyBack
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
    public void SetAdapterPaymentMethodsSpinner(Spinner spinner){
        List<String> paymentOptions = Arrays.asList("Pay by PayPal",  "Pay upon arrival at the station");
        SpinnerPaymentMethodsAdapter spinnerPaymentMethodsAdapter = new SpinnerPaymentMethodsAdapter(getContext(), paymentOptions);
        spinner.setAdapter(spinnerPaymentMethodsAdapter);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Handle successful payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("paymentExample", confirm.toJSONObject().toString(4));
                        // Add the following lines to return to your app
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "success"); // You can pass any data back to your app
                        getActivity().setResult(Activity.RESULT_OK, returnIntent);
                        getActivity().finish(); // Finish the current activity or fragment
                    } catch (JSONException e) {
                        Log.e("paymentExample", "JSON exception occurred: " + e.getMessage());
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid payment was submitted. Please see the docs.");
            }
        }
    }
}

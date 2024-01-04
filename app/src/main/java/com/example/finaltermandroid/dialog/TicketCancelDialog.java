package com.example.finaltermandroid.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.finaltermandroid.R;
import com.example.finaltermandroid.activity.LoginActivity;
import com.example.finaltermandroid.fragment.HomeFragment;
import com.example.finaltermandroid.fragment.ProfileFragment;
import com.example.finaltermandroid.model.Notification;
import com.example.finaltermandroid.model.Ticket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TicketCancelDialog extends DialogFragment {
    private TextInputEditText edtPassword,edtCardNumber,edtCVC,edtMMYY;
    private TextInputLayout passwordInputLayout,cardNumberInputLayout,CVCInputLayout,MMYYInputLayout;
    private Button okButton,cancelButton;
    private EditText edtRefundedAmount;
    boolean checkLengthFalse = false;
    private Ticket ticket;
    public TicketCancelDialog(Ticket ticket) {
        this.ticket = ticket;
    }
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_ticket_cancel, container, false);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtCardNumber = view.findViewById(R.id.edtCardNumber);
        edtCVC = view.findViewById(R.id.edtCVC);
        edtMMYY = view.findViewById(R.id.edtMMYY);
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
        cardNumberInputLayout = view.findViewById(R.id.cardNumberInputLayout);
        CVCInputLayout = view.findViewById(R.id.CVCInputLayout);
        MMYYInputLayout = view.findViewById(R.id.MMYYInputLayout);
        okButton = view.findViewById(R.id.okButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        edtRefundedAmount = view.findViewById(R.id.edtRefundedAmount);
        LoadEditTextAndInputLayout(passwordInputLayout,edtPassword);
        LoadEditTextAndInputLayout(cardNumberInputLayout,edtCardNumber);
        LoadEditTextAndInputLayout(CVCInputLayout,edtCVC);
        LoadEditTextAndInputLayout(MMYYInputLayout,edtMMYY);
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtCardNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtCVC.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtMMYY.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtRefundedAmount.setText(String.valueOf(ticket.getTotalMoney() * 0.6) + " VND");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLengthFalse){
                    Toast.makeText(getContext(),"Please check your length of input",Toast.LENGTH_LONG).show();
                } else{
                    if (edtPassword.getText().toString().isEmpty() || edtCVC.getText().toString().isEmpty()
                            || edtMMYY.getText().toString().isEmpty() || edtCardNumber.getText().toString().isEmpty()){
                        Toast.makeText(getContext(),"Please enter all information",Toast.LENGTH_LONG).show();
                    } else {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String email = currentUser.getEmail();
                            DatabaseReference accountRefs = FirebaseDatabase.getInstance().getReference().child("accounts");
                            accountRefs.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot accountSnapshot : snapshot.getChildren()) {
                                        String accountId =  accountSnapshot.getKey();
                                        DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference().child("accounts").child(accountId);
                                        accountRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    String password = snapshot.child("password").getValue(String.class);

                                                    if (!password.equals(edtPassword.getText().toString())){
                                                        Toast.makeText(getContext(),"Your password is incorrect",Toast.LENGTH_LONG).show();
                                                    } else {
                                                        //Delete seatBooked
                                                        String seatBookedCancelId = ticket.getSeatBookedId();
                                                        //Delete ticket
                                                        processRemoveTicket(seatBookedCancelId);
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
    private boolean isPasswordVisible(int inputType) {
        int variation = inputType & InputType.TYPE_MASK_VARIATION;
        return variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Adjust the width of the dialog window
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public void LoadEditTextAndInputLayout(TextInputLayout textInputLayout, TextInputEditText textInputEditText){
        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible(textInputEditText.getInputType())){
                    textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else{
                    textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
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
                    checkLengthFalse = true;
                    textInputLayout.setError("Max character length is " + textInputLayout.getCounterMaxLength());
                }
                else{
                    checkLengthFalse = false;
                    textInputLayout.setError(null);
                }
            }
        });
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
    public void processRemoveTicket(String seatBookedId){
        try{
            DatabaseReference ticketRefs = FirebaseDatabase.getInstance().getReference().child("ticket");
            ticketRefs.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        if (dataSnapshot.child("seatBookedId").getValue(String.class).equals(seatBookedId)){
                            //Save new notification
                            String message1 = "Your ticket with code " + dataSnapshot.getKey() +  " has been cancelled";
                            ProcessSaveNewNotification(message1);
                            //remove ticket
                            ticketRefs.child(dataSnapshot.getKey()).removeValue();
                            //Delete seatsBooked
                            processRemoveSeat(seatBookedId);
                            //redirect home
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
                            fragmentTransaction.commit();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
            fragmentTransaction.commit();
        }

    }
    public void processRemoveSeat(String seatBookedId){

        DatabaseReference seatRefs = FirebaseDatabase.getInstance().getReference().child("seatsBooked");
        seatRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if (dataSnapshot.getKey().equals(seatBookedId)){
                        seatRefs.child(dataSnapshot.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

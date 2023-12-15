package com.example.finaltermandroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.dialog.EditPhoneDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    TextView tvName, tv_phoneNumberValue,tv_emailValue,tv_passwordValue;
    ImageView iv_editPhoneNumber,iv_editEmail,iv_editPassword;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tv_phoneNumberValue  = (TextView) view.findViewById(R.id.tv_phoneNumberValue);
        tv_emailValue = (TextView) view.findViewById(R.id.tv_emailValue);
        tv_passwordValue = (TextView) view.findViewById(R.id.tv_passwordValue);
        iv_editPhoneNumber = (ImageView) view.findViewById(R.id.iv_editPhoneNumber);
        iv_editEmail = (ImageView) view.findViewById(R.id.iv_editEmail);
        iv_editPassword = (ImageView) view.findViewById(R.id.iv_editPassword);
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

                        // Call a method to load data
                        DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference().child("accounts").child(accountId);
                        accountRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    String name = snapshot.child("name").getValue(String.class);
                                    String email = snapshot.child("email").getValue(String.class);
                                    String imgPath = snapshot.child("imgPath").getValue(String.class);
                                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                                    String password = snapshot.child("password").getValue(String.class);
                                    tvName.setText(name);
                                    tv_emailValue.setText(email);
                                    tv_phoneNumberValue.setText(phoneNumber);
                                    String passwordNotShow = "*";
                                    for (int i=1;i<password.length();i++){
                                        passwordNotShow += "*";
                                    }
                                    tv_passwordValue.setText(passwordNotShow);
                                    iv_editPhoneNumber.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showEditPhoneDialog(phoneNumber);
                                        }
                                    });
                                    iv_editEmail.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                                    iv_editPassword.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return view;
    }

    public void showEditPhoneDialog(String phoneNumberDefault) {
        EditPhoneDialog customDialogFragment = new EditPhoneDialog(phoneNumberDefault);
        customDialogFragment.show(getChildFragmentManager(), "CustomDialogFragment");
    }

    public void onDialogEditPhoneResult(String result) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            DatabaseReference accountRefs = FirebaseDatabase.getInstance().getReference().child("accounts");
            accountRefs.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot accountSnapshot : snapshot.getChildren()) {
                        String accountId =  accountSnapshot.getKey();
                        // Call a method to load data
                        DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference().child("accounts").child(accountId);
                        accountRef.child("phoneNumber").setValue(result);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        tv_phoneNumberValue.setText(result);
    }
}

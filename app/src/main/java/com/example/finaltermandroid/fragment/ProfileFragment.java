package com.example.finaltermandroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.finaltermandroid.MainActivity;
import com.example.finaltermandroid.R;
import com.example.finaltermandroid.activity.LoginActivity;
import com.example.finaltermandroid.dialog.EditPasswordDialog;
import com.example.finaltermandroid.dialog.EditPhoneDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    TextView tvName, tv_phoneNumberValue,tv_emailValue,tv_passwordValue;
    ImageView iv_editPhoneNumber,iv_editPassword;
    MaterialButton btnLogout;
    private String oldPasswordValue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tv_phoneNumberValue  = (TextView) view.findViewById(R.id.tv_phoneNumberValue);
        tv_emailValue = (TextView) view.findViewById(R.id.tv_emailValue);
        tv_passwordValue = (TextView) view.findViewById(R.id.tv_passwordValue);
        iv_editPhoneNumber = (ImageView) view.findViewById(R.id.iv_editPhoneNumber);
        iv_editPassword = (ImageView) view.findViewById(R.id.iv_editPassword);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
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
                                    oldPasswordValue = password;

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
                                    iv_editPassword.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showEditPasswordDialog();
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
        customDialogFragment.show(getChildFragmentManager(), "EditPhoneDialogFragment");
    }

    public void onDialogEditPhoneResult(String result) {
        if (result.length()!=10){
            Toast.makeText(getContext(),"Your phone must be contain 10 digits",Toast.LENGTH_LONG).show();
        } else{
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
                            Toast.makeText(getContext(), "Update phone number successful", Toast.LENGTH_SHORT).show();
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
    public void showEditPasswordDialog() {
        EditPasswordDialog editPasswordDialog = new EditPasswordDialog();
        editPasswordDialog.show(getChildFragmentManager(), "EditPasswordDialogFragment");
    }

    public void onDialogEditPasswordResult(String oldPassword,String newPassword) {
        if (!oldPassword.equals(oldPasswordValue)){
            Toast.makeText(getContext(),"Your old password is incorrect",Toast.LENGTH_LONG).show();
        } else{
            try{
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    //Change password in database
                    String email = currentUser.getEmail();
                    DatabaseReference accountRefs = FirebaseDatabase.getInstance().getReference().child("accounts");
                    accountRefs.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot accountSnapshot : snapshot.getChildren()) {
                                String accountId =  accountSnapshot.getKey();
                                // Call a method to load data
                                DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference().child("accounts").child(accountId);
                                accountRef.child("password").setValue(newPassword);
                                // Change password in firebase authentication
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user!=null){
                                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(),"Update password successful",Toast.LENGTH_LONG).show();
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                                startActivity(intent);
                                            } else {
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                tv_passwordValue.setText(newPassword);
            } catch (Exception e){
                FirebaseAuth.getInstance().signOut();
            }
        }
    }
}

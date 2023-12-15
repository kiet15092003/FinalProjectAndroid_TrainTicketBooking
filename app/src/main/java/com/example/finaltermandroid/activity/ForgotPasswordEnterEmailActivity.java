package com.example.finaltermandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.dialog.EditPasswordDialog;
import com.example.finaltermandroid.dialog.ForgotPasswordDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordEnterEmailActivity extends AppCompatActivity {
    TextInputLayout emailInputLayout;
    TextInputEditText emailEditText;
    MaterialButton btnNext;
    TextView tvLogin;
    private boolean checkLengthFalse = false;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_enter_email);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        emailEditText = findViewById(R.id.emailEditText);
        btnNext = findViewById(R.id.btnNext);
        tvLogin = findViewById(R.id.tvLogin);

        LoadEditTextAndInputLayout(emailInputLayout,emailEditText);
        firebaseAuth = FirebaseAuth.getInstance();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                if (email.length()==0){
                    Toast.makeText(ForgotPasswordEnterEmailActivity.this,"Please enter your email",Toast.LENGTH_LONG).show();
                } else if (checkLengthFalse){
                    Toast.makeText(ForgotPasswordEnterEmailActivity.this,"Please check your email length",Toast.LENGTH_LONG).show();
                } else {
                    //Check the email is exist or not
                    DatabaseReference accountRefs = FirebaseDatabase.getInstance().getReference().child("accounts");
                    accountRefs.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                // Send to email and move to next activity
                                firebaseAuth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    showForgotPasswordDialog();
                                                    //Show dialog back to login
                                                } else {
                                                    // If the user does not exist or other error occurs
                                                    Toast.makeText(ForgotPasswordEnterEmailActivity.this,"Your email does not exist",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            } else{
                                Toast.makeText(ForgotPasswordEnterEmailActivity.this,"No one has registered with this email account",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordEnterEmailActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
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
    private void showForgotPasswordDialog() {
        ForgotPasswordDialog forgotPasswordDialog = new ForgotPasswordDialog(emailEditText.getText().toString());
        forgotPasswordDialog.show(getSupportFragmentManager(), "ForgotPasswordDialogFragmentTag");
    }
}

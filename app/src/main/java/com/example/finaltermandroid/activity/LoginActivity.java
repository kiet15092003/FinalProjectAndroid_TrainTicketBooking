package com.example.finaltermandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finaltermandroid.MainActivity;
import com.example.finaltermandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout LoginNameInputLayout,LoginPasswordInputLayout;
    TextInputEditText LoginNameEditText,LoginPasswordText;
    MaterialButton btnLogin;
    TextView tvRegister;
    private FirebaseAuth auth;
    private boolean checkLengthFalse = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginNameEditText = (TextInputEditText) findViewById(R.id.LoginNameEditText);
        LoginNameInputLayout = (TextInputLayout) findViewById(R.id.LoginNameInputLayout);
        LoginPasswordText = (TextInputEditText) findViewById(R.id.LoginPasswordEditText);
        LoginPasswordInputLayout = (TextInputLayout) findViewById(R.id.LoginPasswordInputLayout);
        btnLogin = (MaterialButton) findViewById(R.id.btnLogin);
        auth = FirebaseAuth.getInstance();
        LoadEditTextAndInputLayout(LoginNameInputLayout,LoginNameEditText);
        LoadEditTextAndInputLayout(LoginPasswordInputLayout,LoginPasswordText);
        LoginNameEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        LoginPasswordText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);



        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = LoginNameEditText.getText().toString();
                String password = LoginPasswordText.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkLengthFalse){
                        Toast.makeText(LoginActivity.this, "Please check your email or password length", Toast.LENGTH_SHORT).show();
                    } else{
                        signIn(username, password);
                    }
                }
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
                    textInputLayout.setError(null);
                }
            }
        });
    }
    private void signIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // move to Main
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

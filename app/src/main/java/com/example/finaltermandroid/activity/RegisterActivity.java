package com.example.finaltermandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finaltermandroid.MainActivity;
import com.example.finaltermandroid.R;
import com.example.finaltermandroid.model.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private boolean checkLengthFalse = false;

    TextInputLayout nameInputLayout,phoneInputLayout,emailInputLayout,passwordInputLayout,confirmPasswordInputLayout;
    TextInputEditText nameEditText,phoneEditText,emailEditText,passwordEditText,confirmPasswordEditText;
    CheckBox cbAccept;
    Button btnSignUp;
    TextView tvLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = (TextInputEditText) findViewById(R.id.nameEditText);
        nameInputLayout = (TextInputLayout) findViewById(R.id.nameInputLayout);
        phoneEditText = (TextInputEditText) findViewById(R.id.phoneEditText);
        phoneInputLayout = (TextInputLayout) findViewById(R.id.phoneInputLayout);
        emailEditText = (TextInputEditText) findViewById(R.id.emailEditText);
        emailInputLayout = (TextInputLayout) findViewById(R.id.emailInputLayout);
        passwordEditText = (TextInputEditText) findViewById(R.id.passwordEditText);
        passwordInputLayout = (TextInputLayout) findViewById(R.id.passwordInputLayout);
        confirmPasswordEditText = (TextInputEditText) findViewById(R.id.confirmPasswordEditText);
        confirmPasswordInputLayout = (TextInputLayout) findViewById(R.id.confirmPasswordInputLayout);
        cbAccept = (CheckBox) findViewById(R.id.cbAccept);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        tvLogin = (TextView) findViewById(R.id.tvLogin);

        LoadEditTextAndInputLayout(nameInputLayout, nameEditText);
        LoadEditTextAndInputLayout(phoneInputLayout, phoneEditText);
        LoadEditTextAndInputLayout(emailInputLayout, emailEditText);
        LoadEditTextAndInputLayout(passwordInputLayout, passwordEditText);
        LoadEditTextAndInputLayout(confirmPasswordInputLayout, confirmPasswordEditText);

        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        phoneEditText.setInputType(InputType.TYPE_CLASS_PHONE);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPass = confirmPasswordEditText.getText().toString();

                if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPass.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"Please enter all information",Toast.LENGTH_LONG).show();
                } else if (checkLengthFalse){
                    Toast.makeText(RegisterActivity.this,"Please check your max length of input",Toast.LENGTH_LONG).show();
                } else if (!email.contains("@")){
                    Toast.makeText(RegisterActivity.this,"Please check your email address format",Toast.LENGTH_LONG).show();
                } else if (phone.length()!=10){
                    Toast.makeText(RegisterActivity.this,"Please enter your phone length is 10",Toast.LENGTH_LONG).show();
                } else if (password.length()<6 && password.length()>0){
                    Toast.makeText(RegisterActivity.this,"Please enter your password length more than 5",Toast.LENGTH_LONG).show();
                } else if (!password.equals(confirmPass)){
                    Toast.makeText(RegisterActivity.this,"Please confirm your password",Toast.LENGTH_LONG).show();
                } else if (!cbAccept.isChecked()){
                    Toast.makeText(RegisterActivity.this,"Please accept term of service and privacy policy",Toast.LENGTH_LONG).show();
                } else{
                    DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference().child("accounts");
                    accountRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                Toast.makeText(RegisterActivity.this,"Your email address already exist",Toast.LENGTH_LONG).show();
                            } else{
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,
                                        new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                Account newAccount = new Account(name, phone, email, password, "imgPath");
                                                DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference().child("accounts");
                                                String userId = accountRef.push().getKey();
                                                accountRef.child(userId).setValue(newAccount);
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
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
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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
}

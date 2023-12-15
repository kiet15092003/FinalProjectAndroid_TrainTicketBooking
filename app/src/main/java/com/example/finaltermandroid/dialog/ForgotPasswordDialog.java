package com.example.finaltermandroid.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.finaltermandroid.MainActivity;
import com.example.finaltermandroid.R;
import com.example.finaltermandroid.activity.LoginActivity;
import com.example.finaltermandroid.fragment.ProfileFragment;

public class ForgotPasswordDialog extends DialogFragment {
    private Button okButton,cancelButton;
    private TextView tvEmailCheck;
    private String emailAddress;
    public ForgotPasswordDialog(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_forgot_password, container, false);
        okButton = view.findViewById(R.id.okButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        tvEmailCheck = view.findViewById(R.id.tvEmailCheck);
        tvEmailCheck.setText("Now, check your email address " + emailAddress + " to create your new password");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                dismiss();
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
}

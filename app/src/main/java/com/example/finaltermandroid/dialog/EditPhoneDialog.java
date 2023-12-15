package com.example.finaltermandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentActivity;

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.fragment.ProfileFragment;
import com.google.android.material.textfield.TextInputEditText;

public class EditPhoneDialog extends DialogFragment {
    private EditText editText;
    private Button okButton,cancelButton;
    private String phoneNumberDefault;
    public EditPhoneDialog(String phoneNumberDefault) {
        this.phoneNumberDefault = phoneNumberDefault;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_phone_number, container, false);
        editText = view.findViewById(R.id.edtPhoneNumber);
        editText.setText(phoneNumberDefault);
        okButton = view.findViewById(R.id.okButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredText = editText.getText().toString();
                Fragment parentFragment = getParentFragment();
                ((ProfileFragment) parentFragment).onDialogEditPhoneResult(enteredText);
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

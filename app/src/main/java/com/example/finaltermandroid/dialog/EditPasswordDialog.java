package com.example.finaltermandroid.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

import com.example.finaltermandroid.R;
import com.example.finaltermandroid.fragment.ProfileFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditPasswordDialog extends DialogFragment {
    private TextInputEditText edtOldPassword,edtNewPassword,edtConfirmPassword;
    private TextInputLayout oldPasswordInputLayout,newPasswordInputLayout,confirmPasswordInputLayout;
    private Button okButton,cancelButton;
    public EditPasswordDialog() {

    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_password, container, false);
        edtOldPassword = view.findViewById(R.id.edtOldPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        oldPasswordInputLayout = view.findViewById(R.id.oldPasswordInputLayout);
        newPasswordInputLayout = view.findViewById(R.id.newPasswordInputLayout);
        confirmPasswordInputLayout = view.findViewById(R.id.confirmPasswordInputLayout);

        LoadEditTextAndInputLayout(oldPasswordInputLayout,edtOldPassword);
        LoadEditTextAndInputLayout(newPasswordInputLayout,edtNewPassword);
        LoadEditTextAndInputLayout(confirmPasswordInputLayout,edtConfirmPassword);
        edtOldPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        okButton = view.findViewById(R.id.okButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = edtOldPassword.getText().toString();
                String newPassword = edtNewPassword.getText().toString();
                String confirmPassword = edtConfirmPassword.getText().toString();
                if (newPassword.length()<6 || newPassword.length()>12){
                    Toast.makeText(getContext(), "Please check your length of new Password", Toast.LENGTH_SHORT).show();
                } else if(!newPassword.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Please check your confirm password", Toast.LENGTH_SHORT).show();
                } else{
                    Fragment parentFragment = getParentFragment();
                    ((ProfileFragment) parentFragment).onDialogEditPasswordResult(oldPassword,newPassword);
                    dismiss();
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
                    textInputLayout.setError("Max character length is " + textInputLayout.getCounterMaxLength());
                }
                else{
                    textInputLayout.setError(null);
                }
            }
        });
    }
}

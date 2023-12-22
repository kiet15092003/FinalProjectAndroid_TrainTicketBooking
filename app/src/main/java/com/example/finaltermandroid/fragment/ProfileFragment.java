package com.example.finaltermandroid.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.finaltermandroid.MainActivity;
import com.example.finaltermandroid.R;
import com.example.finaltermandroid.activity.LoginActivity;
import com.example.finaltermandroid.dialog.EditPasswordDialog;
import com.example.finaltermandroid.dialog.EditPhoneDialog;
import com.github.dhaval2404.imagepicker.ImagePicker;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {
    TextView tvName, tv_phoneNumberValue,tv_emailValue,tv_passwordValue;
    ImageView iv_editPhoneNumber,iv_editPassword, iv_img;
    MaterialButton btnLogout;
    private String oldPasswordValue;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            setProfilePic(getContext(), selectedImageUri, iv_img);
                            uploadImageToFirebaseStorage(selectedImageUri);
                        }
                    }
                });

    }
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
        iv_img = (ImageView) view.findViewById(R.id.iv_img);
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
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imgPath);
                                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        Glide.with(requireContext())
                                                .load(uri)
                                                .apply(RequestOptions.circleCropTransform())
                                                .into(iv_img);
                                    }).addOnFailureListener(exception -> {
                                        exception.printStackTrace();
                                        Log.e("ProfileFragment", "Failed to load image from Firebase Storage: " + exception.getMessage());
                                    });

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
        iv_img.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });
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
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference imagesRef = storageRef.child("profile_images");

        String imageName = "image_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = imagesRef.child(imageName);

        // Upload file to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveImageUrlToFirebaseDatabase(imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void saveImageUrlToFirebaseDatabase(String imageUrl) {
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
                        accountRef.child("imgPath").setValue(imageUrl);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}

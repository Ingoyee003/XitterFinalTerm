package com.xetterfb.xetterchat.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.xetterfb.xetterchat.R;


import java.util.Objects;

public class ChangePassword extends AppCompatActivity {

    EditText pass,name;
    ImageView imageView3,imageView4;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        pass = findViewById(R.id.pass);
        name = findViewById(R.id.name);
        mAuth = FirebaseAuth.getInstance();
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView3.setOnClickListener(v -> onBackPressed());
        imageView4.setOnClickListener(v -> {
            String oldP = name.getText().toString().trim();
            String newP = pass.getText().toString().trim();
            if (TextUtils.isEmpty(oldP)){
                Toast.makeText(this, "Error!: Enter Your Current Password ", Toast.LENGTH_LONG).show();
                return;
            }else if (TextUtils.isEmpty(newP)){
                Toast.makeText(this, "Enter Your New Password", Toast.LENGTH_LONG).show();
                return;
            }else if (newP.length()<6){
                Toast.makeText(this, "Password should have minimum 6 characters", Toast.LENGTH_LONG).show();
                return;
            }
            updatePassword(oldP,newP);
        });
    }

    private void updatePassword(String oldP, String newP) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser).getEmail(), oldP);

        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> firebaseUser.updatePassword(newP)
                        .addOnSuccessListener(aVoid1 -> {
                            Toast.makeText(ChangePassword.this, "Password updated successfully", Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ChangePassword.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(ChangePassword.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
package com.xetterfb.xetterchat.menu;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.xetterfb.xetterchat.R;


import java.util.Objects;

public class ChangeEmail extends AppCompatActivity {

    EditText pass,name;
    ImageView imageView3,imageView4;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        name = findViewById(R.id.name);
        pass = findViewById(R.id.pass);
        mAuth = FirebaseAuth.getInstance();
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView3.setOnClickListener(v -> onBackPressed());
        imageView4.setOnClickListener(v -> {
            String newE = name.getText().toString().trim();
            String newP = pass.getText().toString().trim();
            if (TextUtils.isEmpty(newE)){
                Toast.makeText(this, "Enter your new Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(newP)){
                Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show();
                return;
            }
            Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(newE);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount()>0){
                        Toast.makeText(ChangeEmail.this, "Email already exist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateEmail(newE,newP);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void updateEmail(String newE, String newP) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(Objects.requireNonNull(firebaseUser).getEmail()), newP);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> firebaseUser.updateEmail(newE)
                        .addOnSuccessListener(aVoid1 -> {
                            Toast.makeText(ChangeEmail.this, "Email updated", Toast.LENGTH_LONG).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(ChangeEmail.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(ChangeEmail.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }
}
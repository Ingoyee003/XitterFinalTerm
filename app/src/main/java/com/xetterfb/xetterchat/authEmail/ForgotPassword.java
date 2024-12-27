package com.xetterfb.xetterchat.authEmail;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.xetterfb.xetterchat.R;

import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {
    TextView signIn,signUp;
    private EditText mEmail;
    private FirebaseAuth mAuth;
    Button button;
    ProgressBar progressBar4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        progressBar4 = findViewById(R.id.progressBar4);
        signIn = findViewById(R.id.textView5);
        signUp = findViewById(R.id.textView6);
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.email);
        button = findViewById(R.id.button5);
        signIn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignIn.class );
            startActivity(intent);
        });

        signUp.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignUp.class );
            startActivity(intent);
        });

        button.setOnClickListener(view -> {
            progressBar4.setVisibility(View.VISIBLE);
            String email = mEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)){
                Toast.makeText(this, "You Must Enter Email", Toast.LENGTH_SHORT).show();
                progressBar4.setVisibility(View.INVISIBLE);

            }else {

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Reset link is sent to your email", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(getApplicationContext(), SignIn.class);
                            startActivity(intent);
                            finish();
                            progressBar4.setVisibility(View.INVISIBLE);

                        }, 3000);

                    } else {
                        String msg = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(this, "Error!!!", Toast.LENGTH_SHORT).show();
                        progressBar4.setVisibility(View.INVISIBLE);

                    }
                });
            }
        });
    }
}

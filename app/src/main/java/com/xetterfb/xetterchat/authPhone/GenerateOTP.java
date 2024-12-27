package com.xetterfb.xetterchat.authPhone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xetterfb.xetterchat.R;

public class GenerateOTP extends AppCompatActivity {

    EditText email;
    Button button5;
    ProgressBar progressBar6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_o_t_p);
        email = findViewById(R.id.email);
        progressBar6 = findViewById(R.id.progressBar6);
        button5 = findViewById(R.id.button5);
        button5.setOnClickListener(v -> {
            progressBar6.setVisibility(View.VISIBLE);
            String mobile = email.getText().toString().trim().trim();
            if (mobile.isEmpty() || mobile.length() < 10){
                Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show();
                progressBar6.setVisibility(View.INVISIBLE);
            }else {
                @SuppressWarnings("UnnecessaryLocalVariable") String phonenumber = mobile;
                Intent intent = new Intent(GenerateOTP.this, VerifyOTP.class);
                intent.putExtra("phonenumber", phonenumber);
                startActivity(intent);
                progressBar6.setVisibility(View.INVISIBLE);
            }
        });
    }



}

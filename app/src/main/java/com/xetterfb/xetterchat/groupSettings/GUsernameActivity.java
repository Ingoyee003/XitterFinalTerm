package com.xetterfb.xetterchat.groupSettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.xetterfb.xetterchat.R;
import com.xetterfb.xetterchat.SharedPref;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"unchecked", "Convert2Lambda"})
public class GUsernameActivity extends AppCompatActivity {

    EditText mUsername;
    private DatabaseReference mDatabase;
    ImageView button,settings;
    ProgressBar progressBar8;
    String GroupId;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_username);
        button = findViewById(R.id.menu);
        button = findViewById(R.id.menu);
        mUsername = findViewById(R.id.name);
        settings = findViewById(R.id.settings);
        GroupId= EditGroup.getActivityInstance().getGroupId();
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar8 = findViewById(R.id.progressBar8);
        progressBar8.setVisibility(View.VISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(GroupId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("gUsername").getValue()).toString();
                mUsername.setText(name);
                progressBar8.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar8.setVisibility(View.GONE);
                Toast.makeText(GUsernameActivity.this, "Error!", Toast.LENGTH_SHORT).show();

            }
        });
        button.setOnClickListener(view -> {
            progressBar8.setVisibility(View.VISIBLE);
            final String username = mUsername.getText().toString();
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, "Enter User Name", Toast.LENGTH_SHORT).show();
                progressBar8.setVisibility(View.GONE);

            } else {
                Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Groups").orderByChild("gUsername").equalTo(username);
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {

                            Toast.makeText(GUsernameActivity.this, "User Name Already Exist", Toast.LENGTH_LONG).show();
                            progressBar8.setVisibility(View.GONE);

                        } else {

                            addUsername(username);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(GUsernameActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        progressBar8.setVisibility(View.GONE);
                    }
                });
            }
        });

    }

    private void addUsername(String username) {
        @SuppressWarnings("rawtypes") Map hashMap = new HashMap();
        hashMap.put("gUsername", username);
        mDatabase.updateChildren(hashMap);
        Toast.makeText(this, "User Name Updated", Toast.LENGTH_LONG).show();
        progressBar8.setVisibility(View.GONE);
    }
}
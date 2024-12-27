package com.xetterfb.xetterchat.groups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.xetterfb.xetterchat.adapter.AdapterGroupChat;
import com.xetterfb.xetterchat.groupSettings.EditGroup;
import com.xetterfb.xetterchat.model.ModelGroupChat;
import com.xetterfb.xetterchat.R;
import com.xetterfb.xetterchat.SharedPref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChat extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    private static final int PICK_VIDEO_REQUEST = 1;
    EditText textBox;
    ImageView send,back,attach,more;
    TextView mName,mUsername;
    CircleImageView circleImageView;
    String GroupId, myGroupRole;
    private FirebaseAuth mAuth;
    private ArrayList<ModelGroupChat> groupChats;
    private AdapterGroupChat adapterGroupChat;
    ConstraintLayout constraintLayout3,delete;
    BottomSheetDialog bottomSheetDialog;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private String userId;
    ConstraintLayout add,info,post,edit,leave;
    BottomSheetDialog bottomDialog;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Intent intent = getIntent();
        GroupId = intent.getStringExtra("groupId");
        send = findViewById(R.id.imageView10);
        textBox = findViewById(R.id.textBox);
        recyclerView = findViewById(R.id.chat);
        mName = findViewById(R.id.name);
        mUsername = findViewById(R.id.username);
        attach = findViewById(R.id.imageView11);
        back = findViewById(R.id.imageView9);
        back.setOnClickListener(v -> onBackPressed());
        more = findViewById(R.id.more);
        circleImageView = findViewById(R.id.circleImageView3);

        loadGroupInfo();
        loadGroupMessage();
        createBottomSheetDialog();
        loadMyGroupRole();


        attach.setOnClickListener(v -> bottomSheetDialog.show());

        send.setOnClickListener(v -> {
            String message = textBox.getText().toString().trim();
            if (TextUtils.isEmpty(message)){

            }else {
                sendMessage(message);
            }
            textBox.setText("");
        });

        more.setOnClickListener(v -> bottomDialog.show());
        createBottomDialog();
    }

    private void loadMyGroupRole() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Participants")
                .orderByChild("id").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            myGroupRole = ""+ds.child("role").getValue();
                            if(myGroupRole.equals("creator")){
                                add.setVisibility(View.VISIBLE);
                                edit.setVisibility(View.VISIBLE);
                                leave.setVisibility(View.GONE);
                                post.setVisibility(View.VISIBLE);
                            }
                            if (myGroupRole.equals("admin")){
                                add.setVisibility(View.VISIBLE);
                                edit.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void createBottomDialog() {
        if ( bottomDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.more_group_bottom_sheet, null);

            add = view.findViewById(R.id.chatshare);
            info = view.findViewById(R.id.appshare);

            post = view.findViewById(R.id.addgpost);
            edit = view.findViewById(R.id.editgroup);
            leave = view.findViewById(R.id.leave);

            add.setOnClickListener(this);
            info.setOnClickListener(this);
            post.setOnClickListener(this);
            edit.setOnClickListener(this);
            leave.setOnClickListener(this);
            add.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            bottomDialog = new BottomSheetDialog(this);
            bottomDialog.setContentView(view);


        }
    }


    private void loadGroupMessage() {
        groupChats = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Message")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        groupChats.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelGroupChat modelGroupChat = ds.getValue(ModelGroupChat.class);
                            groupChats.add(modelGroupChat);
                        }
                       adapterGroupChat = new AdapterGroupChat(GroupChat.this, groupChats);
                        recyclerView.setAdapter(adapterGroupChat);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendMessage(String message) {

        String timestamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", mAuth.getUid());
        hashMap.put("msg", message);
        hashMap.put("type", "text");
        hashMap.put("timestamp", timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(GroupId).child("Message").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(aVoid -> textBox.setText(""))
                .addOnFailureListener(e -> {
                    Toast.makeText(GroupChat.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(GroupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String name = ""+ds.child("gName").getValue();
                            String userName = ""+ds.child("gUsername").getValue();
                            String icon = ""+ds.child("gIcon").getValue();

                            mName.setText(name);
                            mUsername.setText(userName);
                            try {
                                Picasso.get().load(icon).placeholder(R.drawable.group).into(circleImageView);
                            }catch (Exception e){
                                Picasso.get().load(R.drawable.group).into(circleImageView);
                            }
                            mName.setOnClickListener(v -> {

                                Intent intent8 = new Intent(GroupChat.this, GroupProfile.class);
                                intent8.putExtra("groupId", GroupId);
                                startActivity(intent8);
                            });
                            circleImageView.setOnClickListener(v -> {

                                Intent intent8 = new Intent(GroupChat.this, GroupProfile.class);
                                intent8.putExtra("groupId", GroupId);
                                startActivity(intent8);
                            });
                            mUsername.setOnClickListener(v -> {

                                Intent intent8 = new Intent(GroupChat.this, GroupProfile.class);
                                intent8.putExtra("groupId", GroupId);
                                startActivity(intent8);
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void createBottomSheetDialog(){
        if (bottomSheetDialog == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.add_bottom_sheet, null);
            constraintLayout3 = view.findViewById(R.id.constraintLayoutPhoto);
            delete = view.findViewById(R.id.delete);
            constraintLayout3.setOnClickListener(this);
            delete.setOnClickListener(this);
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.constraintLayout3) {
            // Check Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    pickImageFromGallery();
                }
            } else {
                pickImageFromGallery();
            }
        } else if (v.getId() == R.id.delete) {
            // Check Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    chooseVideo();
                }
            } else {
                chooseVideo();
            }
        } else if (v.getId() == R.id.chatshare) {
            bottomDialog.cancel();

            Intent intent = new Intent(this, AddParticipants.class);
            intent.putExtra("groupId", GroupId);
            startActivity(intent);
        } else if (v.getId() == R.id.leave) {
            bottomDialog.cancel();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
            reference.child(GroupId).child("Participants").child(userId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Left", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else if (v.getId() == R.id.appshare) {
            bottomDialog.cancel();

            Intent intent8 = new Intent(this, GroupProfile.class);
            intent8.putExtra("groupId", GroupId);
            startActivity(intent8);
        } else if (v.getId() == R.id.addgpost) {
            bottomDialog.cancel();
            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Groups");
            ref2.child(GroupId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Group Deleted", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else if (v.getId() == R.id.editgroup) {
            bottomDialog.cancel();
            Intent intent9 = new Intent(this, EditGroup.class);
            intent9.putExtra("groupId", GroupId);
            startActivity(intent9);
        }
    }


    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }
    private String getfileExt(Uri video_uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(video_uri));
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission Allowed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Storage permission is required", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        bottomSheetDialog.cancel();
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Uri image_uri = Objects.requireNonNull(data).getData();
            sendImage(image_uri);
            bottomSheetDialog.cancel();
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri video_uri = data.getData();
            sendVideo(video_uri);
            bottomSheetDialog.cancel();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendVideo(Uri video_uri) {
        String timeStamp = ""+System.currentTimeMillis();
        String filenameAndPath = "GroupChatImages/"+"post_"+System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(video_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()){

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", mAuth.getUid());
                hashMap.put("msg", downloadUri);
                hashMap.put("type", "video");
                hashMap.put("timestamp", timeStamp);

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                ref1.child(GroupId).child("Message").child(timeStamp)
                        .setValue(hashMap)
                        .addOnSuccessListener(aVoid -> textBox.setText(""))
                        .addOnFailureListener(e -> {
                            Toast.makeText(GroupChat.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });



            }
        }).addOnFailureListener(e -> {

        });
    }

    private void sendImage(Uri image_uri) {
        String timeStamp = ""+System.currentTimeMillis();
        String filenameAndPath = "GroupChatImages/"+"post_"+System.currentTimeMillis();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filenameAndPath);
        ref.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
            if (uriTask.isSuccessful()){

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", mAuth.getUid());
                hashMap.put("msg", downloadUri);
                hashMap.put("type", "image");
                hashMap.put("timestamp", timeStamp);

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                ref1.child(GroupId).child("Message").child(timeStamp)
                        .setValue(hashMap)
                        .addOnSuccessListener(aVoid -> textBox.setText(""))
                        .addOnFailureListener(e ->
                                Toast.makeText(GroupChat.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );


            }
        }).addOnFailureListener(e -> {

        });


    }

}
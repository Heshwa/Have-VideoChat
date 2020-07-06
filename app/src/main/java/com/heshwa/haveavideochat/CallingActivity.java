package com.heshwa.haveavideochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CallingActivity extends AppCompatActivity {
    private TextView txtCallingName;
    private ImageButton imgCancel ,imgAccept;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String callingId,ringingId;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        txtCallingName = findViewById(R.id.txtCallingUser);
        imgCancel = findViewById(R.id.imgCancel);
        imgAccept = findViewById(R.id.imgAccept);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Intent intent = getIntent();
        callingId = intent.getStringExtra("CallingId");
        ringingId = intent.getStringExtra("RingingId");

        if(mAuth.getCurrentUser().getUid().equals(callingId))
        {
            imgAccept.setVisibility(View.GONE);
            userRef.child(ringingId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists() && snapshot.hasChild("Name"))
                    txtCallingName.setText(snapshot.child("Name").getValue().toString());
                    if(!snapshot.hasChild("Ringing"))
                    {
                        finish();
                        Toast.makeText(CallingActivity.this,"Call Cancelled",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CallingActivity.this,MainActivity.class);
                        startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if(mAuth.getCurrentUser().getUid().equals(ringingId))
        {
            imgAccept.setVisibility(View.VISIBLE);
            userRef.child(callingId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists() && snapshot.hasChild("Name"))
                        txtCallingName.setText(snapshot.child("Name").getValue().toString());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child(callingId).child("Calling").removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            userRef.child(ringingId).child("Ringing").removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(CallingActivity.this,"Call Cancelled",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(CallingActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });
                        }
                    }
                });


            }
        });

    }
}
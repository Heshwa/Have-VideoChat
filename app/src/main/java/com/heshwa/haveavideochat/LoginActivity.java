package com.heshwa.haveavideochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText edtEmailSignIn,edtPasswordSignIn;
    private Button btnSignIn;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtEmailSignIn = findViewById(R.id.edtEmailSignIn);
        edtPasswordSignIn = findViewById(R.id.edtPasswordSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Authentication");
        progressDialog.setMessage("Please wait until authentication finishes");
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEmailSignIn.getText().toString().equals("")|| edtPasswordSignIn.getText().toString().equals(""))
                {
                    Toast.makeText(LoginActivity.this,"All fields are needed",Toast.LENGTH_LONG).show();
                }
                else {
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(edtEmailSignIn.getText().toString(),edtPasswordSignIn.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        userRef.child(mAuth.getCurrentUser().getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.hasChild("Name"))
                                                        {
                                                            String name =(String) snapshot.child("Name").getValue();
                                                            Toast.makeText(LoginActivity.this,name+" Signed In Successfully"
                                                                    ,Toast.LENGTH_LONG).show();
                                                            progressDialog.dismiss();
                                                            Intent intent = new Intent(LoginActivity.this
                                                                    ,MainActivity.class);
                                                            startActivity(intent);
                                                            finish();

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this,task.getException().getMessage()
                                                ,Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
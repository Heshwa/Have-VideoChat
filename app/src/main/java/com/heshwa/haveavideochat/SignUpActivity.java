package com.heshwa.haveavideochat;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private TextView txtAlreadyHaveAAccount;
    private EditText edtName, edtPassword ,edtEmail;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        txtAlreadyHaveAAccount = findViewById(R.id.txtAldreadyHaveAAccount);
        edtEmail = findViewById(R.id.edtEmail);
        edtName =findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        mAuth = FirebaseAuth.getInstance();
        userRef =FirebaseDatabase.getInstance().getReference().child("Users");
        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Authentication");
        progressDialog.setMessage("Please wait until authentication finishes");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if(edtEmail.getText().toString().equals("")||edtName.getText().toString().equals("")||edtPassword.getText().toString().equals(""))
                {
                    Toast.makeText(SignUpActivity.this,"All fields are needed"
                            ,Toast.LENGTH_LONG).show();
                }
                else
                {
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        userRef.child(mAuth.getCurrentUser().getUid()).child("Name")
                                                .setValue(edtName.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(SignUpActivity.this
                                                                    , edtName.getText().toString() +" successfully signed up"
                                                                    ,Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }

                                                    }
                                                });
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignUpActivity.this,task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();

                                    }

                                }
                            });
                }


            }
        });
        txtAlreadyHaveAAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);


            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
package com.heshwa.haveavideochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerViewUserList;
    private UserAdapter mUserAdapter;
    private ArrayList<String> userList;
    private DatabaseReference userRef;
    private ProgressDialog progressDialog;
    public static ArrayList<String> userIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mRecyclerViewUserList = findViewById(R.id.recyculerViewUserList);
        mRecyclerViewUserList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        userList = new ArrayList<>();
        mUserAdapter = new UserAdapter(MainActivity.this,userList);
        mRecyclerViewUserList.setAdapter(mUserAdapter);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog= new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Getting Users");
        progressDialog.setMessage("Please wait until , getting all the users");
        userIds = new ArrayList<>();
        getUsersFromServer();






    }

    private void getUsersFromServer() {
        progressDialog.show();
        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.hasChild("Name"))
                {
                    if(!snapshot.getKey().equals(mAuth.getCurrentUser().getUid()))
                    {
                        userList.add(snapshot.child("Name").getValue().toString());
                        userIds.add(snapshot.getKey());
                        mUserAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.itemLogOut)
        {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
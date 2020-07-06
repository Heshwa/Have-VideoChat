package com.heshwa.haveavideochat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<String> names;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    public UserAdapter(Context context, ArrayList<String> names) {
        mContext = context;
        this.names = names;
    }


    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false)) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ViewHolder holder, final int position) {
        holder.txtUserName.setText(names.get(position));
        holder.imgVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef = FirebaseDatabase.getInstance().getReference().child("Users");
                mAuth = FirebaseAuth.getInstance();
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("calling",MainActivity.userIds.get(position));
                userRef.child(mAuth.getCurrentUser().getUid()).child("Calling").child("calling")
                        .setValue(MainActivity.userIds.get(position))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    userRef.child(MainActivity.userIds.get(position)).child("Ringing")
                                            .child("ringing").setValue(mAuth.getCurrentUser().getUid())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Intent intent = new Intent(mContext,CallingActivity.class);
                                                        intent.putExtra("CallingId",mAuth.getCurrentUser().getUid());
                                                        intent.putExtra("RingingId",MainActivity.userIds.get(position));
                                                        mContext.startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });


            }
        });

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUserName;
        private ImageView imgVideoCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            imgVideoCall = itemView.findViewById(R.id.imgVideoCall);
        }
    }
}


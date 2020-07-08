package com.heshwa.haveavideochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import android.opengl.GLSurfaceView;
import android.widget.ImageButton;
import android.widget.Toast;


public class VideoCallActivity extends AppCompatActivity implements  Session.SessionListener, PublisherKit.PublisherListener{
    private static String API_KEY = "46827974";
    private static String SESSION_ID = "1_MX40NjgyNzk3NH5-MTU5NDExODY2Njg2Nn4yd2oxYzB6ZWh1d3l0dVdqaXNTblMydW9-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjgyNzk3NCZzaWc9ZTNjMzk0NWI0YTIwZGExZjlkZGI1NThiNGY0NzQxYzVkOTkxNTIzYjpzZXNzaW9uX2lkPTFfTVg0ME5qZ3lOemszTkg1LU1UVTVOREV4T0RZMk5qZzJObjR5ZDJveFl6QjZaV2gxZDNsMGRWZHFhWE5UYmxNeWRXOS1mZyZjcmVhdGVfdGltZT0xNTk0MTE4NjkyJm5vbmNlPTAuMDQ3OTExNTM3NjcxMTY0NzQmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTU5NjcxMDY5MCZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;
    private Session mSession;
    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private ImageButton imgCancel;
    private DatabaseReference userRef;
    private String callingId,ringingId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        imgCancel = findViewById(R.id.imgCancelVideo);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        callingId = CallingActivity.callingId;
        ringingId=CallingActivity.ringingId;
        userRef.child(ringingId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(!snapshot.hasChild("Ringing"))
                {
                    onDisconnected(mSession);
                    Intent intent = new Intent(VideoCallActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                                                userRef.child(callingId).child("Accepted").removeValue();
                                            }

                                        }
                                    });




                                }
                            }
                        });
            }
        });
        requestPermissions();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize view objects from your layout
            mPublisherViewContainer = findViewById(R.id.publisher_container);
            mSubscriberViewContainer = findViewById(R.id.subscriber_container);

            // initialize and connect to the session
            mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(this);
            mSession.connect(TOKEN);



        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }



    @Override
    public void onConnected(Session session) {

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisherViewContainer.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);

    }

    @Override
    public void onDisconnected(Session session) {

    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        showOpenTokError(opentokError);
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        showOpenTokError(opentokError);

    }

    private void showOpenTokError(OpentokError opentokError) {

        Toast.makeText(this, opentokError.getErrorDomain().name() +": " +opentokError.getMessage() + " Please, see the logcat.", Toast.LENGTH_LONG).show();
        finish();
    }
    @Override
    protected void onPause() {


        super.onPause();

        if (mSession != null) {
            mSession.onPause();
        }
        if (isFinishing()) {
            disconnectSession();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectSession();
    }

    @Override
    protected void onResume() {


        super.onResume();

        if (mSession != null) {
            mSession.onResume();
        }
    }
    private void disconnectSession() {
        if (mSession == null) {
            return;
        }

        if (mSubscriber != null) {
            mSubscriberViewContainer.removeView(mSubscriber.getView());
            mSession.unsubscribe(mSubscriber);
            mSubscriber.destroy();
            mSubscriber = null;
        }

        if (mPublisher != null) {
            mPublisherViewContainer.removeView(mPublisher.getView());
            mSession.unpublish(mPublisher);
            mPublisher.destroy();
            mPublisher = null;
        }
        mSession.disconnect();
    }
}
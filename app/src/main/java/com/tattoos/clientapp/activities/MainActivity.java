package com.tattoos.clientapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tattoos.clientapp.MyApplicationContext;
import com.tattoos.clientapp.R;
import com.tattoos.clientapp.enums.IntentKeys;
import com.tattoos.clientapp.models.Artist;
import com.tattoos.clientapp.models.User;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    MyApplicationContext mContext;

    // Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = (MyApplicationContext) getApplicationContext();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setupGoogle();
        setupAuth();
        writeUserIfNeeded();
    }

    private void writeUserIfNeeded() {
        final String userId = mContext.getFirebaseUser().getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            Log.d("yyy","era null");
                           writeNewUser(mContext.getFirebaseUser().getDisplayName(),mContext.getFirebaseUser().getEmail());
                        }else{
                            Log.d("yyy","nao era null");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("yyy", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void setupGoogle() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    private void setupAuth() {
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            //setup username and photoURL
            mContext.setFirebaseUser(mFirebaseUser);
            mContext.setUsername(mFirebaseUser.getDisplayName());
            mContext.setEmail(mFirebaseUser.getEmail());
            if (mFirebaseUser.getPhotoUrl() != null) {
                mContext.setPhotoURL(mFirebaseUser.getPhotoUrl().toString());
            }
        }
    }

    private void writeNewUser(String username, String email) {
        User user = new User(username, email);
        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        String uid = mContext.getFirebaseUser().getUid();
        childUpdates.put("/users/" + uid, userValues);

        mDatabase.updateChildren(childUpdates);

        Log.d("yyy", "adicionei user");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void tattoosButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, ShowroomActivity.class);
        intent.putExtra(IntentKeys.SHOWROOM_TYPE.toString(), "tattoos");
        startActivity(intent);
    }

    public void artistsButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, ShowroomActivity.class);
        intent.putExtra(IntentKeys.SHOWROOM_TYPE.toString(), "artists");
        startActivity(intent);
    }

    public void yourProfileButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, ArtistProfileActivity.class);
        startActivity(intent);
    }

    public void signUpArtistButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, SignUpArtistActivity.class);
        startActivity(intent);
    }
}

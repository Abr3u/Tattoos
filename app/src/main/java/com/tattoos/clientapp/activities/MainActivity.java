package com.tattoos.clientapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.tattoos.clientapp.MyApplicationContext;
import com.tattoos.clientapp.R;
import com.tattoos.clientapp.enums.IntentKeys;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    MyApplicationContext mContext;

    // Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = (MyApplicationContext)getApplicationContext();
        setupGoogle();
        setupAuth();
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
            mContext.setUsername(mFirebaseUser.getDisplayName());
            mContext.setEmail(mFirebaseUser.getEmail());
            if (mFirebaseUser.getPhotoUrl() != null) {
                mContext.setPhotoURL(mFirebaseUser.getPhotoUrl().toString());
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void tattoosButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this,ShowroomActivity.class);
        intent.putExtra(IntentKeys.SHOWROOM_TYPE.toString(),"tattoos");
        startActivity(intent);
    }

    public void artistsButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this,ShowroomActivity.class);
        intent.putExtra(IntentKeys.SHOWROOM_TYPE.toString(),"artists");
        startActivity(intent);
    }

    public void yourProfileButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this,ArtistProfileActivity.class);
        startActivity(intent);
    }
}

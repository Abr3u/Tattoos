package com.tattoos.clientapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tattoos.clientapp.MyApplicationContext;
import com.tattoos.clientapp.R;
import com.tattoos.clientapp.adapters.PagerViewAdapter;
import com.tattoos.clientapp.enums.JSONKeys;
import com.tattoos.clientapp.location.GPSTracker;
import com.tattoos.clientapp.location.LocationParser;
import com.tattoos.clientapp.models.Artist;
import com.tattoos.clientapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ArtistProfileActivity extends AppCompatActivity {

    private GPSTracker mGPS;

    private TextView artistBio;
    private TextView artistName;
    private TextView artistLocation;
    private ViewPager artistAvatars;
    private ProgressBar mProgressBar;

    private ArrayList<String> mUrls;

    private MyApplicationContext mContext;

    private PagerViewAdapter adapterView;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_profile);

        mGPS = new GPSTracker(ArtistProfileActivity.this);
        mUrls = new ArrayList<String>();

        mProgressBar = (ProgressBar) findViewById(R.id.artistProgressBar);
        artistBio = (TextView) findViewById(R.id.UserBio);
        artistName = (TextView) findViewById(R.id.UserName);
        artistLocation = (TextView) findViewById(R.id.UserLocation);
        artistAvatars = (ViewPager) findViewById(R.id.profileViewPager);

        mContext = (MyApplicationContext) getApplicationContext();

        adapterView = new PagerViewAdapter(this,mUrls);
        artistAvatars.setAdapter(adapterView);

        fetchArtistData();
    }

    private void fetchArtistData() {
        final String userId = mContext.getFirebaseUser().getUid();
        mDatabase.child("artists").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Artist artist = dataSnapshot.getValue(Artist.class);

                        // [START_EXCLUDE]
                        if (artist == null) {
                            // User is null, error out
                            Log.e("yyy", "Artist " + userId + " is unexpectedly null");
                            Toast.makeText(ArtistProfileActivity.this,
                                    "Error: could not fetch artist.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mUrls.add(artist.avatarURL);
                            adapterView.setUrls(mUrls);
                            artistBio.setText(artist.bio);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("yyy", "getUser:onCancelled", databaseError.toException());
                    }
                });

    }

    private String getUserLocality() {
        if (mGPS.canGetLocation()) {
            JSONObject json = LocationParser.getGoogleLocationInfo(mGPS.getLatitude(), mGPS.getLongitude());
            return LocationParser.getLocalityFromGoogleJSON(json);
        }
        return "";
    }



}

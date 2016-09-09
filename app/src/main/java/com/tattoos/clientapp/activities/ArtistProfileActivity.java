package com.tattoos.clientapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tattoos.clientapp.MyApplicationContext;
import com.tattoos.clientapp.R;
import com.tattoos.clientapp.enums.JSONKeys;
import com.tattoos.clientapp.location.GPSTracker;
import com.tattoos.clientapp.location.LocationParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArtistProfileActivity extends AppCompatActivity {

    private GPSTracker mGPS;
    private TextView artistBio;
    private TextView artistName;
    private TextView artistLocation;
    private ImageView artistAvatar;

    private MyApplicationContext myApplicationContext;

    private String ARTISTS_URL = "http://192.168.1.69:9999/artists";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_profile);

        mGPS = new GPSTracker(ArtistProfileActivity.this);

        artistBio = (TextView) findViewById(R.id.UserBio);
        artistName = (TextView) findViewById(R.id.UserName);
        artistLocation = (TextView) findViewById(R.id.UserLocation);
        artistAvatar = (ImageView) findViewById(R.id.profileImageView);

        myApplicationContext = (MyApplicationContext) getApplicationContext();

        String myUrl = ARTISTS_URL + "?email=" + myApplicationContext.getEmail();

        if (myApplicationContext.getArtistAvatar() == null || myApplicationContext.getArtistName().isEmpty() ||
                myApplicationContext.getArtistBio().isEmpty()) {
            Log.d("yyy","not cached");
            new AsyncHttpTask().execute(myUrl);
        }else{
            Log.d("yyy","cached");
            //update UI based on cached info
            artistName.setText(myApplicationContext.getArtistName());
            artistBio.setText(myApplicationContext.getArtistBio());
            artistLocation.setText(myApplicationContext.getLastKnownLocation());
            artistAvatar.setImageBitmap(myApplicationContext.getArtistAvatar());
        }
    }

    private String getUserLocality() {
        if (mGPS.canGetLocation()) {
            JSONObject json = LocationParser.getGoogleLocationInfo(mGPS.getLatitude(), mGPS.getLongitude());
            return LocationParser.getLocalityFromGoogleJSON(json);
        }
        return "";
    }


    public class AsyncHttpTask extends AsyncTask<String, Void, String> {

        private String locality;

        @Override
        protected String doInBackground(String... params) {

            if (myApplicationContext.getLastKnownLocation().isEmpty()) {
                locality = getUserLocality();
                myApplicationContext.setLastKnownLocation(locality);
            }

            Request request = new Request.Builder()
                    .url(params[0])
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // Download complete. Let us update UI
            if (!result.isEmpty()) {
                updateUI(result);
                artistLocation.setText(locality);
            } else {
                Toast.makeText(ArtistProfileActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(String json) {
        try {
            JSONObject artist = new JSONObject(json);
            String name = artist.getString(JSONKeys.ARTIST_NAME.toString());
            String bio = artist.getString(JSONKeys.ARTIST_BIO.toString());
            String avatarStr = artist.getString(JSONKeys.ARTIST_AVATAR.toString());

            byte[] avatarBytes = Base64.decode(avatarStr, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);

            //cache artist info
            myApplicationContext.setArtistName(name);
            myApplicationContext.setArtistBio(bio);
            myApplicationContext.setArtistAvatar(bmp);

            //update UI
            artistName.setText(name);
            artistBio.setText(bio);
            artistAvatar.setImageBitmap(bmp);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

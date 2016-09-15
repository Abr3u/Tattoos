package com.tattoos.clientapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import com.tattoos.clientapp.adapters.GridItem;
import com.tattoos.clientapp.adapters.GridViewAdapter;
import com.tattoos.clientapp.enums.IntentKeys;
import com.tattoos.clientapp.enums.JSONKeys;
import com.tattoos.clientapp.location.GPSTracker;
import com.tattoos.clientapp.models.Artist;
import com.tattoos.clientapp.models.Tattoo;
import com.tattoos.clientapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ShowroomActivity extends AppCompatActivity {

    private final String searchTattooHint = "search by title/artist/body part/style";
    private final String searchArtistHint = "search by name/locality";

    private String showroomType;

    private TextView showroomTitle;
    private EditText searchET;

    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showroom);

        showroomType = getIntent().getStringExtra(IntentKeys.SHOWROOM_TYPE.toString());

        showroomTitle = (TextView) findViewById(R.id.showroomTitleHolder);

        searchET = (EditText) findViewById(R.id.searchET);
        if (showroomType.equals("Tattoos")) {
            searchET.setHint(searchTattooHint);
        } else {
            searchET.setHint(searchArtistHint);
        }

        searchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                mGridAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        showroomTitle.setText(showroomType);
        mGridView = (GridView) findViewById(R.id.gridview);

        mGridData = new ArrayList<>();

        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item, mGridData);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                if (item.isTattoo()) {
                    //Pass the image details to TattooDetailsActivity
                    Intent intent = new Intent(ShowroomActivity.this, TattooDetailsActivity.class);

                    intent.putExtra(IntentKeys.SHOWROOM_TYPE.toString(), showroomType);
                    intent.putExtra(IntentKeys.TATTOO_TITLE.toString(), item.getTattoo_title());
                    intent.putExtra(IntentKeys.TATTOO_URL.toString(), item.getTattoo_url());
                    intent.putExtra(IntentKeys.TATTOO_ARTIST.toString(), item.getTattoo_artist());
                    intent.putExtra(IntentKeys.TATTOO_BODY_PART.toString(), item.getTatto_body_part());
                    intent.putExtra(IntentKeys.TATTOO_STYLE.toString(), item.getTattoo_style());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ShowroomActivity.this, TattooDetailsActivity.class);
                    intent.putExtra(IntentKeys.SHOWROOM_TYPE.toString(), showroomType);
                    intent.putExtra(IntentKeys.ARTIST_LOCALITY.toString(), item.getArtist_locality());
                    intent.putExtra(IntentKeys.ARTIST_URL.toString(), item.getArtist_url());
                    intent.putExtra(IntentKeys.ARTIST_NAME.toString(), item.getArtist_name());
                    intent.putExtra(IntentKeys.ARTIST_BIO.toString(), item.getArtist_bio());
                    startActivity(intent);
                }
            }
        });

        switch (showroomType) {
            case "Tattoos":
                getTattoosFirebase();
                break;
            case "Artists":
                getArtistsFirebase();
                break;
        }
    }

    private void getArtistsFirebase() {
        mDatabase.child("artists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot artistSnapshot : snapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);

                    if (artist == null) {
                        // Artist is null, error out
                        Log.e("yyy", "Artist is unexpectedly null");
                        Toast.makeText(ShowroomActivity.this,
                                "Error: could not fetch artist.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        HashMap<String, String> tats = (HashMap) artistSnapshot.child("tattoos").getValue();

                        Map.Entry<String, String> entry = tats.entrySet().iterator().next();

                        GridItem item = new GridItem();
                        item.setIsTattoo(false);
                        item.setArtist_url(entry.getValue());
                        item.setArtist_bio(artist.bio);
                        item.setArtist_name(artist.username);
                        item.setArtist_locality(artist.locality);
                        item.setArtist_latitude(artist.latitude);
                        item.setArtist_longitude(artist.longitude);
                        mGridData.add(item);
                    }
                }
                sortArtistsByDistanceToSelf();
                mGridAdapter.setGridData(mGridData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("yyy", "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    private void sortArtistsByDistanceToSelf() {
        GPSTracker gps = new GPSTracker(this);

        if (gps.canGetLocation()) {
            double[] distances = new double[mGridData.size()];
            HashMap<Double,GridItem> distance_item = new HashMap<>(mGridData.size());

            double myLati = gps.getLatitude();
            double myLongi = gps.getLongitude();

            //store distances to my location
            int i = 0;
            for(GridItem unsorted : mGridData){
                double lati = Double.parseDouble(unsorted.getArtist_latitude());
                double longi = Double.parseDouble(unsorted.getArtist_longitude());
                double distance = Utils.distance(myLati,lati,myLongi,longi,0.0,0.0);

                distances[i] = distance;
                distance_item.put(distance,unsorted);
                i++;
            }

            //order based on distance (closer first)
            Arrays.sort(distances);

            //update GridData
            mGridData = new ArrayList<>();
            for(i=0;i<distances.length;i++){
                mGridData.add(distance_item.get(distances[i]));
                Log.d("yyy","adicionei "+distance_item.get(distances[i]).getArtist_name()+" com distancia "+distances[i]);
            }
        }
    }

    private void getTattoosFirebase() {
        mDatabase.child("tattoos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("yyy", "There are " + snapshot.getChildrenCount() + " tattoos");
                for (DataSnapshot tattooSnapshot : snapshot.getChildren()) {
                    Tattoo tattoo = tattooSnapshot.getValue(Tattoo.class);

                    if (tattoo == null) {
                        // Artist is null, error out
                        Log.e("yyy", "Tattoo is unexpectedly null");
                        Toast.makeText(ShowroomActivity.this,
                                "Error: could not fetch tattoo.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        GridItem item = new GridItem();
                        item.setIsTattoo(true);
                        item.setTattoo_title(tattoo.title);
                        item.setTattoo_url(tattoo.url);
                        item.setTattoo_artist(tattoo.artistName);
                        item.setTatto_body_part(tattoo.bodyPart);
                        item.setTattoo_style(tattoo.style);
                        mGridData.add(item);
                    }

                }
                mGridAdapter.setGridData(mGridData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("yyy", "getUser:onCancelled", databaseError.toException());
            }
        });
    }
}

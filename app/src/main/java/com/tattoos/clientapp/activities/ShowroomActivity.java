package com.tattoos.clientapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.tattoos.clientapp.models.Artist;
import com.tattoos.clientapp.models.Tattoo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowroomActivity extends AppCompatActivity {
    private String showroomType;

    private TextView showroomTitle;

    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showroom);

        showroomType = getIntent().getStringExtra(IntentKeys.SHOWROOM_TYPE.toString());


        showroomTitle = (TextView)findViewById(R.id.showroomTitleHolder);
        showroomTitle.setText(showroomType);
        mGridView = (GridView) findViewById(R.id.gridview);
        //mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mGridData = new ArrayList<>();


        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item, mGridData);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                if (showroomType.equals("tattoos")) {
                    //Pass the image details to TattooDetailsActivity
                    Intent intent = new Intent(ShowroomActivity.this, TattooDetailsActivity.class);
                    intent.putExtra(IntentKeys.SHOWROOM_TYPE.toString(), showroomType);
                    intent.putExtra(IntentKeys.TATTOO_TITLE.toString(), item.getTattoo_title());
                    intent.putExtra(IntentKeys.TATTOO_URL.toString(), item.getTattoo_url());
                    intent.putExtra(IntentKeys.TATTOO_ARTIST.toString(), item.getTattoo_artist());
                    intent.putExtra(IntentKeys.TATTOO_BODY_PART.toString(), item.getTatto_body_part());
                    intent.putExtra(IntentKeys.TATTOO_STYLE.toString(), item.getTattoo_style());

                    startActivity(intent);
                }
                if (showroomType.equals("artists")) {
                    Intent intent = new Intent(ShowroomActivity.this, TattooDetailsActivity.class);
                    intent.putExtra(IntentKeys.SHOWROOM_TYPE.toString(), showroomType);
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
                Log.d("yyy", "There are " + snapshot.getChildrenCount() + " artists");
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
                        Log.d("yyy", artist.username);

                        Map.Entry<String, String> entry = tats.entrySet().iterator().next();

                        GridItem item = new GridItem();
                        item.setIsTattoo(false);
                        item.setArtist_url(entry.getValue());
                        item.setArtist_bio(artist.bio);
                        item.setArtist_name(artist.username);
                        item.setArtist_locality(artist.locality);
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
                        Log.d("yyy", tattoo.bodyPart);

                        GridItem item = new GridItem();
                        item.setIsTattoo(true);
                        item.setTattoo_title(tattoo.title);
                        item.setTattoo_url(tattoo.url);
                        item.setTattoo_artist(tattoo.artist);
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

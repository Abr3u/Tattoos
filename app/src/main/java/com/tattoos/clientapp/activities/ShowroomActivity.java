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
import android.widget.Toast;

import com.tattoos.clientapp.MyApplicationContext;
import com.tattoos.clientapp.R;
import com.tattoos.clientapp.adapters.GridItem;
import com.tattoos.clientapp.adapters.GridViewAdapter;
import com.tattoos.clientapp.enums.IntentKeys;
import com.tattoos.clientapp.enums.JSONKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShowroomActivity extends AppCompatActivity {

    private static final String TAG = ShowroomActivity.class.getSimpleName();

    private String showroomType;
    private boolean cached;

    private MyApplicationContext myApplicationContext;
    private ProgressBar mProgressBar;
    private Button refreshButton;

    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;

    private String TATTOOS_URL = "http://192.168.1.69:9999/tattoos?count=10";
    private String ARTISTS_URL = "http://192.168.1.69:9999/artists";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showroom);

        showroomType = getIntent().getStringExtra(IntentKeys.SHOWROOM_TYPE.toString());

        refreshButton = (Button) findViewById(R.id.refreshButton);
        mGridView = (GridView) findViewById(R.id.gridview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        myApplicationContext = (MyApplicationContext) getApplicationContext();

        switch (showroomType){
            case "tattoos":
                if(myApplicationContext.getTattoosCache().isEmpty()){
                    Log.d("yyy","not cached");
                    //Initialize with empty data
                    mGridData = new ArrayList<>();
                    cached = false;
                }
                else{
                    Log.d("yyy","cached");
                    //Initialize with cached data
                    mGridData = myApplicationContext.getTattoosCache();
                    cached = true;
                }
                break;
            case "artists":
                if(myApplicationContext.getArtistsCache().isEmpty()){
                    Log.d("yyy","not cached");
                    //Initialize with empty data
                    mGridData = new ArrayList<>();
                    cached = false;
                }
                else{
                    Log.d("yyy","cached");
                    //Initialize with cached data
                    mGridData = myApplicationContext.getArtistsCache();
                    cached = true;
                }
        }

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
                    intent.putExtra(IntentKeys.TATTOO_BYTES.toString(), item.getTattoo_bytes());
                    intent.putExtra(IntentKeys.TATTOO_ARTIST.toString(), item.getTattoo_artist());
                    intent.putExtra(IntentKeys.TATTOO_BODY_PART.toString(), item.getTatto_body_part());
                    intent.putExtra(IntentKeys.TATTOO_STYLE.toString(), item.getTattoo_style());

                    startActivity(intent);
                }
                if (showroomType.equals("artists")) {
                    Intent intent = new Intent(ShowroomActivity.this, TattooDetailsActivity.class);
                    intent.putExtra(IntentKeys.SHOWROOM_TYPE.toString(), showroomType);
                    intent.putExtra(IntentKeys.ARTIST_AVATAR.toString(), item.getArtist_avatar());
                    intent.putExtra(IntentKeys.ARTIST_NAME.toString(), item.getArtist_name());
                    intent.putExtra(IntentKeys.ARTIST_BIO.toString(), item.getArtist_bio());

                    startActivity(intent);
                }
            }
        });

        if(!cached){
            if (showroomType.equals("tattoos")) {
                new AsyncHttpTask().execute(TATTOOS_URL);
            } else {
                new AsyncHttpTask().execute(ARTISTS_URL);
            }
        }
    }

    public void refreshButtonClicked(View view) {
        if (showroomType.equals("tattoos")) {
            new AsyncHttpTask().execute(TATTOOS_URL);
        } else {
            new AsyncHttpTask().execute(ARTISTS_URL);
        }
    }

    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            refreshButton.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Request request = new Request.Builder()
                    .url(params[0])
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    parseResult(response.body().string());
                    return 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 1) {
                if(showroomType.equals("tattoos")){
                    myApplicationContext.setTattoosCache(mGridData);
                }else{
                    myApplicationContext.setArtistsCache(mGridData);
                }
                mGridAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(ShowroomActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.GONE);
            refreshButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    private void parseResult(String result) {

        if (showroomType.equals("tattoos")) {

            try {
                JSONArray images = new JSONArray(result);
                GridItem item;
                for (int i = 0; i < images.length(); i++) {
                    JSONObject image = images.optJSONObject(i);
                    String title = image.optString(JSONKeys.TATTOO_TITLE.toString());
                    String artist = image.optString(JSONKeys.TATTOO_ARTIST.toString());
                    String bodyPart = image.optString(JSONKeys.TATTOO_BODY_PART.toString());
                    String style = image.optString(JSONKeys.TATTOO_STYLE.toString());

                    String imageStr = image.optString(JSONKeys.TATTOO_BYTES.toString());
                    byte[] imgBytes = Base64.decode(imageStr, Base64.DEFAULT);

                    item = new GridItem();
                    item.setTattoo_title(title);
                    item.setTattoo_bytes(imgBytes);
                    item.setTattoo_artist(artist);
                    item.setTatto_body_part(bodyPart);
                    item.setTattoo_style(style);
                    mGridData.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (showroomType.equals("artists")) {
            try {
                JSONArray artists = new JSONArray(result);
                GridItem item;
                for (int i = 0; i < artists.length(); i++) {
                    JSONObject artist = artists.optJSONObject(i);
                    String name = artist.optString(JSONKeys.ARTIST_NAME.toString());
                    String bio = artist.optString(JSONKeys.ARTIST_BIO.toString());

                    String avatarStr = artist.optString(JSONKeys.ARTIST_AVATAR.toString());
                    byte[] imgBytes = Base64.decode(avatarStr, Base64.DEFAULT);

                    item = new GridItem();
                    item.setArtist_avatar(imgBytes);
                    item.setArtist_bio(bio);
                    item.setArtist_name(name);
                    mGridData.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

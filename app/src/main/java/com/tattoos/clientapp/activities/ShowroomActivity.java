package com.tattoos.clientapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tattoos.clientapp.R;
import com.tattoos.clientapp.adapters.GridItem;
import com.tattoos.clientapp.adapters.GridViewAdapter;
import com.tattoos.clientapp.enums.IntentKeys;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShowroomActivity extends AppCompatActivity {

    private static final String TAG = ShowroomActivity.class.getSimpleName();
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private String FEED_URL = "http://192.168.1.69:9999/images?count=10";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showroom);

        mGridView = (GridView) findViewById(R.id.gridview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item, mGridData);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                //Pass the image details to DetailsActivity
                Intent intent = new Intent(ShowroomActivity.this, DetailsActivity.class);
                intent.putExtra(IntentKeys.IMG_TITLE.toString(), item.getTitle());
                intent.putExtra(IntentKeys.IMG_SOURCE.toString(), item.getImage());
                intent.putExtra(IntentKeys.IMG_AUTHOR.toString(), item.getAuthor());
                intent.putExtra(IntentKeys.IMG_BODY_PART.toString(), item.getBodyPart());
                intent.putExtra(IntentKeys.IMG_STYLE.toString(), item.getStyle());

                //Start details activity
                startActivity(intent);
            }
        });

        mProgressBar.setVisibility(View.VISIBLE);
        new AsyncHttpTask().execute(FEED_URL);
    }

    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

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
            protected void onPostExecute (Integer result){
                // Download complete. Let us update UI
                if (result == 1) {
                    mGridAdapter.setGridData(mGridData);
                } else {
                    Toast.makeText(ShowroomActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
                }
                mProgressBar.setVisibility(View.GONE);
            }
        }

        /**
         * Parsing the feed results and get the list
         *
         * @param result
         */
        private void parseResult(String result) {
            try {
                JSONArray images = new JSONArray(result);
                GridItem item;
                for (int i = 0; i < images.length(); i++) {
                    JSONObject image = images.optJSONObject(i);
                    String title = image.optString("imgTitle");
                    String author = image.optString("imgAuthor");
                    String bodyPart = image.optString("imgBodyPart");
                    String style = image.optString("imgStyle");

                    String imageStr = image.optString("imgBytes");
                    byte[] imgBytes = Base64.decode(imageStr, Base64.DEFAULT);

                    item = new GridItem();
                    item.setTitle(title);
                    item.setImage(imgBytes);
                    item.setAuthor(author);
                    item.setBodyPart(bodyPart);
                    item.setStyle(style);
                    mGridData.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

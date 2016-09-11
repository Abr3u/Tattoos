package com.tattoos.clientapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tattoos.clientapp.MyApplicationContext;
import com.tattoos.clientapp.R;
import com.tattoos.clientapp.enums.JSONKeys;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpArtistActivity extends AppCompatActivity {

    private static final String ARTIST_URL = "http://192.168.1.69:9999/artists";
    private static final int SELECT_PHOTO = 1;
    private static final int TAKE_PHOTO = 2;
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    private TextView pictureSelected;
    private EditText artistName;
    private EditText artistBio;

    private final String selectSuccess = "picture successfully selected";
    private String picturesDirectory;
    private String base64img;

    private final OkHttpClient client = new OkHttpClient();
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_artist);

        base64img = getApplicationContext().getResources().getString(R.string.default_avatar);

        mProgressBar = (ProgressBar) findViewById(R.id.signUpProgressBar);
        pictureSelected = (TextView) findViewById(R.id.artistPictureSelected);
        artistBio = (EditText) findViewById(R.id.artistBio);
        artistName = (EditText) findViewById(R.id.artistName);

        picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(picturesDirectory);
        newdir.mkdirs();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        //both take and select photo do the same
        if (resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                base64img = getBase64FromImageStream(imageStream);
                pictureSelected.setText(selectSuccess);
                pictureSelected.setBackgroundColor(Color.GREEN);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String getBase64FromImageStream(InputStream imageStream) {

        byte[] bytes = new byte[0];
        try {
            bytes = IOUtils.toByteArray(imageStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }

    public void submitButtonClicked(View view) {

        if (artistName.getText().toString().trim().isEmpty() || artistBio.getText().toString().trim().isEmpty() || base64img.isEmpty()) {
            Toast.makeText(SignUpArtistActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject postBody = new JSONObject();
        try {
            postBody.put(JSONKeys.ARTIST_AVATAR.toString(), base64img);
            postBody.put(JSONKeys.ARTIST_NAME.toString(), artistName.getText().toString().trim());
            postBody.put(JSONKeys.ARTIST_BIO.toString(), artistBio.getText().toString().trim());

            //MyApplicationContext context = (MyApplicationContext) getApplicationContext();
            postBody.put(JSONKeys.ARTIST_EMAIL.toString(), "amazing email");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncHttpTask().execute(postBody.toString());
    }

    public void takePictureButtonClicked(View view) {
        Date now = new Date();
        String pictureName = picturesDirectory + now.getTime() + ".jpg";
        File newfile = new File(pictureName);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
        }

        Uri outputFileUri = Uri.fromFile(newfile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(cameraIntent, TAKE_PHOTO);
    }

    public void choosePictureButtonClicked(View view) {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PHOTO);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Request request = new Request.Builder()
                    .url(ARTIST_URL)
                    .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, params[0]))
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!response.isSuccessful()) {
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 0) {
                Toast.makeText(SignUpArtistActivity.this, "Failed to post data!", Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.GONE);
        }
    }
}

package com.tattoos.clientapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tattoos.clientapp.MyApplicationContext;
import com.tattoos.clientapp.R;
import com.tattoos.clientapp.location.GPSTracker;
import com.tattoos.clientapp.location.LocationParser;
import com.tattoos.clientapp.models.Artist;
import com.tattoos.clientapp.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpArtistActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 1;
    private static final int TAKE_PHOTO = 2;
    private final String selectSuccess = "selected";
    private final String selectFailure = "select other";
    private final String loadingText = "just a second, thanks for your patience";
    private final String addMoreLaterText = "Dont worry, you can add more tattoos later";


    private TextView newProfileHolder;
    private TextView showcaseHolder;
    private TextView aboutYouHolder;
    private TextView helperViewHolder;
    private TableLayout tableLayout;
    private EditText artistBio;
    private Button submitButton;
    private Button buttonToUpdate;
    private ProgressBar mProgressBar;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://tattoos-2166f.appspot.com");
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private int counter;
    private int indexToUpdate;
    private String picturesDirectory;
    private String latitude;
    private String longitude;
    private String locality;
    private Uri[] localImagesUris;
    private MyApplicationContext mContext;
    private GPSTracker mGPS;
    private ArrayList<String> downloadUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_artist);

        mContext = (MyApplicationContext) getApplicationContext();

        mGPS = new GPSTracker(SignUpArtistActivity.this);
        localImagesUris = new Uri[3];
        downloadUrls = new ArrayList<String>();

        newProfileHolder = (TextView)findViewById(R.id.signUpArtistBioHolder);
        showcaseHolder = (TextView)findViewById(R.id.chooseTattosHolder);
        aboutYouHolder = (TextView) findViewById(R.id.signUpArtistBioHolder);
        helperViewHolder = (TextView)findViewById(R.id.addMoreTattosLater);
        tableLayout = (TableLayout) findViewById(R.id.tableSignUp);
        mProgressBar = (ProgressBar) findViewById(R.id.signUpProgressBar);
        artistBio = (EditText) findViewById(R.id.artistBio);
        submitButton = (Button) findViewById(R.id.submitButton);

        counter = 0;
        locality = "undefined";
        latitude = "undefined";
        longitude = "undefined";
        picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(picturesDirectory);
        newdir.mkdirs();

        LocalityThread thread = new LocalityThread();
        new Thread(thread).start();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        //both take and select photo do the same
        if (resultCode == RESULT_OK) {
            if (uriAlreadyExists(localImagesUris, imageReturnedIntent.getData())) {
                buttonToUpdate.setText(selectFailure);
                buttonToUpdate.setBackgroundColor(Color.RED);
                return;
            }
            if (counter < 4) {
                counter++;
                Log.d("yyy", "counter ficou " + counter);
            }
            buttonToUpdate.setText(selectSuccess);
            buttonToUpdate.setBackgroundColor(Color.GREEN);
            localImagesUris[indexToUpdate] = imageReturnedIntent.getData();
        }
    }

    private boolean uriAlreadyExists(Uri[] localImagesUris, Uri toCheck) {
        if (localImagesUris[0] != null && localImagesUris[0].equals(toCheck)) return true;
        if (localImagesUris[1] != null && localImagesUris[1].equals(toCheck)) return true;
        if (localImagesUris[2] != null && localImagesUris[2].equals(toCheck)) return true;
        return false;
    }


    public void submitButtonClicked(View view) {

        final String bio = artistBio.getText().toString().trim();

        if (bio.isEmpty() || (localImagesUris[0] == null && localImagesUris[1] == null && localImagesUris[2] == null)) {
            Log.d("yyy", "not all info");
            Toast.makeText(SignUpArtistActivity.this, "Please provide at least one tattoo and your bio", Toast.LENGTH_SHORT).show();
            return;
        }

        setEditingEnabled(false);
        // [START single_value_read]
        final String userId = mContext.getFirebaseUser().getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e("yyy", "User " + userId + " is unexpectedly null");
                            Toast.makeText(SignUpArtistActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            int i;
                            for (i = 0; i < localImagesUris.length; i++) {
                                Uri uri = localImagesUris[i];
                                Log.d("yyy", "uri -> " + uri);
                                if (uri != null) {
                                    Log.d("yyy", "uploading img");
                                    HashMap<String,String> tattooDetails = getTattooDetails(i);
                                    tattooDetails.put("artist",userId);
                                    ImageUploaderThread thread = new ImageUploaderThread(uri, userId,tattooDetails);
                                    new Thread(thread).start();
                                }
                            }
                            while (counter != 0) {

                            }
                            writeNewArtist(userId, bio, user.username,downloadUrls);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("yyy", "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });

    }

    private HashMap<String, String> getTattooDetails(int i) {
        HashMap<String, String> details = new HashMap<>();
        if (i == 0) {
            EditText titleET = (EditText) findViewById(R.id.TattooDescription11);
            EditText bodyPartET = (EditText) findViewById(R.id.TattooBodyPart11);
            EditText styleET = (EditText) findViewById(R.id.TattooStyle11);

            String title = titleET.getText().toString().trim();
            String bodyPart = bodyPartET.getText().toString().trim();
            String style = styleET.getText().toString().trim();

            if (title.isEmpty()) {
                title = "undefined";
            }
            if (bodyPart.isEmpty()) {
                bodyPart = "undefined";
            }
            if (style.isEmpty()) {
                style = "undefined";
            }
            details.put("title", title);
            details.put("bodyPart", bodyPart);
            details.put("style", style);
        }
        if (i == 1) {
            EditText titleET = (EditText) findViewById(R.id.TattooDescription12);
            EditText bodyPartET = (EditText) findViewById(R.id.TattooBodyPart12);
            EditText styleET = (EditText) findViewById(R.id.TattooStyle12);

            String title = titleET.getText().toString().trim();
            String bodyPart = bodyPartET.getText().toString().trim();
            String style = styleET.getText().toString().trim();

            if (title.isEmpty()) {
                title = "undefined";
            }
            if (bodyPart.isEmpty()) {
                bodyPart = "undefined";
            }
            if (style.isEmpty()) {
                style = "undefined";
            }
            details.put("title", title);
            details.put("bodyPart", bodyPart);
            details.put("style", style);
        }
        if (i == 2) {
            EditText titleET = (EditText) findViewById(R.id.TattooDescription13);
            EditText bodyPartET = (EditText) findViewById(R.id.TattooBodyPart13);
            EditText styleET = (EditText) findViewById(R.id.TattooStyle13);

            String title = titleET.getText().toString().trim();
            String bodyPart = bodyPartET.getText().toString().trim();
            String style = styleET.getText().toString().trim();

            if (title.isEmpty()) {
                title = "undefined";
            }
            if (bodyPart.isEmpty()) {
                bodyPart = "undefined";
            }
            if (style.isEmpty()) {
                style = "undefined";
            }
            details.put("title", title);
            details.put("bodyPart", bodyPart);
            details.put("style", style);
        }
        return details;
    }

    public void choosePictureButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.selectPictureButton11:
                buttonToUpdate = (Button) findViewById(R.id.selectPictureButton11);
                indexToUpdate = 0;
                break;
            case R.id.selectPictureButton12:
                buttonToUpdate = (Button) findViewById(R.id.selectPictureButton12);
                indexToUpdate = 1;
                break;
            case R.id.selectPictureButton13:
                buttonToUpdate = (Button) findViewById(R.id.selectPictureButton13);
                indexToUpdate = 2;
                break;
        }
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PHOTO);
    }

    public void uploadImageFirebase(Uri uri, final String uid, final HashMap<String,String> tattooDetails) throws IOException {
        StorageReference imagesRef = storageRef.child("images/" + uid).child(uri.getLastPathSegment());
        imagesRef.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                Log.d("yyy", "uploaded");
                downloadUrls.add(downloadUrl.toString());
                writeNewTattoo(downloadUrl.toString(),tattooDetails);
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("yyy", "uploadFromUri:onFailure", e);
            }
        });
    }

    private void writeNewArtist(String userId, String bio, String username,ArrayList<String> urls) {
        Artist artist = new Artist(username, bio, locality,latitude,longitude,urls);
        Map<String, Object> artistValues = artist.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/artists/" + userId, artistValues);

        mDatabase.updateChildren(childUpdates);
        finish();
    }

    private void writeNewTattoo(String tattooUrl, HashMap tattooDetails) {
        String key = mDatabase.child("tattoos").push().getKey();
        tattooDetails.put("url",tattooUrl);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/tattoos/" +key, tattooDetails);

        mDatabase.updateChildren(childUpdates);
        counter--;
    }

    private String getUserLocality() {
        if (mGPS.canGetLocation()) {
            latitude = ""+mGPS.getLatitude();
            longitude = ""+mGPS.getLongitude();
            JSONObject json = LocationParser.getGoogleLocationInfo(latitude, longitude);
            return LocationParser.getLocalityFromGoogleJSON(json);
        }
        return "undefined";
    }

    private void setEditingEnabled(boolean enabled) {
        if (enabled) {
            helperViewHolder.setText(addMoreLaterText);
            mProgressBar.setVisibility(View.GONE);
            showcaseHolder.setVisibility(View.VISIBLE);
            newProfileHolder.setVisibility(View.VISIBLE);
            aboutYouHolder.setVisibility(View.VISIBLE);
            artistBio.setVisibility(View.VISIBLE);
            tableLayout.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
        } else {
            helperViewHolder.setText(loadingText);
            mProgressBar.setVisibility(View.VISIBLE);
            showcaseHolder.setVisibility(View.GONE);
            newProfileHolder.setVisibility(View.GONE);
            aboutYouHolder.setVisibility(View.GONE);
            artistBio.setVisibility(View.GONE);
            tableLayout.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
        }
    }

    final class ImageUploaderThread implements Runnable {

        Uri _uri;
        String _uid;
        HashMap<String,String> _tattooDetails;

        public ImageUploaderThread(Uri uri, String uid,HashMap tattooDetails) {
            this._uri = uri;
            this._uid = uid;
            this._tattooDetails = tattooDetails;
        }

        @Override
        public void run() {
            try {
                uploadImageFirebase(_uri, _uid,_tattooDetails);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    final class LocalityThread implements Runnable {

        @Override
        public void run() {
            locality = getUserLocality();
        }
    }
}

package com.tattoos.clientapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpArtistActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 1;
    private static final int TAKE_PHOTO = 2;
    private final String selectSuccess = "picture selected";
    private final String selectFailure = "select another picture";


    private EditText artistBio;
    private Button submitButton;
    private TextView textViewToUpdate;
    private ProgressBar mProgressBar;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://tattoos-2166f.appspot.com");
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private int counter;
    private int indexToUpdate;
    private String picturesDirectory;
    private String locality;
    private Uri[] localImagesUris;
    private ArrayList<String> downloadUrls;
    private MyApplicationContext mContext;
    private GPSTracker mGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_artist);

        mContext = (MyApplicationContext) getApplicationContext();

        mGPS = new GPSTracker(SignUpArtistActivity.this);
        localImagesUris = new Uri[4];
        downloadUrls = new ArrayList<String>();

        mProgressBar = (ProgressBar) findViewById(R.id.signUpProgressBar);
        artistBio = (EditText) findViewById(R.id.artistBio);
        submitButton = (Button) findViewById(R.id.submitButton);

        counter = 0;
        locality = "undefined";
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
                textViewToUpdate.setText(selectFailure);
                textViewToUpdate.setBackgroundColor(Color.RED);
                return;
            }
            if (counter < 4) {
                counter++;
                Log.d("yyy", "counter ficou " + counter);
            }
            textViewToUpdate.setText(selectSuccess);
            textViewToUpdate.setBackgroundColor(Color.GREEN);
            localImagesUris[indexToUpdate] = imageReturnedIntent.getData();
        }
    }

    private boolean uriAlreadyExists(Uri[] localImagesUris, Uri toCheck) {
        if (localImagesUris[0] != null && localImagesUris[0].equals(toCheck)) return true;
        if (localImagesUris[1] != null && localImagesUris[1].equals(toCheck)) return true;
        if (localImagesUris[2] != null && localImagesUris[2].equals(toCheck)) return true;
        if (localImagesUris[3] != null && localImagesUris[3].equals(toCheck)) return true;
        return false;
    }


    public void submitButtonClicked(View view) {

        final String bio = artistBio.getText().toString().trim();

        if (bio.isEmpty() || (localImagesUris[0] == null && localImagesUris[1] == null && localImagesUris[2] == null && localImagesUris[3] == null)) {
            Log.d("yyy", "not all info");
            Toast.makeText(SignUpArtistActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
            return;
        }

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
                            for (Uri uri : localImagesUris) {
                                Log.d("yyy","uri -> "+uri);
                                if (uri != null) {
                                    Log.d("yyy", "uploading img");
                                    ImageUploaderThread thread = new ImageUploaderThread(uri, userId);
                                    new Thread(thread).start();
                                }
                            }
                            while (counter != 0) {

                            }
                            writeNewArtist(userId, bio, user.username, downloadUrls);
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

    public void takePictureButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.takePictureButton1:
                textViewToUpdate = (TextView) findViewById(R.id.tattoo1Holder);
                indexToUpdate = 0;
                break;
            case R.id.takePictureButton2:
                textViewToUpdate = (TextView) findViewById(R.id.tattoo2Holder);
                indexToUpdate = 1;
                break;
            case R.id.takePictureButton3:
                textViewToUpdate = (TextView) findViewById(R.id.tattoo3Holder);
                indexToUpdate = 2;
                break;
            case R.id.takePictureButton4:
                textViewToUpdate = (TextView) findViewById(R.id.tattoo4Holder);
                indexToUpdate = 3;
                break;
        }
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
        switch (view.getId()) {
            case R.id.cameraRollButton1:
                textViewToUpdate = (TextView) findViewById(R.id.tattoo1Holder);
                indexToUpdate = 0;
                break;
            case R.id.cameraRollButton2:
                textViewToUpdate = (TextView) findViewById(R.id.tattoo2Holder);
                indexToUpdate = 1;
                break;
            case R.id.cameraRollButton3:
                textViewToUpdate = (TextView) findViewById(R.id.tattoo3Holder);
                indexToUpdate = 2;
                break;
            case R.id.cameraRollButton4:
                textViewToUpdate = (TextView) findViewById(R.id.tattoo4Holder);
                indexToUpdate = 3;
                break;
        }
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PHOTO);
    }

    public void uploadImageFirebase(Uri uri, final String uid) throws IOException {
        StorageReference imagesRef = storageRef.child("images/" + uid).child(uri.getLastPathSegment());
        imagesRef.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                downloadUrls.add(downloadUrl.toString());
                Log.d("yyy", "uploaded");
                counter--;
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("yyy", "uploadFromUri:onFailure", e);
            }
        });
    }

    private void writeNewArtist(String userId, String bio, String username, ArrayList<String> urls) {
        Artist artist = new Artist(username, bio, locality, urls);
        Map<String, Object> artistValues = artist.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/artists/" + userId, artistValues);

        mDatabase.updateChildren(childUpdates);
        finish();
    }

    private String getUserLocality() {
        if (mGPS.canGetLocation()) {
            JSONObject json = LocationParser.getGoogleLocationInfo(mGPS.getLatitude(), mGPS.getLongitude());
            return LocationParser.getLocalityFromGoogleJSON(json);
        }
        return "undefined";
    }

    private void setEditingEnabled(boolean enabled) {
        artistBio.setEnabled(enabled);
        if (enabled) {
            submitButton.setVisibility(View.VISIBLE);
        } else {
            submitButton.setVisibility(View.GONE);
        }
    }

    final class ImageUploaderThread implements Runnable {

        Uri _uri;
        String _uid;

        public ImageUploaderThread(Uri uri, String uid) {
            this._uri = uri;
            this._uid = uid;
        }

        @Override
        public void run() {
            try {
                uploadImageFirebase(_uri, _uid);
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

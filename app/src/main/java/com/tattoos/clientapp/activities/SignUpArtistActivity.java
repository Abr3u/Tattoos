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
import com.tattoos.clientapp.models.Artist;
import com.tattoos.clientapp.models.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpArtistActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 1;
    private static final int TAKE_PHOTO = 2;
    private final String selectSuccess = "picture successfully selected";
    private final String selectFailure = "picture couldnt be selected";

    private TextView pictureSelected;
    private EditText artistBio;
    private Button submitButton;
    private ProgressBar mProgressBar;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://tattoos-2166f.appspot.com");
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    private String picturesDirectory;
    private Uri selectedImageUri;
    private Uri downloadUrl;
    private MyApplicationContext mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_artist);

        mContext = (MyApplicationContext)getApplicationContext();


        mProgressBar = (ProgressBar) findViewById(R.id.signUpProgressBar);
        pictureSelected = (TextView) findViewById(R.id.artistPictureSelected);
        artistBio = (EditText) findViewById(R.id.artistBio);
        submitButton = (Button) findViewById(R.id.submitButton);

        picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(picturesDirectory);
        newdir.mkdirs();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        //both take and select photo do the same
        if (resultCode == RESULT_OK) {
            selectedImageUri = imageReturnedIntent.getData();
            pictureSelected.setText(selectSuccess);
            pictureSelected.setBackgroundColor(Color.GREEN);
        }
    }


    public void submitButtonClicked(View view) {

        if (artistBio.getText().toString().trim().isEmpty() || selectedImageUri == null) {
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
                            try {
                                uploadImageFirebase(selectedImageUri,userId,user.username);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

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

    private void writeNewArtist(String userId, String bio,String avatarUrl) {
        ArrayList<String> aux = new ArrayList<>();
        aux.add("primeira");
        aux.add("segunda");
        aux.add("terceira");
        Artist artist = new Artist(bio,avatarUrl,aux);
        Map<String, Object> artistValues = artist.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/artists/" + userId, artistValues);

        mDatabase.updateChildren(childUpdates);

        Log.d("yyy","adicionei artist");
        finish();
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

    public void uploadImageFirebase(Uri uri, final String uid, final String username) throws IOException {
        final String bio = artistBio.getText().toString().trim();

        Log.d("yyy","unique? "+uri.getLastPathSegment());
        StorageReference imagesRef = storageRef.child("images/"+uid).child(uri.getLastPathSegment());
        imagesRef.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("yyy", "uploadFromUri:onSuccess");
                downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                Log.d("yyy","dwnURI "+downloadUrl);

                writeNewArtist(uid,bio,downloadUrl.toString());
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("yyy", "uploadFromUri:onFailure", e);
                pictureSelected.setText(selectFailure);
                pictureSelected.setBackgroundColor(Color.RED);
            }
        });
    }

    private void setEditingEnabled(boolean enabled) {
        artistBio.setEnabled(enabled);
        if (enabled) {
            submitButton.setVisibility(View.VISIBLE);
        } else {
            submitButton.setVisibility(View.GONE);
        }
    }
}

package com.tattoos.clientapp.activities;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.tattoos.clientapp.R;
import com.tattoos.clientapp.enums.IntentKeys;

public class TattooDetailsActivity extends ActionBarActivity {
    private TextView titleTextView;
    private TextView authorTextView;
    private ImageView imageView;

    private String showroomType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        showroomType = getIntent().getStringExtra(IntentKeys.SHOWROOM_TYPE.toString());

        titleTextView = (TextView) findViewById(R.id.tattoo_title);
        authorTextView = (TextView) findViewById(R.id.tattoo_artist);
        imageView = (ImageView) findViewById(R.id.grid_item_image);

        if(showroomType.equals("tattoos")) {
            String title = getIntent().getStringExtra(IntentKeys.TATTOO_TITLE.toString());
            String artist = getIntent().getStringExtra(IntentKeys.TATTOO_ARTIST.toString());
            String style = getIntent().getStringExtra(IntentKeys.TATTOO_STYLE.toString());
            String bodyPart = getIntent().getStringExtra(IntentKeys.TATTOO_BODY_PART.toString());
            byte[] image = getIntent().getByteArrayExtra(IntentKeys.TATTOO_URL.toString());

            titleTextView.setText(title);
            authorTextView.setText("By : "+artist);

            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            imageView.setImageBitmap(bmp);
        }
        if(showroomType.equals("artists")){
            String name = getIntent().getStringExtra(IntentKeys.ARTIST_NAME.toString());
            String bio = getIntent().getStringExtra(IntentKeys.ARTIST_BIO.toString());
            byte[] avatar = getIntent().getByteArrayExtra(IntentKeys.ARTIST_URL.toString());

            titleTextView.setText(name);
            authorTextView.setText(bio);

            Bitmap bmp = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
            imageView.setImageBitmap(bmp);
        }
    }
}

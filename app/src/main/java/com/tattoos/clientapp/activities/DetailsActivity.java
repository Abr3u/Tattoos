package com.tattoos.clientapp.activities;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import com.tattoos.clientapp.R;
import com.tattoos.clientapp.enums.IntentKeys;

public class DetailsActivity extends ActionBarActivity {
    private TextView titleTextView;
    private TextView authorTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        String title = getIntent().getStringExtra(IntentKeys.IMG_TITLE.toString());
        String author = getIntent().getStringExtra(IntentKeys.IMG_AUTHOR.toString());
        String style = getIntent().getStringExtra(IntentKeys.IMG_STYLE.toString());
        String bodyPart = getIntent().getStringExtra(IntentKeys.IMG_BODY_PART.toString());
        byte[] image = getIntent().getByteArrayExtra(IntentKeys.IMG_SOURCE.toString());

        titleTextView = (TextView) findViewById(R.id.title);
        authorTextView = (TextView) findViewById(R.id.author);
        imageView = (ImageView) findViewById(R.id.grid_item_image);

        titleTextView.setText(title);
        authorTextView.setText("By : "+author);

        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
        imageView.setImageBitmap(bmp);
    }
}

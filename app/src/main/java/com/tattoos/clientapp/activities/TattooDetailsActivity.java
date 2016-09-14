package com.tattoos.clientapp.activities;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tattoos.clientapp.R;
import com.tattoos.clientapp.enums.IntentKeys;

public class TattooDetailsActivity extends ActionBarActivity {
    private TextView titleTextView;
    private TextView detail1TextView;
    private TextView detail2TextView;
    private TextView detail3TextView;
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
        detail1TextView = (TextView) findViewById(R.id.tattoo_detail1);
        detail2TextView = (TextView) findViewById(R.id.tattoo_detail2);
        detail3TextView = (TextView) findViewById(R.id.tattoo_detail3);
        imageView = (ImageView) findViewById(R.id.details_image);

        if(showroomType.equals("Tattoos")) {
            String title = getIntent().getStringExtra(IntentKeys.TATTOO_TITLE.toString());
            String artist = getIntent().getStringExtra(IntentKeys.TATTOO_ARTIST.toString());
            String style = getIntent().getStringExtra(IntentKeys.TATTOO_STYLE.toString());
            String bodyPart = getIntent().getStringExtra(IntentKeys.TATTOO_BODY_PART.toString());
            String url = getIntent().getStringExtra(IntentKeys.TATTOO_URL.toString());

            int nextToUpdate = 0;
            if(!title.equals("undefined")){
                updateNextDetailTextView(nextToUpdate,title);
                nextToUpdate++;
            }
            if(!artist.equals("undefined")){
                String text = "By: "+artist;
                updateNextDetailTextView(nextToUpdate,text);
                nextToUpdate++;
            }
            if(!bodyPart.equals("undefined")) {
                String text = "Body Part: "+bodyPart;
                updateNextDetailTextView(nextToUpdate,text);
                nextToUpdate++;
            }
            if(!style.equals("undefined")){
                String text = "Style: "+style;
                updateNextDetailTextView(nextToUpdate,text);
                nextToUpdate++;
            }

            Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .crossFade()
                    .into(imageView);
        }
        if(showroomType.equals("Artists")){
            String name = getIntent().getStringExtra(IntentKeys.ARTIST_NAME.toString());
            String bio = getIntent().getStringExtra(IntentKeys.ARTIST_BIO.toString());
            String url = getIntent().getStringExtra(IntentKeys.ARTIST_URL.toString());
            String locality = getIntent().getStringExtra(IntentKeys.ARTIST_LOCALITY.toString());

            int nextToUpdate = 0;
            if(!name.equals("undefined")){
                updateNextDetailTextView(nextToUpdate,name);
                nextToUpdate++;
            }
            if(!locality.equals("undefined")){
                String text = "Locality: "+locality;
                updateNextDetailTextView(nextToUpdate,text);
                nextToUpdate++;
            }
            if(!bio.equals("undefined")){
                String text = "\""+bio+"\"";
                updateNextDetailTextView(nextToUpdate,text);
                nextToUpdate++;
            }

            Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .crossFade()
                    .into(imageView);
        }
    }

    private void updateNextDetailTextView(int nextToUpdate, String text) {
        switch (nextToUpdate){
            case 0:
                titleTextView.setText(text);
                titleTextView.setVisibility(View.VISIBLE);
                break;
            case 1:
                detail1TextView.setText(text);
                detail1TextView.setVisibility(View.VISIBLE);
                break;
            case 2:
                detail2TextView.setText(text);
                detail2TextView.setVisibility(View.VISIBLE);
                break;
            case 3:
                detail3TextView.setText(text);
                detail3TextView.setVisibility(View.VISIBLE);
                break;
        }
    }
}

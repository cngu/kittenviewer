package com.cngu.kittenviewer.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cngu.kittenviewer.R;
import com.cngu.kittenviewer.ui.view.KittenPhotoView;


public class KittenPhotoActivity extends AppCompatActivity implements KittenPhotoView {

    private EditText mWidthEditText;
    private EditText mHeightEditText;
    private Button mSearchButton;
    private ImageView mKittenImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitten_photo);

        // Use Toolbar as Action Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find and initialize views from layout
        mWidthEditText = (EditText) findViewById(R.id.width_edittext);
        mHeightEditText = (EditText) findViewById(R.id.height_edittext);
        mSearchButton = (Button) findViewById(R.id.search_button);
        mKittenImageView = (ImageView) findViewById(R.id.kitten_imageview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_kitten_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getWidth() {
        String widthText = mWidthEditText.getText().toString();
        if (widthText.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(widthText);
    }

    @Override
    public int getHeight() {
        String heightText = mHeightEditText.getText().toString();
        if (heightText.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(heightText);
    }

    @Override
    public void setKittenBitmap(Bitmap kittenBitmap) {
        mKittenImageView.setImageBitmap(kittenBitmap);
    }
}

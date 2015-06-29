package com.cngu.kittenviewer.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cngu.kittenviewer.R;
import com.cngu.kittenviewer.domain.service.ImageDownloadServiceImpl;
import com.cngu.kittenviewer.ui.presenter.KittenPhotoPresenter;
import com.cngu.kittenviewer.ui.presenter.KittenPhotoPresenterImpl;
import com.cngu.kittenviewer.ui.view.KittenPhotoView;

public class KittenPhotoActivity extends AppCompatActivity implements KittenPhotoView,
                                                                      ServiceConnection {

    private EditText mWidthEditText;
    private EditText mHeightEditText;
    private Button mSearchButton;
    private ImageView mKittenImageView;

    private KittenPhotoPresenter mPresenter;

    private boolean mServiceBound = false;
    private ImageDownloadServiceImpl mImageDownloadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initializeView() {
        setContentView(R.layout.activity_kitten_photo);

        // Use Toolbar as Action Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find views from layout
        mWidthEditText = (EditText) findViewById(R.id.width_edittext);
        mHeightEditText = (EditText) findViewById(R.id.height_edittext);
        mSearchButton = (Button) findViewById(R.id.search_button);
        mKittenImageView = (ImageView) findViewById(R.id.kitten_imageview);

        // Initialize views
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.onRequestedPhotoDimenChanged(getRequestedPhotoWidth(), getRequestedPhotoHeight());
            }
        };

        mWidthEditText.addTextChangedListener(tw);
        mHeightEditText.addTextChangedListener(tw);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onSearchButtonClicked();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Bind to ImageDownloadServiceImpl
        if (!mServiceBound) {
            Intent bindIntent = new Intent(this, ImageDownloadServiceImpl.class);
            bindService(bindIntent, this, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unbind from ImageDownloadServiceImpl
        if (mServiceBound) {
            unbindService(this);
            mServiceBound = false;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // Bind to service
        ImageDownloadServiceImpl.ImageDownloadBinder binder =
                (ImageDownloadServiceImpl.ImageDownloadBinder) service;

        mImageDownloadService = binder.getService();
        mServiceBound = true;

        // Initialize View Presenter
        initializeView();

        mPresenter = new KittenPhotoPresenterImpl(this);
        mPresenter.onViewCreated();
        mPresenter.onServiceConnected(mImageDownloadService);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mServiceBound = false;
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
    public int getRequestedPhotoWidth() {
        String widthText = mWidthEditText.getText().toString();
        if (widthText.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(widthText);
    }

    @Override
    public int getRequestedPhotoHeight() {
        String heightText = mHeightEditText.getText().toString();
        if (heightText.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(heightText);
    }

    @Override
    public void setSearchButtonEnabled(boolean enabled) {
        mSearchButton.setEnabled(enabled);
    }

    @Override
    public void setKittenBitmap(Bitmap kittenBitmap) {
        mKittenImageView.setImageBitmap(kittenBitmap);
    }
}

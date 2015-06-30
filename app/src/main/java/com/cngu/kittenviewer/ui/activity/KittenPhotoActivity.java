package com.cngu.kittenviewer.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.MyCircleImageView;
import android.support.v4.widget.MyMaterialProgressDrawable;
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
import android.widget.RelativeLayout;

import com.cngu.kittenviewer.R;
import com.cngu.kittenviewer.domain.service.ImageDownloadService;
import com.cngu.kittenviewer.domain.service.ImageDownloadServiceImpl;
import com.cngu.kittenviewer.ui.presenter.KittenPhotoPresenter;
import com.cngu.kittenviewer.ui.presenter.KittenPhotoPresenterImpl;
import com.cngu.kittenviewer.ui.view.KittenPhotoView;

public class KittenPhotoActivity extends AppCompatActivity implements KittenPhotoView,
                                                                      ServiceConnection,
                                                                      UIThreadExecutor {

    private EditText mWidthEditText;
    private EditText mHeightEditText;
    private Button mSearchButton;
    //private ProgressBar mProgressBar;
    private MyCircleImageView mCircleView;
    private MyMaterialProgressDrawable mProgress;
    private ImageView mKittenImageView;

    private KittenPhotoPresenter mPresenter;

    private Intent mServiceIntent;
    private boolean mServiceBound = false;
    private ImageDownloadService mImageDownloadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mServiceIntent = new Intent(this, ImageDownloadServiceImpl.class);

        if (savedInstanceState == null) {
            startService(mServiceIntent);
        }
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
        //mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
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

        RelativeLayout photoContainer = (RelativeLayout) findViewById(R.id.photo_container);

        mCircleView = new MyCircleImageView(this, CIRCLE_BG_LIGHT, CIRCLE_DIAMETER_LARGE/2);
        mProgress = new MyMaterialProgressDrawable(this, photoContainer);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(null);
        mProgress.updateSizes(MyMaterialProgressDrawable.LARGE);
        mCircleView.setImageDrawable(mProgress);
        //mCircleView.setVisibility(View.GONE);

        photoContainer.addView(mCircleView);

        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams)mCircleView.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mCircleView.setLayoutParams(layoutParams);


        mProgress.setColorSchemeColors(getResources().getColor(R.color.color_accent));
        mProgress.setAlpha(255);
        mProgress.start();
    }

    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to ImageDownloadServiceImpl
        if (!mServiceBound) {
            bindService(mServiceIntent, this, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        // Unbind from ImageDownloadServiceImpl
        if (mServiceBound) {
            unbindService(this);
            mServiceBound = false;
        }

        super.onStop();
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

        mPresenter = new KittenPhotoPresenterImpl(this, this);
        mPresenter.onServiceConnected(mImageDownloadService);
        mPresenter.onViewCreated();
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

    @Override
    public void setDownloadProgressBarVisibility(boolean visible) {
        if (visible) {
            mProgress.start();
            mCircleView.setVisibility(View.VISIBLE);
        } else {
            mProgress.stop();
            mCircleView.setVisibility(View.GONE);
        }
    }
}

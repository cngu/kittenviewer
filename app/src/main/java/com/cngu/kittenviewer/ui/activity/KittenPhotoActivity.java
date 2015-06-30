package com.cngu.kittenviewer.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import com.cngu.kittenviewer.R;
import com.cngu.kittenviewer.domain.service.ImageDownloadService;
import com.cngu.kittenviewer.domain.service.ImageDownloadServiceImpl;
import com.cngu.kittenviewer.ui.presenter.KittenPhotoPresenter;
import com.cngu.kittenviewer.ui.presenter.KittenPhotoPresenterImpl;
import com.cngu.kittenviewer.ui.view.KittenPhotoView;

public class KittenPhotoActivity extends AppCompatActivity implements KittenPhotoView,
                                                                      ServiceConnection,
                                                                      UIThreadExecutor {
    private static final String BUNDLE_KEY_WIDTH = "cngu.bundle.key.WIDTH";
    private static final String BUNDLE_KEY_HEIGHT = "cngu.bundle.key.HEIGHT";
    private static final String BUNDLE_KEY_BITMAP = "cngu.bundle.key.BITMAP";

    private Bundle mSavedState;

    private EditText mWidthEditText;
    private EditText mHeightEditText;
    private Button mSearchButton;
    private MyCircleImageView mProgressCircleView;
    private MyMaterialProgressDrawable mProgress;
    private ImageView mKittenImageView;
    private Bitmap mKittenBitmap;

    private KittenPhotoPresenter mPresenter;

    private Toast mToast;

    private Intent mServiceIntent;
    private boolean mServiceBound = false;
    private ImageDownloadService mImageDownloadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSavedState = savedInstanceState;

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
        mKittenImageView = (ImageView) findViewById(R.id.kitten_imageview);
        RelativeLayout photoContainer = (RelativeLayout) findViewById(R.id.photo_container);

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

        // Initialize and dynamically add custom progress bar
        int progressBackground = MyMaterialProgressDrawable.CIRCLE_BG_LIGHT;
        int progressCircleDiameter = MyMaterialProgressDrawable.CIRCLE_DIAMETER_LARGE;
        int progressAlpha = MyMaterialProgressDrawable.MAX_ALPHA;

        mProgress = new MyMaterialProgressDrawable(this, photoContainer);
        mProgress.updateSizes(MyMaterialProgressDrawable.LARGE);
        mProgress.setBackgroundColor(progressBackground);
        mProgress.setColorSchemeColors(getResources().getColor(R.color.progressbar_tint));
        mProgress.setAlpha(progressAlpha);

        mProgressCircleView = new MyCircleImageView(this, progressBackground, progressCircleDiameter/2);
        mProgressCircleView.setImageDrawable(null);
        mProgressCircleView.setImageDrawable(mProgress);

        mProgressCircleView.setVisibility(View.GONE);

        photoContainer.addView(mProgressCircleView);
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) mProgressCircleView.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mProgressCircleView.setLayoutParams(layoutParams);
    }

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
    protected void onSaveInstanceState(Bundle outState) {
        saveViewState(outState);
        super.onSaveInstanceState(outState);
    }

    private void restoreViewState(Bundle inState) {
        if (inState != null) {
            String width = inState.getString(BUNDLE_KEY_WIDTH);
            String height = inState.getString(BUNDLE_KEY_HEIGHT);
            mKittenBitmap = inState.getParcelable(BUNDLE_KEY_BITMAP);

            mWidthEditText.setText(width);
            mHeightEditText.setText(height);
            mKittenImageView.setImageBitmap(mKittenBitmap);
        }
    }

    private void saveViewState(Bundle outState) {
        String width = mWidthEditText.getText().toString();
        String height = mHeightEditText.getText().toString();
        Bitmap bitmap = mKittenBitmap;

        outState.putString(BUNDLE_KEY_WIDTH, width);
        outState.putString(BUNDLE_KEY_HEIGHT, height);
        outState.putParcelable(BUNDLE_KEY_BITMAP, bitmap);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // Bind to service
        ImageDownloadServiceImpl.ImageDownloadBinder binder =
                (ImageDownloadServiceImpl.ImageDownloadBinder) service;

        mImageDownloadService = binder.getService();
        mServiceBound = true;

        // Initialize View and Presenter
        mPresenter = new KittenPhotoPresenterImpl(this, this);
        mPresenter.onServiceConnected(mImageDownloadService);

        initializeView();
        restoreViewState(mSavedState);

        // Notify presenter of first view creation only
        if (mSavedState == null) {
            mPresenter.onViewCreated();
        }

        mSavedState = null;
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
    public void setKittenPhoto(Bitmap kittenBitmap) {
        mKittenBitmap = kittenBitmap;
        mKittenImageView.setImageBitmap(mKittenBitmap);
    }

    @Override
    public void setDownloadProgressBarVisibility(boolean visible) {
        if (visible) {
            mProgress.start();
            mProgressCircleView.setVisibility(View.VISIBLE);
        } else {
            mProgress.stop();
            mProgressCircleView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showToast(int msgResId) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, msgResId, Toast.LENGTH_SHORT);
        mToast.show();
    }
}

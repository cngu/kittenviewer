package com.cngu.kittenviewer.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.widget.MyCircleImageView;
import android.support.v4.widget.MyMaterialProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.cngu.kittenviewer.ui.widget.InterceptingFrameLayout;

public class KittenPhotoActivity extends AppCompatActivity implements KittenPhotoView,
                                                                      ServiceConnection,
                                                                      UIThreadExecutor {
    private static final String BUNDLE_KEY_WIDTH = "cngu.bundle.key.WIDTH";
    private static final String BUNDLE_KEY_HEIGHT = "cngu.bundle.key.HEIGHT";
    private static final String PREF_KEY_SEARCHED_WIDTH = "cngu.bundle.key.SEARCHED_WIDTH";
    private static final String PREF_KEY_SEARCHED_HEIGHT = "cngu.bundle.key.SEARCHED_HEIGHT";

    private EditText mWidthEditText;
    private EditText mHeightEditText;
    private MyCircleImageView mProgressCircleView;
    private MyMaterialProgressDrawable mProgress;
    private ImageView mKittenImageView;

    private KittenPhotoPresenter mPresenter;

    private SharedPreferences mSharedPreferences;

    private Toast mToast;

    private Intent mServiceIntent;
    private boolean mServiceBound = false;

    private boolean mLoadedDefaultPhoto = false;

    private int getLastSearchedWidth() {
        int defaultWidth = getResources().getInteger(R.integer.default_width);
        return mSharedPreferences.getInt(PREF_KEY_SEARCHED_WIDTH, defaultWidth);
    }

    private int getLastSearchedHeight() {
        int defaultHeight = getResources().getInteger(R.integer.default_height);
        return mSharedPreferences.getInt(PREF_KEY_SEARCHED_HEIGHT, defaultHeight);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mServiceIntent = new Intent(this, ImageDownloadServiceImpl.class);

        // Initialize View and Presenter
        mPresenter = new KittenPhotoPresenterImpl(this, this);
        initializeView();

        if (savedInstanceState == null) {
            startService(mServiceIntent);
        } else {
            restoreViewState(savedInstanceState);
        }
    }

    private void initializeView() {
        setContentView(R.layout.activity_kitten_photo);

        // Use Toolbar as Action Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find views from layout
        InterceptingFrameLayout rootContainer = (InterceptingFrameLayout) findViewById(R.id.root_container);
        ImageView searchButton = (ImageView) findViewById(R.id.search_button);
        RelativeLayout photoContainer = (RelativeLayout) findViewById(R.id.photo_container);
        mWidthEditText = (EditText) findViewById(R.id.width_edittext);
        mHeightEditText = (EditText) findViewById(R.id.height_edittext);
        mKittenImageView = (ImageView) findViewById(R.id.kitten_imageview);

        // Set default widths and heights
        mWidthEditText.setText(Integer.toString(getLastSearchedWidth()));
        mHeightEditText.setText(Integer.toString(getLastSearchedHeight()));

        // Intercept touch events on all views to determine if we should close the OSK
        rootContainer.setOnInterceptTouchEventListener(new InterceptingFrameLayout.OnInterceptTouchEventListener() {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_UP) {
                    float x = ev.getRawX();
                    float y = ev.getRawY();
                    boolean insideWidthEditText = isPointInsideView(x, y, mWidthEditText);
                    boolean insideHeightEditText = isPointInsideView(x, y, mHeightEditText);
                    boolean widthEditTextFocus = mWidthEditText.hasFocus();
                    boolean heightEditTextFocus = mHeightEditText.hasFocus();

                    if (!insideWidthEditText && !insideHeightEditText) {
                        if (widthEditTextFocus) {
                            mWidthEditText.clearFocus();
                            hideKeyboard();
                        } else if (heightEditTextFocus) {
                            mHeightEditText.clearFocus();
                            hideKeyboard();
                        }
                    }
                }

                // Never consume the event; only intercept and pass it on
                return false;
            }
        });

        // Notify Presenter of the search button click event
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int searchedWidth = getEditTextValueAsInt(mWidthEditText);
                int searchedHeight = getEditTextValueAsInt(mHeightEditText);
                mSharedPreferences.edit().putInt(PREF_KEY_SEARCHED_WIDTH, searchedWidth).apply();
                mSharedPreferences.edit().putInt(PREF_KEY_SEARCHED_HEIGHT, searchedHeight).apply();

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
        String width = inState.getString(BUNDLE_KEY_WIDTH);
        String height = inState.getString(BUNDLE_KEY_HEIGHT);

        mWidthEditText.setText(width);
        mHeightEditText.setText(height);
    }

    private void saveViewState(Bundle outState) {
        String width = mWidthEditText.getText().toString();
        String height = mHeightEditText.getText().toString();

        outState.putString(BUNDLE_KEY_WIDTH, width);
        outState.putString(BUNDLE_KEY_HEIGHT, height);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ImageDownloadServiceImpl.ImageDownloadBinder binder =
                (ImageDownloadServiceImpl.ImageDownloadBinder) service;

        ImageDownloadService imageDownloadService = binder.getService();
        mServiceBound = true;

        mPresenter.onServiceConnected(imageDownloadService);
        if (!mLoadedDefaultPhoto) {
            mPresenter.onRequestPhotoRefresh();
            mLoadedDefaultPhoto = true;
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mServiceBound = false;
    }

    @Override
    public int getRequestedPhotoWidth() {
        return getLastSearchedWidth();
    }

    @Override
    public int getRequestedPhotoHeight() {
        return getLastSearchedHeight();
    }

    @Override
    public void setKittenPhoto(Bitmap kittenBitmap) {
        mKittenImageView.setImageBitmap(kittenBitmap);
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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    private boolean isPointInsideView(float x, float y, View view){
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        return (x > viewX && x < (viewX + view.getWidth())) &&
               (y > viewY && y < (viewY + view.getHeight()));
    }

    private int getEditTextValueAsInt(EditText editText) {
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            return -1;
        } else {
            return Integer.parseInt(text);
        }
    }
}

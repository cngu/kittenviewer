package com.cngu.kittenviewer.ui.presenter;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cngu.kittenviewer.data.listener.DownloadListener;
import com.cngu.kittenviewer.domain.service.ImageDownloadService;
import com.cngu.kittenviewer.ui.activity.UIThreadExecutor;
import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;
import com.cngu.kittenviewer.ui.view.KittenPhotoView;

public class KittenPhotoPresenterImpl implements KittenPhotoPresenter, DownloadListener<Bitmap> {
    private static final String TAG = "KittenPhotoPresenter";
    private static final boolean DEBUG = true;

    private KittenPhotoView mView;
    private UIThreadExecutor mUiThreadExecutor;
    private ImageDownloadService mImageDownloadService;

    public KittenPhotoPresenterImpl(KittenPhotoView view, UIThreadExecutor uiThreadExecutor) {
        mView = view;
        mUiThreadExecutor = uiThreadExecutor;
    }

    @Override
    public void onViewCreated() {
        downloadPlaceKittenPhoto();
    }

    @Override
    public void onServiceConnected(ImageDownloadService imageDownloadService) {
        mImageDownloadService = imageDownloadService;
    }

    @Override
    public void onSearchButtonClicked() {
        downloadPlaceKittenPhoto();
    }

    @Override
    public void onRequestedPhotoDimenChanged(int requestedPhotoWidth, int requestedPhotoHeight) {
        boolean validDimensions = requestedPhotoWidth > 0 && requestedPhotoHeight > 0;
        mView.setSearchButtonEnabled(validDimensions);
    }

    @Override
    public void onDownloadComplete(final Bitmap download) {
        if (DEBUG) {
            Log.i(TAG, String.format("Completed bitmap download of size: %dx%d",
                    download.getWidth(), download.getHeight()));
        }

        setDownloadProgressBarVisibility(false);
        setKittenBitmap(download);
    }

    @Override
    public void onDownloadMissing() {
        if (DEBUG) Log.i(TAG, "Placekitten did not return an image with the requested dimensions");

        setDownloadProgressBarVisibility(false);
    }

    @Override
    public void onDownloadError() {
        if (DEBUG) Log.i(TAG, "Placekitten returned an error");

        setDownloadProgressBarVisibility(false);
    }

    @Override
    public void onNetworkConnectionLost() {
        if (DEBUG) Log.i(TAG, "WiFi connection was lost during download");

        setDownloadProgressBarVisibility(false);
    }

    private void downloadPlaceKittenPhoto() {
        setDownloadProgressBarVisibility(true);

        int reqWidth = mView.getRequestedPhotoWidth();
        int reqHeight = mView.getRequestedPhotoHeight();
        PlaceKittenArgs args = new PlaceKittenArgs(reqWidth, reqHeight);

        mImageDownloadService.downloadBitmap(args, this);
    }

    private void setDownloadProgressBarVisibility(final boolean visible) {
        mUiThreadExecutor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.setDownloadProgressBarVisibility(visible);
            }
        });
    }

    private void setKittenBitmap(final Bitmap bitmap) {
        mUiThreadExecutor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.setKittenBitmap(bitmap);
            }
        });
    }
}

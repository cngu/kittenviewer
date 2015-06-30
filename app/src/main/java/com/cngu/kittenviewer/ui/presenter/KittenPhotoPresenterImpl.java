package com.cngu.kittenviewer.ui.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import com.cngu.kittenviewer.R;
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
            Log.i(TAG, String.format("Received bitmap of size: %dx%d",
                    download.getWidth(), download.getHeight()));
        }

        showDownloadProgress(false, 0);
        showKittenPhoto(download);
    }

    @Override
    public void onDownloadMissing() {
        if (DEBUG) Log.i(TAG, "Placekitten did not return an image with the requested dimensions");

        showDownloadProgress(false, R.string.msg_placekitten_missing_photo);
    }

    @Override
    public void onDownloadError() {
        if (DEBUG) Log.i(TAG, "Placekitten returned an error");

        showDownloadProgress(false, R.string.msg_placekitten_error);
    }

    @Override
    public void onNetworkConnectionLost() {
        if (DEBUG) Log.i(TAG, "WiFi connection was lost during download");

        showDownloadProgress(false, R.string.msg_not_connected_to_wifi);
    }

    private void downloadPlaceKittenPhoto() {
        int reqWidth = mView.getRequestedPhotoWidth();
        int reqHeight = mView.getRequestedPhotoHeight();
        if (reqWidth <= 0 || reqHeight <= 0) {
            Log.e(TAG, "Can not download photo with invalid size dimensions <=0");
            return;
        }

        showDownloadProgress(true, 0);

        PlaceKittenArgs args = new PlaceKittenArgs(reqWidth, reqHeight);
        mImageDownloadService.downloadBitmap(args, this);
    }

    private void showDownloadProgress(final boolean progressBarVisible, final int msgResId) {
        mUiThreadExecutor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.setDownloadProgressBarVisibility(progressBarVisible);

                if (msgResId > 0) {
                    // Valid resources ids are always positive
                    mView.showToast(msgResId);
                }
            }
        });
    }

    private void showKittenPhoto(final Bitmap bitmap) {
        mUiThreadExecutor.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.setKittenPhoto(bitmap);
            }
        });
    }
}

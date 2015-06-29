package com.cngu.kittenviewer.ui.presenter;

import android.util.Log;

import com.cngu.kittenviewer.domain.service.ImageDownloadService;
import com.cngu.kittenviewer.ui.view.KittenPhotoView;

public class KittenPhotoPresenterImpl implements KittenPhotoPresenter {
    private static final String TAG = "KittenPhotoPresenter";
    private static final boolean DEBUG = true;

    private KittenPhotoView mView;
    private ImageDownloadService mImageDownloadService;

    public KittenPhotoPresenterImpl(KittenPhotoView view) {
        mView = view;
    }

    @Override
    public void onViewCreated() {
        int reqWidth = mView.getRequestedPhotoWidth();
        int reqHeight = mView.getRequestedPhotoHeight();

        refreshSearchButtonState(reqWidth, reqHeight);
    }

    @Override
    public void onServiceConnected(ImageDownloadService imageDownloadService) {
        mImageDownloadService = imageDownloadService;
    }

    @Override
    public void onSearchButtonClicked() {
        int reqWidth = mView.getRequestedPhotoWidth();
        int reqHeight = mView.getRequestedPhotoHeight();

        if (DEBUG) Log.i(TAG, String.format("User requested size: %dx%d", reqWidth, reqHeight));

        mImageDownloadService.test();
    }

    @Override
    public void onRequestedPhotoDimenChanged(int requestedPhotoWidth, int requestedPhotoHeight) {
        refreshSearchButtonState(requestedPhotoWidth, requestedPhotoHeight);
    }

    private void refreshSearchButtonState(int requestedPhotoWidth, int requestedPhotoHeight) {
        boolean validDimensions = requestedPhotoWidth > 0 && requestedPhotoHeight > 0;
        mView.setSearchButtonEnabled(validDimensions);
    }
}

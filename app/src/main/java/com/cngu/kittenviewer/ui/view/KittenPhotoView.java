package com.cngu.kittenviewer.ui.view;

import android.graphics.Bitmap;

public interface KittenPhotoView {
    int getRequestedPhotoWidth();

    int getRequestedPhotoHeight();

    void setSearchButtonEnabled(boolean enabled);

    void setKittenPhoto(Bitmap kittenBitmap);

    void setDownloadProgressBarVisibility(boolean visible);

    void showToast(int msgResId);
}

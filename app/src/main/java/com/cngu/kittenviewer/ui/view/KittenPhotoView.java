package com.cngu.kittenviewer.ui.view;

import android.graphics.Bitmap;

public interface KittenPhotoView {
    int getWidth();

    int getHeight();

    void setKittenBitmap(Bitmap kittenBitmap);
}

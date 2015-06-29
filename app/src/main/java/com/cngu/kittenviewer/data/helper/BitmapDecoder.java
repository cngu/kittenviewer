package com.cngu.kittenviewer.data.helper;

import android.graphics.Bitmap;

import java.io.InputStream;

public interface BitmapDecoder {
    void setRequestedBitmapSize(int requestedWidth, int requestedHeight);

    Bitmap decodeBitmap(InputStream is);

    void cancelDecode();
}

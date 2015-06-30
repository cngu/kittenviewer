package com.cngu.kittenviewer.domain.service;

import android.graphics.Bitmap;

import com.cngu.kittenviewer.data.listener.DownloadListener;
import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

public interface ImageDownloadService {
    void downloadBitmap(PlaceKittenArgs args, DownloadListener<Bitmap> downloadListener);
}

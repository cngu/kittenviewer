package com.cngu.kittenviewer.domain.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ImageDownloadServiceImpl extends Service implements ImageDownloadService {
    private static final String TAG = "ImageDownloadService";
    private static final boolean DEBUG = true;

    private final IBinder mBinder = new ImageDownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void test() {
        Log.d(TAG, "CALLED BOUND METHOD");
    }

    public class ImageDownloadBinder extends Binder {
        public ImageDownloadServiceImpl getService() {
            return ImageDownloadServiceImpl.this;
        }
    }
}

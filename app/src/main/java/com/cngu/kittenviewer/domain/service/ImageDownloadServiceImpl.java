package com.cngu.kittenviewer.domain.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cngu.kittenviewer.data.dao.PlaceKittenBitmapDao;
import com.cngu.kittenviewer.data.dao.PlaceKittenBitmapDaoImpl;
import com.cngu.kittenviewer.data.listener.DownloadListener;
import com.cngu.kittenviewer.domain.task.PlaceKittenDownloadTask;
import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

public class ImageDownloadServiceImpl extends Service implements ImageDownloadService,
                                                                 DownloadListener<Bitmap> {
    private static final String TAG = "ImageDownloadService";
    private static final boolean DEBUG = true;

    private final IBinder mBinder = new ImageDownloadBinder();

    private ConnectivityManager mConnectivityManager;
    private PlaceKittenBitmapDao mPlaceKittenDao;

    private PlaceKittenDownloadTask mDownloadTask;
    private DownloadListener<Bitmap> mClientDownloadListener;

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.i(TAG, "onCreate");

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mPlaceKittenDao = new PlaceKittenBitmapDaoImpl();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.i(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG) Log.i(TAG, "onDestroy");
    }

    @Override
    public void downloadBitmap(PlaceKittenArgs args, DownloadListener<Bitmap> downloadListener) {
        if (DEBUG) Log.i(TAG, "Beginning bitmap download with args: " + args);
        mClientDownloadListener = downloadListener;

        if (mDownloadTask != null) {
            if (mDownloadTask.getArgs().equals(args)) {
                if (DEBUG) Log.i(TAG, "A download for the requested bitmap is already occurring; ignoring request");
                return;
            } else {
                if (DEBUG) Log.i(TAG, "A download for a different bitmap is already occurring; cancelling it");
                mDownloadTask.cancel();
            }
        }

        mDownloadTask = new PlaceKittenDownloadTask(mPlaceKittenDao, args, this, mConnectivityManager);
        new Thread(mDownloadTask).start();
    }

    @Override
    public void onDownloadComplete(Bitmap download) {
        mClientDownloadListener.onDownloadComplete(download);
        mDownloadTask = null;
    }

    @Override
    public void onDownloadMissing() {
        mClientDownloadListener.onDownloadMissing();
        mDownloadTask = null;
    }

    @Override
    public void onDownloadError() {
        mClientDownloadListener.onDownloadError();
        mDownloadTask = null;
    }

    @Override
    public void onNetworkConnectionLost() {
        mClientDownloadListener.onNetworkConnectionLost();
        mDownloadTask = null;
    }

    public class ImageDownloadBinder extends Binder {
        public ImageDownloadServiceImpl getService() {
            return ImageDownloadServiceImpl.this;
        }
    }
}

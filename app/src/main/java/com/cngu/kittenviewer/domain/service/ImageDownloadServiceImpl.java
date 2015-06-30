package com.cngu.kittenviewer.domain.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cngu.kittenviewer.data.dao.PlaceKittenBitmapCache;
import com.cngu.kittenviewer.data.dao.PlaceKittenBitmapCacheImpl;
import com.cngu.kittenviewer.data.dao.PlaceKittenBitmapDao;
import com.cngu.kittenviewer.data.dao.PlaceKittenBitmapDaoImpl;
import com.cngu.kittenviewer.data.listener.DownloadListener;
import com.cngu.kittenviewer.domain.task.PlaceKittenDownloadTask;
import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

import java.lang.ref.WeakReference;

public class ImageDownloadServiceImpl extends Service implements ImageDownloadService,
                                                                 DownloadListener<Bitmap> {
    private static final String TAG = "ImageDownloadService";
    private static final boolean DEBUG = true;

    private final IBinder mBinder = new ImageDownloadBinder();

    private ConnectivityManager mConnectivityManager;
    private PlaceKittenBitmapDao mPlaceKittenDao;
    private PlaceKittenBitmapCache mBitmapCache;

    private PlaceKittenDownloadTask mDownloadTask;
    private WeakReference<DownloadListener<Bitmap>> mClientDownloadListener;

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.i(TAG, "onCreate");

        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mPlaceKittenDao = new PlaceKittenBitmapDaoImpl();
        mBitmapCache = new PlaceKittenBitmapCacheImpl();
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
        if (DEBUG) Log.i(TAG, "Received request to download bitmap with args: " + args);
        mClientDownloadListener = new WeakReference<>(downloadListener);

        Bitmap cachedBitmap = mBitmapCache.get(args);
        if (cachedBitmap != null) {
            if (DEBUG) Log.i(TAG, "Found requested bitmap in cache; not going to re-download it");
            notifyClientOfCompletedDownload(cachedBitmap);
            return;
        }

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
        PlaceKittenArgs downloadArgs = mDownloadTask.getArgs();
        mBitmapCache.put(downloadArgs, download);

        notifyClientOfCompletedDownload(download);
        mDownloadTask = null;
    }

    private void notifyClientOfCompletedDownload(Bitmap download) {
        DownloadListener<Bitmap> listener = mClientDownloadListener.get();
        if (listener != null) {
            listener.onDownloadComplete(download);
        } else {
            if (DEBUG) Log.e(TAG, "Client died; failed to return bitmap");
        }
    }

    @Override
    public void onDownloadMissing() {
        DownloadListener<Bitmap> listener = mClientDownloadListener.get();
        if (listener != null) {
            listener.onDownloadMissing();
        } else {
            if (DEBUG) Log.e(TAG, "Client died; failed to notify client of missing download");
        }

        mDownloadTask = null;
    }

    @Override
    public void onDownloadError() {
        DownloadListener<Bitmap> listener = mClientDownloadListener.get();
        if (listener != null) {
            listener.onDownloadError();
        } else {
            if (DEBUG) Log.e(TAG, "Client died; failed to notify client of download error");
        }

        mDownloadTask = null;
    }

    @Override
    public void onNetworkConnectionLost() {
        DownloadListener<Bitmap> listener = mClientDownloadListener.get();
        if (listener != null) {
            listener.onNetworkConnectionLost();
        } else {
            if (DEBUG) Log.e(TAG, "Client died; failed to notify client of lost network connection");
        }

        mDownloadTask = null;
    }

    public class ImageDownloadBinder extends Binder {
        public ImageDownloadServiceImpl getService() {
            return ImageDownloadServiceImpl.this;
        }
    }
}

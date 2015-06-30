package com.cngu.kittenviewer.domain.task;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.cngu.kittenviewer.data.helper.BitmapDecoder;
import com.cngu.kittenviewer.data.helper.BitmapDecoderImpl;
import com.cngu.kittenviewer.data.dao.PlaceKittenBitmapDao;
import com.cngu.kittenviewer.data.listener.DownloadListener;
import com.cngu.kittenviewer.exception.PlaceKittenErrorException;
import com.cngu.kittenviewer.exception.PlaceKittenMissingPhotoException;
import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

import java.io.IOException;

public class PlaceKittenDownloadTask implements Runnable {
    private static final String TAG = "PlaceKittenDownloadTask";
    private static final boolean DEBUG = true;

    private PlaceKittenBitmapDao mPlaceKittenDao;
    private PlaceKittenArgs mPlaceKittenArgs;
    private volatile DownloadListener<Bitmap> mDownloadListener;
    private ConnectivityManager mConnectivityManager;

    private BitmapDecoder mBitmapDecoder;

    public PlaceKittenDownloadTask(PlaceKittenBitmapDao dao,
                                   PlaceKittenArgs args,
                                   DownloadListener<Bitmap> listener,
                                   ConnectivityManager connectivityManager) {
        mPlaceKittenDao = dao;
        mPlaceKittenArgs = args;
        mDownloadListener = listener;
        mConnectivityManager = connectivityManager;

        mBitmapDecoder = new BitmapDecoderImpl();
    }

    public PlaceKittenArgs getArgs() {
        return mPlaceKittenArgs;
    }

    @Override
    public void run() {
        if (isCancelled()) {
            return;
        }

        try {
            Bitmap bitmap = mPlaceKittenDao.getBitmap(mPlaceKittenArgs, mBitmapDecoder);

            if (!isCancelled()) {
                mDownloadListener.onDownloadComplete(bitmap);
            }
        } catch (PlaceKittenMissingPhotoException pkmpe) {
            if (!isCancelled()) {
                mDownloadListener.onDownloadMissing();
            }
        } catch (PlaceKittenErrorException pkee) {
            if (!isCancelled()) {
                mDownloadListener.onDownloadError();
            }
        } catch (IOException ioe) {
            if (!isCancelled()) {
                if (connectedToWifi()) {
                    mDownloadListener.onDownloadError();
                } else {
                    mDownloadListener.onNetworkConnectionLost();
                }
            }
        }
    }

    public void cancel() {
        mDownloadListener = null;
        mBitmapDecoder.cancelDecode();

        if (DEBUG) Log.i(TAG, "Cancelled task with args: " + mPlaceKittenArgs);
    }

    private boolean isCancelled() {
        return mDownloadListener == null;
    }

    private boolean connectedToWifi() {
        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        return isConnected && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
    }


}

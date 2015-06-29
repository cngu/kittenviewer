package com.cngu.kittenviewer.domain.task;

import com.cngu.kittenviewer.data.helper.PlaceKittenBitmapDao;
import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

public class PlaceKittenDownloadTask implements Runnable {

    private PlaceKittenBitmapDao mPlaceKittenDao;
    private PlaceKittenArgs mPlaceKittenArgs;
    private DownloadListener mDownloadListener;

    public PlaceKittenDownloadTask(PlaceKittenBitmapDao dao, PlaceKittenArgs args,
                                   DownloadListener listener) {
        mPlaceKittenDao = dao;
        mPlaceKittenArgs= args;
        mDownloadListener = listener;
    }

    @Override
    public void run() {

    }

    public interface DownloadListener {

    }
}

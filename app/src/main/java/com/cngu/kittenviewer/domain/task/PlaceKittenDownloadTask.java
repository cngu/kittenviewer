package com.cngu.kittenviewer.domain.task;

import com.cngu.kittenviewer.data.helper.BitmapDecoder;
import com.cngu.kittenviewer.data.helper.BitmapDecoderImpl;
import com.cngu.kittenviewer.data.dao.PlaceKittenBitmapDao;
import com.cngu.kittenviewer.exception.PlaceKittenErrorException;
import com.cngu.kittenviewer.exception.PlaceKittenMissingPhotoException;
import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

import java.io.IOException;

public class PlaceKittenDownloadTask implements Runnable {

    private PlaceKittenBitmapDao mPlaceKittenDao;
    private PlaceKittenArgs mPlaceKittenArgs;
    private volatile DownloadListener mDownloadListener;

    private BitmapDecoder mBitmapDecoder;

    public PlaceKittenDownloadTask(PlaceKittenBitmapDao dao, PlaceKittenArgs args,
                                   DownloadListener listener) {
        mPlaceKittenDao = dao;
        mPlaceKittenArgs= args;
        mDownloadListener = listener;

        mBitmapDecoder = new BitmapDecoderImpl();
    }

    @Override
    public void run() {
        try {
            mPlaceKittenDao.getBitmap(mPlaceKittenArgs, mBitmapDecoder);
        } catch (PlaceKittenErrorException e) {
            e.printStackTrace();
        } catch (PlaceKittenMissingPhotoException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        mDownloadListener = null;
        mBitmapDecoder.cancelDecode();
    }

    public interface DownloadListener {

    }
}

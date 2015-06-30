package com.cngu.kittenviewer.data.dao;

import android.graphics.Bitmap;

import com.cngu.kittenviewer.data.helper.BitmapDecoder;
import com.cngu.kittenviewer.data.helper.PlaceKittenUrlGeneratorImpl;
import com.cngu.kittenviewer.exception.PlaceKittenErrorException;
import com.cngu.kittenviewer.exception.PlaceKittenMissingPhotoException;
import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlaceKittenBitmapDaoImpl implements PlaceKittenBitmapDao {

    @Override
    public Bitmap getBitmap(PlaceKittenArgs args, BitmapDecoder decoder)
            throws PlaceKittenErrorException, PlaceKittenMissingPhotoException, IOException {

        int width = args.getWidth();
        int height = args.getHeight();
        decoder.setRequestedBitmapSize(width, height);

        InputStream is = null;
        try {
            // Generate placekitten URL and open a connection
            PlaceKittenUrlGeneratorImpl requestGenerator = new PlaceKittenUrlGeneratorImpl();
            URL url = requestGenerator.generateRequest(args);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // throw if placekitten returned an error response code
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new PlaceKittenErrorException(responseCode);
            }

            // throw if placekitten responded with 200, but did not return an image
            int contentLength = connection.getContentLength();
            if (contentLength == 0) {
                throw new PlaceKittenMissingPhotoException();
            }

            is = connection.getInputStream();
            return decoder.decodeBitmap(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}

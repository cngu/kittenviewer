package com.cngu.kittenviewer.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cngu.kittenviewer.data.helper.PlaceKittenBitmapDao;
import com.cngu.kittenviewer.data.helper.PlaceKittenRequestGenerator;
import com.cngu.kittenviewer.exception.PlaceKittenErrorException;
import com.cngu.kittenviewer.exception.PlaceKittenMissingPhotoException;
import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlaceKittenBitmapDaoImpl implements PlaceKittenBitmapDao {

    private static final int MAX_TEXTURE_SIZE = 2048;

    private PlaceKittenRequestGenerator mRequestGenerator;

    public PlaceKittenBitmapDaoImpl(PlaceKittenRequestGenerator requestGenerator) {
        mRequestGenerator = requestGenerator;
    }

    @Override
    public Bitmap getBitmap(PlaceKittenArgs args) throws PlaceKittenErrorException,
                                                         PlaceKittenMissingPhotoException,
                                                         IOException {
        int width = args.getWidth();
        int height = args.getHeight();
        BitmapFactory.Options options = createBitmapDecodeOptions(width, height);

        InputStream is = null;
        try {
            URL url = mRequestGenerator.generateRequest(args);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new PlaceKittenErrorException(responseCode);
            }

            int contentLength = connection.getContentLength();
            if (contentLength == 0) {
                throw new PlaceKittenMissingPhotoException();
            }

            is = connection.getInputStream();

            return BitmapFactory.decodeStream(is, null, options);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private BitmapFactory.Options createBitmapDecodeOptions(int requestedWidth, int requestedHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = calculateSampleSize(requestedWidth, requestedHeight);

        return options;
    }

    /**
     * @param requestedWidth Width of the bitmap download
     * @param requestedHeight Height of the bitmap download
     * @return The minimal sample size to use when decoding such that {@code requestedWidth} and
     *         {code requestedHeight} stay within {@link PlaceKittenBitmapDaoImpl#MAX_TEXTURE_SIZE}.
     */
    private int calculateSampleSize(int requestedWidth, int requestedHeight) {
        int sampleSize = 1;
        while (requestedWidth/sampleSize > MAX_TEXTURE_SIZE ||
               requestedHeight/sampleSize > MAX_TEXTURE_SIZE) {
            sampleSize <<= 1;
        }

        return sampleSize;
    }
}

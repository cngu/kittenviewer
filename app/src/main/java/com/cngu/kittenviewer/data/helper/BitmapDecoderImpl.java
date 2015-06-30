package com.cngu.kittenviewer.data.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;

public class BitmapDecoderImpl implements BitmapDecoder {
    private static final String TAG = "BitmapDecoderImpl";
    private static final boolean DEBUG = true;

    private static final int MAX_TEXTURE_SIZE = 2048;

    private BitmapFactory.Options mDecodeOptions;

    public BitmapDecoderImpl() {
        mDecodeOptions = new BitmapFactory.Options();
    }

    @Override
    public void setRequestedBitmapSize(int requestedWidth, int requestedHeight) {
        int sampleSize = calculateSampleSize(requestedWidth, requestedHeight);

        if (DEBUG) {
            Log.i(TAG, String.format("Using sample size of %d: (%dx%d) > (%dx%d)",
                    sampleSize, requestedWidth, requestedHeight,
                    requestedWidth/sampleSize, requestedHeight/sampleSize));
        }

        mDecodeOptions.inSampleSize = sampleSize;
    }

    @Override
    public Bitmap decodeBitmap(InputStream is) {
        if (DEBUG) {
            Log.i(TAG, "Decoding placekitten image to Bitmap...");
        }
        return BitmapFactory.decodeStream(is, null, mDecodeOptions);
    }

    @Override
    public void cancelDecode() {
        mDecodeOptions.requestCancelDecode();
    }

    /**
     * @param requestedWidth Width of the bitmap download
     * @param requestedHeight Height of the bitmap download
     * @return The minimal sample size to use when decoding such that {@code requestedWidth} and
     *         {code requestedHeight} stay within {@link BitmapDecoderImpl#MAX_TEXTURE_SIZE}.
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

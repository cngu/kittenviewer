package com.cngu.kittenviewer.data.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

public class BitmapDecoderImpl implements BitmapDecoder {

    private static final int MAX_TEXTURE_SIZE = 2048;

    private BitmapFactory.Options mDecodeOptions;

    public BitmapDecoderImpl() {
        mDecodeOptions = new BitmapFactory.Options();
    }

    @Override
    public void setRequestedBitmapSize(int requestedWidth, int requestedHeight) {
        mDecodeOptions.inSampleSize = calculateSampleSize(requestedWidth, requestedHeight);
    }

    @Override
    public Bitmap decodeBitmap(InputStream is) {
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

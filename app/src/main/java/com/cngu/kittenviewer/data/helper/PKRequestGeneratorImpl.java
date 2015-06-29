package com.cngu.kittenviewer.data.helper;

import android.util.Log;

import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

import java.net.MalformedURLException;
import java.net.URL;

public class PKRequestGeneratorImpl implements PKRequestGenerator {
    private static final String TAG = "PKRequestGenerator";
    public static final String ENDPOINT = "http://placekitten.com";
    private static final String QUERY_ARGS = "/g/%d/%d";

    @Override
    public URL generateRequest(PlaceKittenArgs args) {
        URL result = null;
        String request = null;

        try {
            int width = args.getWidth();
            int height = args.getHeight();
            request = ENDPOINT + String.format(QUERY_ARGS, width, height);
            result = new URL(request);
        } catch (MalformedURLException mue) {
            Log.e(TAG, "Failed to parse placekitten URL: " + request, mue);
        }

        return result;
    }
}

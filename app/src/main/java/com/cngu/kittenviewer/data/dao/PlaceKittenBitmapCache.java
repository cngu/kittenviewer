package com.cngu.kittenviewer.data.dao;

import android.graphics.Bitmap;

import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

public interface PlaceKittenBitmapCache {
    Bitmap get(PlaceKittenArgs key);

    void put(PlaceKittenArgs key, Bitmap value);
}

package com.cngu.kittenviewer.data.dao;

import android.graphics.Bitmap;

import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

import java.lang.ref.SoftReference;

public class PlaceKittenBitmapCacheImpl implements PlaceKittenBitmapCache {

    private PlaceKittenArgs mKey;
    private SoftReference<Bitmap> mValue;

    @Override
    public Bitmap get(PlaceKittenArgs key) {
        if (mKey != null && mKey.equals(key)) {
            return mValue.get();
        }
        return null;
    }

    @Override
    public void put(PlaceKittenArgs key, Bitmap value) {
        mKey = key;
        mValue = new SoftReference<>(value);
    }
}

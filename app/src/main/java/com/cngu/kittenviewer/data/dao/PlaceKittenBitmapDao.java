package com.cngu.kittenviewer.data.dao;

import android.graphics.Bitmap;

import com.cngu.kittenviewer.data.helper.BitmapDecoder;
import com.cngu.kittenviewer.exception.PlaceKittenErrorException;
import com.cngu.kittenviewer.exception.PlaceKittenMissingPhotoException;
import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

import java.io.IOException;

public interface PlaceKittenBitmapDao {
    Bitmap getBitmap(PlaceKittenArgs args, BitmapDecoder decoder) throws PlaceKittenErrorException,
                                                                         PlaceKittenMissingPhotoException,
                                                                         IOException;
}

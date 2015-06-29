package com.cngu.kittenviewer.ui.model;

public class PlaceKittenArgs {
    private int mWidth;
    private int mHeight;

    public PlaceKittenArgs(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("width must be positive");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be positive");
        }

        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
}

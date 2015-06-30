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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlaceKittenArgs that = (PlaceKittenArgs) o;

        return(mWidth == that.mWidth) &&
              (mHeight == that.mHeight);
    }

    @Override
    public int hashCode() {
        int result = mWidth;
        result = 31 * result + mHeight;
        return result;
    }

    @Override
    public String toString() {
        return String.format("PlaceKittenArgs{width:%d, height:%d}", mWidth, mHeight);
    }
}

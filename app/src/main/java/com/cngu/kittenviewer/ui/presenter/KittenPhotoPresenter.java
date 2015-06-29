package com.cngu.kittenviewer.ui.presenter;

public interface KittenPhotoPresenter {
    void onCreate();

    void onSearchButtonClicked();

    void onRequestedPhotoDimenChanged(int requestedPhotoWidth, int requestedPhotoHeight);
}

package com.cngu.kittenviewer.ui.presenter;

import com.cngu.kittenviewer.domain.service.ImageDownloadService;

public interface KittenPhotoPresenter {
    void onViewCreated();

    void onSearchButtonClicked();

    void onRequestedPhotoDimenChanged(int requestedPhotoWidth, int requestedPhotoHeight);

    void onServiceConnected(ImageDownloadService imageDownloadService);
}

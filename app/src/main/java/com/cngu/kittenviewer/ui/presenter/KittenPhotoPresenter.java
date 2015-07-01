package com.cngu.kittenviewer.ui.presenter;

import com.cngu.kittenviewer.domain.service.ImageDownloadService;

public interface KittenPhotoPresenter {
    void onViewCreated();

    void onSearchButtonClicked();

    void onServiceConnected(ImageDownloadService imageDownloadService);
}

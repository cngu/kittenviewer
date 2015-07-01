package com.cngu.kittenviewer.ui.presenter;

import com.cngu.kittenviewer.domain.service.ImageDownloadService;

public interface KittenPhotoPresenter {
    void onSearchButtonClicked();

    void onRequestPhotoRefresh();

    void onServiceConnected(ImageDownloadService imageDownloadService);
}

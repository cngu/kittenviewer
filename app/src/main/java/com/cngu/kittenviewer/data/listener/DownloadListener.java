package com.cngu.kittenviewer.data.listener;

public interface DownloadListener<T> {
    void onDownloadComplete(T download);
    void onDownloadMissing();
    void onDownloadError();
    void onNetworkConnectionLost();
}

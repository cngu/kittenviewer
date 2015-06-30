package com.cngu.kittenviewer.ui.activity;

public interface UIThreadExecutor {
    void runOnUiThread(Runnable action);
}

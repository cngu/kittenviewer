package com.cngu.kittenviewer.data.helper;

import com.cngu.kittenviewer.ui.model.PlaceKittenArgs;

import java.net.URL;

public interface PlaceKittenUrlGenerator {
    URL generateRequest(PlaceKittenArgs args);
}

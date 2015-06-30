package com.cngu.kittenviewer.exception;

/**
 * Exception thrown when http://placekitten.com responds with a non-OK (non-200) response code.
 */
public class PlaceKittenErrorException extends Exception {
    public PlaceKittenErrorException() {
        this("Encountered unknown error while accessing placekitten");
    }

    public PlaceKittenErrorException(String detailMessage) {
        super(detailMessage);
    }

    public PlaceKittenErrorException(int responseCode) {
        super("Received error response from placekitten: " + responseCode);
    }
}

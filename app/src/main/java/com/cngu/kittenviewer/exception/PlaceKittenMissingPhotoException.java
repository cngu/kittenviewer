package com.cngu.kittenviewer.exception;

/**
 * Exception thrown when http://placekitten.com responds with an OK (200) response code, but does
 * not include an image in the response body.
 */
public class PlaceKittenMissingPhotoException extends Exception {
}

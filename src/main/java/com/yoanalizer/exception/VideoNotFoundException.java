package com.yoanalizer.exception;

public class VideoNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -7373563792624529978L;

    public VideoNotFoundException(String videoId){
        super("Video not found: " + videoId);
    }
}

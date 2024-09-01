package com.videostreaming.app.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoNotFoundException extends RuntimeException {
    private String message;
}

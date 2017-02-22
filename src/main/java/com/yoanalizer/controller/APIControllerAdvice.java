package com.yoanalizer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yoanalizer.model.DefaultResponse;

@ControllerAdvice(annotations = RestController.class)
public class APIControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(APIControllerAdvice.class);

    private static HttpStatus getHttpStatus(Exception e) {

        final ResponseStatus responseStatus =
                AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);

        if (responseStatus == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        final HttpStatus status = responseStatus.value();
        if (status == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            return status;
        }
    }

    @ExceptionHandler
    public ResponseEntity<DefaultResponse> handleException(Exception e) {
        final HttpStatus status = getHttpStatus(e);
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            logger.error(e.getMessage(), e);
        } else {
            logger.debug(e.getMessage(), e);
        }

        return new ResponseEntity<>(new DefaultResponse(e.getMessage()), status);
    }
}

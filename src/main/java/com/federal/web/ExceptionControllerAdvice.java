package com.federal.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorDetails> exceptionNotFound(FileNotFoundException ex) {
        ErrorDetails errorDerails = new ErrorDetails();
        errorDerails.setMessage(ex.getMessage());
        return ResponseEntity.badRequest()
                .body(errorDerails);
    }

}

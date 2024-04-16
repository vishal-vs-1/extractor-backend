package com.tool.reg.exception.handler;

import com.tool.reg.dto.ApiError;
import com.tool.reg.exception.NoDataException;
import com.tool.reg.exception.PdfGenerationException;
import com.tool.reg.exception.TextExtractionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.net.MalformedURLException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TextExtractionException.class)
    public ResponseEntity<Object> handleTextExtractionException(TextExtractionException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<Object> handleMalformedURLException(MalformedURLException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoDataException.class)
    public ResponseEntity<Object> handleNoDataException(NoDataException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<Object> handlePdfGenerationException(PdfGenerationException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(IOException ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception ex, HttpStatus status) {
        return new ResponseEntity<>(new ApiError(status.value(), ex.getMessage()), status);
    }
}



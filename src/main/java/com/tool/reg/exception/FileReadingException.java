package com.tool.reg.exception;

public class FileReadingException extends RuntimeException{

    public FileReadingException(String message) {
        super(message);
    }

    public FileReadingException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.tool.reg.exception;

public class NoDataException extends RuntimeException{

    public NoDataException(String message) {
        super(message);
    }

    public NoDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

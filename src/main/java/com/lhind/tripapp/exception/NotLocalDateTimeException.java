package com.lhind.tripapp.exception;

public class NotLocalDateTimeException extends RuntimeException{
    public NotLocalDateTimeException() {
    }

    public NotLocalDateTimeException(String message) {
        super(message);
    }

    public NotLocalDateTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotLocalDateTimeException(Throwable cause) {
        super(cause);
    }

    public NotLocalDateTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

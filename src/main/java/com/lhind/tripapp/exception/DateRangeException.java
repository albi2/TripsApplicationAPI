package com.lhind.tripapp.exception;

public class DateRangeException extends RuntimeException{
    public DateRangeException() {
    }

    public DateRangeException(String message) {
        super(message);
    }

    public DateRangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DateRangeException(Throwable cause) {
        super(cause);
    }

    public DateRangeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

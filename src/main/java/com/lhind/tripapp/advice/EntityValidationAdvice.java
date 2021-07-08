package com.lhind.tripapp.advice;

import com.lhind.tripapp.exception.DateRangeException;
import com.lhind.tripapp.exception.NotLocalDateTimeException;
import com.lhind.tripapp.exception.RefreshTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class EntityValidationAdvice {
    @ExceptionHandler(value = NotLocalDateTimeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage handleTokenRefreshException(NotLocalDateTimeException e, WebRequest request) {
        return new ErrorMessage(HttpStatus.FORBIDDEN.value(),
                new Date(),
                e.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(value = DateRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleDateRangeException(DateRangeException e, WebRequest request) {
        return new ErrorMessage(HttpStatus.FORBIDDEN.value(),
                new Date(),
                e.getMessage(),
                request.getDescription(false));
    }
}

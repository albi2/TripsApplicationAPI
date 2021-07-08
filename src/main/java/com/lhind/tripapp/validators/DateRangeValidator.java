package com.lhind.tripapp.validators;

import com.lhind.tripapp.exception.DateRangeException;
import com.lhind.tripapp.exception.NotLocalDateTimeException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final DateRange constraintAnnotation)
    {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            Field first = value.getClass().getDeclaredField(this.firstFieldName);
            Field second = value.getClass().getDeclaredField(this.secondFieldName);
            first.setAccessible(true);
            second.setAccessible(true);
            if(!(first.get(value) instanceof  LocalDateTime)) {
                throw new NotLocalDateTimeException("The annotation required that the fields are LocalDateTime, Field: "+ this.firstFieldName);
            }
            if(!(second.get(value) instanceof  LocalDateTime)) {
                throw new NotLocalDateTimeException("The annotation required that the fields are LocalDateTime, Field: " + this.secondFieldName);
            }
            LocalDateTime fromDate = (LocalDateTime) first.get(value);
            LocalDateTime toDate = (LocalDateTime) second.get(value);
            if(fromDate.isAfter(toDate))
                throw new DateRangeException("Date from field "+ this.firstFieldName + " must not be after date from field " + secondFieldName);

            return true;
        } catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch(NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

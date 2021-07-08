package com.lhind.tripapp.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
@Documented
public @interface DateRange {
    // Anotacion
    // Mesazhi
    String message() default "{constraints.daterange}";
    // Grupet
    Class<?>[] groups() default {};

    // Payload
    Class<? extends Payload>[] payload() default {};

    /**
     * @return The first field
     */
    String first();

    /**
     * @return The second field
     */
    String second();

}

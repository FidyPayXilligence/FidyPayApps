package com.fidypay.utils.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author prave
 * @Date 09-10-2023
 */
@Documented
@Constraint(validatedBy = UppercaseValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Uppercase {

    String message() default "API Name must be in CAPITAL letters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

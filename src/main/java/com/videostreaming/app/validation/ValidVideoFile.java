package com.videostreaming.app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VideoFileValidator.class)
public @interface ValidVideoFile {
    String message() default "Invalid video file";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

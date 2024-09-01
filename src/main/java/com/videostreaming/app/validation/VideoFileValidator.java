package com.videostreaming.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class VideoFileValidator implements ConstraintValidator<ValidVideoFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File cannot be empty")
                    .addConstraintViolation();
            return false;
        }

        // Check file size (e.g., max 1000MB)
        if (file.getSize() > 1048576000) { // 1000MB = 1000 * 1024 * 1024 bytes
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File size must be less than 1000MB")
                    .addConstraintViolation();
            return false;
        }

        // Check file type (e.g., only video files)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File must be a valid video type")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
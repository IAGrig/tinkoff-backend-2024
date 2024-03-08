package edu.java.validators;

import java.net.URI;
import java.net.URISyntaxException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UriValidator implements ConstraintValidator<ValidUri, String> {
    @Override
    public void initialize(ValidUri constraintAnnotation) {
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}



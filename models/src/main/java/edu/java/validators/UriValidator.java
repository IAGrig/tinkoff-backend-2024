package edu.java.validators;

import java.net.URI;
import java.net.URISyntaxException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
public class UriValidator implements ConstraintValidator<ValidUri, String> {
    @Override
    public void initialize(ValidUri constraintAnnotation) {
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        try {
            URI uri = new URI(url);
            return uri.getScheme() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}



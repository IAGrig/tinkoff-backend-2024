package edu.java.errorsHandlers;

import edu.java.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ScrapperErrorHandler {
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequest(IllegalArgumentException ex) {
        return new ApiErrorResponse()
            .code("400")
            .exceptionName("Illegal argument")
            .exceptionMessage(ex.getMessage())
            .description(ex.getLocalizedMessage());
    }
}

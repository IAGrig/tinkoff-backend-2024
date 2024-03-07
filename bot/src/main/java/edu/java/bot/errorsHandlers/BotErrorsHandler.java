package edu.java.bot.errorsHandlers;

import edu.java.bot.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BotErrorsHandler {
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

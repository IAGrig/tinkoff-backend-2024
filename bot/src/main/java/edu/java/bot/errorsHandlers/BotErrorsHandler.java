package edu.java.bot.errorsHandlers;

import edu.java.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@SuppressWarnings("MultipleStringLiterals")
@RestControllerAdvice
public class BotErrorsHandler {
    private final String badRequestCode = "400";

    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequest(IllegalArgumentException ex) {
        return getApiErrorResponse(badRequestCode, "Illegal arument", ex);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        return getApiErrorResponse(badRequestCode, "Validation exception", ex);
    }

    @ExceptionHandler(value = HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidationException(HandlerMethodValidationException ex) {
        return getApiErrorResponse(badRequestCode, "Validation exception", ex);
    }

    private ApiErrorResponse getApiErrorResponse(String code, String exceptionName, Throwable ex) {
        return new ApiErrorResponse()
            .code(code)
            .exceptionName(exceptionName)
            .exceptionMessage(ex.getMessage())
            .description(ex.getLocalizedMessage());
    }
}

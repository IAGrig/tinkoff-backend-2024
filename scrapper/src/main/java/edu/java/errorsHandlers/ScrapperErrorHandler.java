package edu.java.errorsHandlers;

import edu.database.exceptions.LinkNotFoundException;
import edu.database.exceptions.UserNotFoundException;
import edu.java.dto.ApiErrorResponse;
import edu.java.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@SuppressWarnings("MultipleStringLiterals")
@RestControllerAdvice
public class ScrapperErrorHandler {
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

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        return getApiErrorResponse(badRequestCode, "The user was not found", ex);
    }

    @ExceptionHandler(value = LinkNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleLinkNotFoundException(LinkNotFoundException ex) {
        return getApiErrorResponse(badRequestCode, "The link was not found", ex);
    }

    @ExceptionHandler(value = ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handle(ApiException ex) {
        return getApiErrorResponse(badRequestCode, "", ex);
    }

    private ApiErrorResponse getApiErrorResponse(String code, String exceptionName, Throwable ex) {
        return new ApiErrorResponse()
            .code(code)
            .exceptionName(exceptionName)
            .exceptionMessage(ex.getMessage())
            .description(ex.getLocalizedMessage());
    }
}

package edu.java.exceptions;

public class ApiException extends Throwable {
    private final int code;

    public ApiException(String msg) {
        super(msg);
        this.code = 0;
    }

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
    }
}

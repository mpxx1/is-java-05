package me.macao.exception;

public class PasswordCreateException
    extends RuntimeException {

    public PasswordCreateException(final String message) {
        super(message);
    }
}

package me.macao.exception;

public class EmailCreateException
    extends RuntimeException {

    public EmailCreateException(final String message) {
        super(message);
    }
}

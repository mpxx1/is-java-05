package me.macao.exception;

public class ObjectNotFoundException
    extends RuntimeException {

    public ObjectNotFoundException(final String message) {
        super(message);
    }
}

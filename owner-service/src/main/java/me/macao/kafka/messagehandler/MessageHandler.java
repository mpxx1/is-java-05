package me.macao.kafka.messagehandler;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MessageHandler {
    String handle(final String value) throws JsonProcessingException;
    boolean canHandle(final String key);
}

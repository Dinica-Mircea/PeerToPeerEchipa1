package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;

public class IncorrectMessageFormatException extends JsonProcessingException {
    protected IncorrectMessageFormatException(String message) {
        super(message + " is not a valid message format");
    }
}

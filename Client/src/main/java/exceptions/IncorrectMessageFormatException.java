package exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

public class IncorrectMessageFormatException extends JsonProcessingException {
    public IncorrectMessageFormatException(String message) {
        super(message + " is not a valid message format");
    }
}

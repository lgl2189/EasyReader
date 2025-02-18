package com.reader.exception;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 2月 18 20:15
 */


public class InvalidInputFileException extends Exception {

    public InvalidInputFileException() {
        super();
    }

    public InvalidInputFileException(String message) {
        super(message);
    }

    public InvalidInputFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputFileException(Throwable cause) {
        super(cause);
    }
}
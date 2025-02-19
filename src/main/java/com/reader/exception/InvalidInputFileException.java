package com.reader.exception;

import java.io.Serial;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 2月 18 20:15
 */


public class InvalidInputFileException extends Exception {

    @Serial
    private static final long serialVersionUID = 3941451940451406787L;

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
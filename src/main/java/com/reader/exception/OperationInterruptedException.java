package com.reader.exception;

import java.io.Serial;

/**
 * @author      ：李冠良
 * @description ：无描述
 * @date        ：2025 2月 19 15:46
 */


public class OperationInterruptedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7185537284709830197L;

    public OperationInterruptedException() {
        super();
    }

    public OperationInterruptedException(String message) {
        super(message);
    }

    public OperationInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OperationInterruptedException(Throwable cause) {
        super(cause);
    }
}
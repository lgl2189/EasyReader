package com.reader.exception;

import java.io.Serial;

/**
 * @author      ：李冠良
 * @description ：空文件异常
 * @date        ：2025 2月 18 19:43
 */


public class EmptyFileException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4752356635173965066L;

    public EmptyFileException() {
        super();
    }

    public EmptyFileException(Throwable cause) {
        super(cause);
    }

    public EmptyFileException(String message) {
        super(message);
    }

    public EmptyFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
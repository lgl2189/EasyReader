package com.reader.exception;

import java.io.Serial;

/**
 * @author      ：李冠良
 * @description ：    遇到无法解析的网络资源

 * @date        ：2025 2月 19 17:12
 */


public class InvalidWebResource extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 9158331398451148561L;

    public InvalidWebResource() {
        super();
    }

    public InvalidWebResource(String message) {
        super(message);
    }

    public InvalidWebResource(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidWebResource(Throwable cause) {
        super(cause);
    }
}
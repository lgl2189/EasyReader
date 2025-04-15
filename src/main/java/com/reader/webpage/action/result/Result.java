package com.reader.webpage.action.result;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ：李冠良
 * @description ：表示一个Action的结果
 * @date ：2025 4月 11 22:54
 */


public class Result implements Serializable {

    @Serial
    private static final long serialVersionUID = 1193996171142880993L;

    private ResultType status;

    private String message = "";

    public Result(ResultType status) {
        this.status = status;
    }

    public Result(ResultType status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResultType getStatus() {
        return status;
    }

    public void setStatus(ResultType status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
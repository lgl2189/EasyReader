package com.reader.entity.net;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author      ：李冠良
 * @description ：    用于保存一个请求返回的数据

 * @date        ：2025 2月 19 16:23
 */


public class ResponseData {
    private int statusCode;
    private Map<String, String> headers = new HashMap<>();
    private String bodyStr;
    private byte[] bodyBytes;

    public ResponseData() {
    }

    public ResponseData(int statusCode, Map<String, String> headers, String bodyStr) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.bodyStr = bodyStr;
    }

    public ResponseData(int statusCode, Map<String, String> headers, byte[] bodyBytes) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.bodyBytes = bodyBytes;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBodyStr() {
        return bodyStr;
    }

    public void setBodyStr(String bodyStr) {
        this.bodyStr = bodyStr;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    public void setBodyBytes(byte[] bodyBytes) {
        this.bodyBytes = bodyBytes;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "statusCode=" + statusCode +
                ", headers=" + headers +
                ", bodyStr='" + bodyStr + '\'' +
                ", bodyBytes=" + Arrays.toString(bodyBytes) +
                '}';
    }
}
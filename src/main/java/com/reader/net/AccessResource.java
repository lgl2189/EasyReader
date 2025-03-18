package com.reader.net;

import com.reader.entity.net.ResponseData;
import com.reader.exception.InvalidWebResource;
import com.reader.exception.OperationInterruptedException;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author      ：李冠良
 * @description ：    从网络获取资源

 * @date        ：2025 2月 19 14:44
 */


public class AccessResource {

    private final String url;
    private ResponseData data;
    private final CloseableHttpClient httpClient;
    private int maxReconnectionNum = 3;
    private int reconnectionInterval = 100;
    private RequestType requestType = RequestType.GET;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public enum RequestType {
        GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"),
        HEAD("HEAD"), OPTIONS("OPTIONS"), TRACE("TRACE"), PATCH("PATCH");

        private final String type;

        RequestType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public AccessResource(String url, CloseableHttpClient httpClient) {
        this.url = url;
        this.httpClient = httpClient;
    }

    public AccessResource(String url, CookieStore cookieStore) {
        this.url = url;
        this.httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    /**
     * 从网络获取资源
     * @throws IOException 重试 maxReconnectionNum 次后，仍然无法访问资源，则抛出IOException
     * @throws IllegalArgumentException url为null或空字符串，或者httpClient为null，则抛出IllegalArgumentException
     * @throws OperationInterruptedException 线程在等待重试时被中断，则抛出OperationInterruptedException
     * @throws InvalidWebResource 资源类型不支持，则抛出InvalidWebResource
     */
    public void execute() throws IOException, InvalidWebResource, IllegalArgumentException, OperationInterruptedException {
        lock.writeLock().lock();
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        if (httpClient == null) {
            throw new IllegalArgumentException("httpClient cannot be null");
        }
        ClassicHttpRequest request = new BasicClassicHttpRequest(requestType.getType(), url);
        HttpClientResponseHandler<ResponseData> handler = response -> {
            int status = response.getCode();
            if (status >= 200 && status < 300) {
                int responseStatusCode = response.getCode();
                Map<String, String> responseHeaders = new HashMap<>();
                for (Header headerItem : response.getHeaders()) {
                    responseHeaders.put(headerItem.getName(), headerItem.getValue());
                }
                String contentType = responseHeaders.get("Content-Type");
                HttpEntity entity = response.getEntity();
                if (contentType != null) {
                    if (contentType.startsWith("text/") || contentType.contains("json") || contentType.contains("xml")) {
                        String responseBodyBytes = EntityUtils.toString(entity);
                        return new ResponseData(responseStatusCode, responseHeaders, responseBodyBytes);
                    }
                    else if (contentType.startsWith("image/") || contentType.startsWith("audio/") ||
                            contentType.startsWith("video/") || contentType.equals("application/octet-stream") ||
                            contentType.equals("application/pdf")) {
                        byte[] binaryData = entity.getContent().readAllBytes();
                        return new ResponseData(responseStatusCode, responseHeaders, binaryData);
                    }
                    else if (contentType.startsWith("multipart/")) {
                        throw new InvalidWebResource("Unsupported content type: " + contentType);
                    }
                    else {
                        throw new InvalidWebResource("Unsupported content type: " + contentType);
                    }
                }
            }
            else {
                throw new RuntimeException("Unexpected response status: " + status);
            }
            return null;
        };
        ResponseData responseData = null;
        for (int attempts = 0; attempts < maxReconnectionNum; attempts++) {
            try {
                responseData = httpClient.execute(request, handler);
                // 如果执行到这里说明请求成功，跳出循环
                break;
            }
            catch (IOException e) {
                if (attempts + 1 >= maxReconnectionNum) {
                    throw new IOException("Failed to access resource after " + maxReconnectionNum + " attempts", e);
                }
                try {
                    Thread.sleep(reconnectionInterval); // 等待指定时间后重试
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // 恢复中断状态
                    throw new OperationInterruptedException("Thread was interrupted during reconnection wait", ie);
                }
            }
        }
        if (responseData == null) {
            throw new IOException("Failed to access resource, after " + maxReconnectionNum + " attempts: " + url);
        }
        data = responseData;
        lock.writeLock().unlock();
    }

    public String getUrl() {
        return url;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public int getMaxReconnectionNum() {
        lock.readLock().lock();
        try {
            return maxReconnectionNum;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    public void setMaxReconnectionNum(int maxReconnectionNum) {
        lock.writeLock().lock();
        this.maxReconnectionNum = maxReconnectionNum;
        lock.writeLock().unlock();
    }

    public int getReconnectionInterval() {
        lock.readLock().lock();
        try {
            return reconnectionInterval;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    public void setReconnectionInterval(int reconnectionInterval) {
        lock.writeLock().lock();
        this.reconnectionInterval = reconnectionInterval;
        lock.writeLock().unlock();
    }

    public RequestType getRequestType() {
        lock.readLock().lock();
        try {
            return requestType;
        }
        finally {
            lock.readLock().unlock();
        }

    }

    public void setRequestType(RequestType requestType) {
        lock.writeLock().lock();
        this.requestType = requestType;
        lock.writeLock().unlock();
    }

    public ResponseData getData() {
        return data;
    }
}
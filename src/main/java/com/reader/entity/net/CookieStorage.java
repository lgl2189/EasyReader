package com.reader.entity.net;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 3月 27 08:31
 */


public class CookieStorage implements Serializable {
    @Serial
    private static final long serialVersionUID = 4772477817965658174L;
    // Cookie自身属性
    private final String name;
    private String value;
    // 标头的Cookie字段中的属性
    private String domain;
    private long maxAge = -1;
    private String path;
    private boolean secure;
    private boolean httpOnly;

    public CookieStorage(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public CookieStorage(String name, String value, String domain, String path, boolean secure, boolean httpOnly) {
        this.name = name;
        this.value = value;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    public CookieStorage(String name, String value, String domain, long maxAge, String path, boolean secure, boolean httpOnly) {
        this.name = name;
        this.value = value;
        this.domain = domain;
        this.maxAge = maxAge;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public String toString() {
        return "CookieStorage{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", domain='" + domain + '\'' +
                ", maxAge=" + maxAge +
                ", path='" + path + '\'' +
                ", secure=" + secure +
                ", httpOnly=" + httpOnly +
                '}';
    }
}
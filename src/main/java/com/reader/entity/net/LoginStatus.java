package com.reader.entity.net;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author ：李冠良
 * @description ：    通过存储Cookie和LocalStorage，来用于存储登录状态
 * @date ：2025 3月 27 08:01
 */


public class LoginStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 4452425838135552712L;

    private List<CookieStorage> cookieStorageList;
    private Map<String, Object> localStorageMap;

    public LoginStatus(List<CookieStorage> cookieStorageList, Map<String, Object> localStorageMap) {
        this.cookieStorageList = cookieStorageList;
        this.localStorageMap = localStorageMap;
    }

    public List<CookieStorage> getCookieStorageList() {
        return cookieStorageList;
    }

    public void setCookieStorageList(List<CookieStorage> cookieStorageList) {
        this.cookieStorageList = cookieStorageList;
    }

    public Map<String, Object> getLocalStorageMap() {
        return localStorageMap;
    }

    public void setLocalStorageMap(Map<String, Object> localStorageMap) {
        this.localStorageMap = localStorageMap;
    }

    @Override
    public String toString() {
        return "LoginStatus{" +
                "cookieStorageList=" + cookieStorageList +
                ", localStorageMap=" + localStorageMap +
                '}';
    }
}
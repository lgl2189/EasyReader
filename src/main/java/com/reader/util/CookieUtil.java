package com.reader.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reader.entity.net.CookieStorage;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：李冠良
 * @description ：无描述
 * @date ：2025 3月 26 14:45
 */


public class CookieUtil {
    public static List<CookieStorage> parseCookies(CookieStore cookieStore) {
        List<CookieStorage> cookieStorageList = new ArrayList<>();
        for (HttpCookie cookie : cookieStore.getCookies()) {
            cookieStorageList.add(new CookieStorage(
                    cookie.getName(),
                    cookie.getValue(),
                    cookie.getDomain(),
                    cookie.getPath(),
                    cookie.getSecure(),
                    cookie.isHttpOnly()
            ));
        }
        return cookieStorageList;
    }

    // LocalStorage解析工具方法
    public static Map<String, Object> parseLocalStorage(String localStorageJson) {
        try {
            return new ObjectMapper().readValue(localStorageJson, HashMap.class);
        }
        catch (Exception e) {
            System.err.println("LocalStorage解析失败: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
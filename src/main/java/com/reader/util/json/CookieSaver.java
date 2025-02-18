package com.reader.util.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.reader.exception.EmptyFileException;
import com.reader.exception.InvalidInputFileException;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;

import java.io.*;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;

/**
 * @author      ：李冠良
 * @description ：用于将Cookies保存到本地的类
 * @date        ：2025 2月 17 22:44
 */


public class CookieSaver {

    public static final Gson gson = new Gson();
    private static final String jsonFilePrefix = File.separator + "cookie" + File.separator;
    private static final String jsonFileSuffix = ".json";

    public static void save(List<Map<String, String>> cookieMapList, String cookieFileName, String path)
            throws IOException {
        String jsonFilePath = path + jsonFilePrefix;
        File file = new File(jsonFilePath);
        if (!file.exists() && !file.mkdirs()) {
            throw new FileNotFoundException("无法创建Cookie存储路径: " + path);
        }
        jsonFilePath += cookieFileName + jsonFileSuffix;
        String resultJson = gson.toJson(cookieMapList);
        try (FileOutputStream fos = new FileOutputStream(jsonFilePath)) {
            fos.write(resultJson.getBytes());
        }
        catch (IOException e) {
            throw new IOException(e);
        }
    }

    public static void save(CookieStore cookieStore, String cookieFileName, String path) throws IOException {
        List<Map<String, String>> cookieMapList = new ArrayList<>();
        for (Cookie cookie : cookieStore.getCookies()) {
            Map<String, String> cookieMap = new HashMap<>();
            String cookieName = cookie.getName();
            String cookieValue = cookie.getValue();
            if (cookieName == null || cookieValue == null) {
                continue;
            }
            String cookieDomain = cookie.getDomain();
            String cookiePath = cookie.getPath();
            Instant cookieExpiryInstant = cookie.getExpiryInstant();
            extractCookieAttribute(cookieMapList, cookieName, cookieValue, cookieDomain, cookiePath,
                    cookieExpiryInstant, cookieMap, cookie.isSecure(), cookie.isHttpOnly());
        }
        save(cookieMapList, cookieFileName, path);
    }

    public static void save(Set<org.openqa.selenium.Cookie> cookieSet, String cookieFileName, String path) throws IOException {
        List<Map<String, String>> cookieMapList = new ArrayList<>();
        for (org.openqa.selenium.Cookie cookie : cookieSet) {
            String cookieName = cookie.getName();
            String cookieValue = cookie.getValue();
            if (cookieName == null || cookieValue == null) {
                continue;
            }
            String cookieDomain = cookie.getDomain();
            String cookiePath = cookie.getPath();
            Date cookieExpiry = cookie.getExpiry();
            Instant cookieExpiryInstant = null;
            if (cookieExpiry != null) {
                cookieExpiryInstant = Instant.ofEpochMilli(cookieExpiry.getTime());
            }
            String cookieSameSite = cookie.getSameSite();
            Map<String, String> cookieMap = new HashMap<>();
            extractCookieAttribute(cookieMapList, cookieName, cookieValue, cookieDomain, cookiePath,
                    cookieExpiryInstant, cookieMap, cookie.isSecure(), cookie.isHttpOnly());
            if (cookieSameSite != null) {
                cookieMap.put("sameSite", cookieSameSite);
            }
        }
        save(cookieMapList, cookieFileName, path);
    }

    private static void extractCookieAttribute(List<Map<String, String>> cookieMapList, String cookieName,
                                               String cookieValue, String cookieDomain, String cookiePath,
                                               Instant cookieExpiryInstant, Map<String, String> cookieMap,
                                               boolean secure, boolean httpOnly) {
        cookieMap.put("name", cookieName);
        cookieMap.put("value", cookieValue);
        if (cookieDomain != null) cookieMap.put("domain", cookieDomain);
        if (cookiePath != null) cookieMap.put("path", cookiePath);
        if (cookieExpiryInstant != null) cookieMap.put("expires", cookieExpiryInstant.toString());
        cookieMap.put("secure", String.valueOf(secure));
        cookieMap.put("httpOnly", String.valueOf(httpOnly));
        cookieMapList.add(cookieMap);
    }

    public static List<Map<String, String>> load(String cookieFileName, String path) throws IOException {
        String jsonFilePath = path + jsonFilePrefix;
        File file = new File(jsonFilePath);
        if (!file.exists()) {
            throw new FileNotFoundException("Cookie存储路径无效");
        }
        jsonFilePath += cookieFileName + jsonFileSuffix;
        try (FileInputStream fis = new FileInputStream(jsonFilePath)) {

            byte[] bytes = new byte[fis.available()];
            int len = fis.read(bytes);
            if (len == -1) {
                throw new EmptyFileException("Cookie文件为空");
            }
            String jsonStr = new String(bytes);
            Type listType = new TypeToken<List<Map<String, String>>>() {
            }.getType();
            return gson.fromJson(jsonStr, listType);
        }
        catch (IOException e) {
            throw new IOException(e);
        }
    }

    public static CookieStore loadAsCookieStore(String cookieFileName, String path) throws IOException {
        List<Map<String, String>> cookieMapList = load(cookieFileName, path);
        CookieStore cookieStore = new BasicCookieStore();
        for (Map<String, String> cookieMap : cookieMapList) {
            String cookieName = cookieMap.get("name");
            String cookieValue = cookieMap.get("value");
            if (cookieName == null || cookieValue == null) {
                continue;
            }
            String cookieDomain = cookieMap.get("domain");
            String cookiePath = cookieMap.get("path");
            Instant cookieExpiryInstant = null;
            String cookieExpiry = cookieMap.get("expires");
            if (cookieExpiry != null) {
                cookieExpiryInstant = Instant.parse(cookieExpiry);
            }
            boolean cookieSecure = Boolean.parseBoolean(cookieMap.get("secure"));
            boolean cookieHttpOnly = Boolean.parseBoolean(cookieMap.get("httpOnly"));
            BasicClientCookie cookie = new BasicClientCookie(cookieName, cookieValue);
            if (cookieDomain != null) cookie.setDomain(cookieDomain);
            if (cookiePath != null) cookie.setPath(cookiePath);
            if (cookieExpiryInstant != null) cookie.setExpiryDate(cookieExpiryInstant);
            cookie.setSecure(cookieSecure);
            cookie.setHttpOnly(cookieHttpOnly);
            cookieStore.addCookie(cookie);
        }
        return cookieStore;
    }

    public static Set<org.openqa.selenium.Cookie> loadAsCookieSet(String cookieFileName, String path) throws IOException {
        List<Map<String, String>> cookieMapList = load(cookieFileName, path);
        Set<org.openqa.selenium.Cookie> cookieSet = new HashSet<>();
        for (Map<String, String> cookieMap : cookieMapList) {
            String cookieName = cookieMap.get("name");
            String cookieValue = cookieMap.get("value");
            if (cookieName == null || cookieValue == null) {
                continue;
            }
            String cookieDomain = cookieMap.get("domain");
            String cookiePath = cookieMap.get("path");
            String cookieExpiry = cookieMap.get("expires");
            boolean cookieSecure = Boolean.parseBoolean(cookieMap.get("secure"));
            boolean cookieHttpOnly = Boolean.parseBoolean(cookieMap.get("httpOnly"));
            String cookieSameSite = cookieMap.get("sameSite");
            org.openqa.selenium.Cookie.Builder cookieBuilder = new org.openqa.selenium.Cookie.Builder(cookieName, cookieValue);
            if (cookieDomain != null) cookieBuilder.domain(cookieDomain);
            if (cookiePath != null) cookieBuilder.path(cookiePath);
            if (cookieExpiry != null) cookieBuilder.expiresOn(Date.from(Instant.parse(cookieExpiry)));
            if (cookieSecure) cookieBuilder.isSecure(true);
            if (cookieHttpOnly) cookieBuilder.isHttpOnly(true);
            if (cookieSameSite != null) cookieBuilder.sameSite(cookieSameSite);
            cookieSet.add(cookieBuilder.build());
        }
        return cookieSet;
    }
}
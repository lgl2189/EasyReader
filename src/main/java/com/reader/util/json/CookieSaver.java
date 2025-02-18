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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author      ：李冠良
 * @description ：用于将Cookies保存到本地的类
 * @date        ：2025 2月 17 22:44
 */


public class CookieSaver {

    public static final Gson gson = new Gson();
    private static final String jsonFilePrefix = File.separator + "cookie" + File.separator;
    private static final String jsonFileSuffix = ".json";

    /**
     * 保存Cookies到本地
     * @param cookieStore 待保存的CookieStore
     * @param path 保存路径
     * @throws FileNotFoundException 所给路径无法保存时抛出异常
     * @throws IOException 写入文件失败时抛出异常
     */
    public static void save(CookieStore cookieStore, String cookieFileName, String path)
            throws FileNotFoundException, IOException {
        String jsonFilePath = path + jsonFilePrefix;
        File file = new File(jsonFilePath);
        if (!file.exists() && !file.mkdirs()) {
            throw new FileNotFoundException("无法创建Cookie存储路径: " + path);
        }
        jsonFilePath += cookieFileName + jsonFileSuffix;
        //将CookeList转换为MapList
        List<Map<String, String>> cookieMapList = new ArrayList<>();
        for (Cookie cookie : cookieStore.getCookies()) {
            cookieMapList.add(transCookieToMap(cookie));
        }
        String resultJson = gson.toJson(cookieMapList);
        try (FileOutputStream fos = new FileOutputStream(jsonFilePath)) {
            fos.write(resultJson.getBytes());
        }
        catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * 从本地读取Cookies
     * @param cookieFileName Cookie文件名
     * @param path 保存路径
     * @return 使用CookieStore储存的Cookies
     * @throws IOException 读取文件失败时抛出异常
     */
    public static CookieStore load(String cookieFileName, String path) throws IOException {
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
            List<Map<String, String>> cookieMapList = gson.fromJson(jsonStr, listType);
            List<Cookie> resultCookieList = new ArrayList<>();
            for (Map<String, String> cookieMap : cookieMapList) {
                try {
                    resultCookieList.add(transMapToCookie(cookieMap));
                }
                catch (InvalidInputFileException ignored) {
                    //忽略非法的Cookie
                }
            }
            CookieStore resultCookieStore = new BasicCookieStore();
            for (Cookie cookie : resultCookieList) {
                resultCookieStore.addCookie(cookie);
            }
            return resultCookieStore;
        }
        catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * 将Cookie转换为Map
     * @param cookie 保存在Cookie对象中的Cookie
     * @return 保存Cookie信息的Map
     */
    public static Map<String, String> transCookieToMap(Cookie cookie) {
        Map<String, String> cookieMap = new HashMap<>();
        String name = cookie.getName();
        String value = cookie.getValue();
        String domain = cookie.getDomain();
        String path = cookie.getPath();
        Instant expiryInstant = cookie.getExpiryInstant();
        if (name != null) cookieMap.put("name", name);
        if (value != null) cookieMap.put("value", value);
        if (domain != null) cookieMap.put("domain", domain);
        if (path != null) cookieMap.put("path", path);
        if (expiryInstant != null) cookieMap.put("expires/max-age", expiryInstant.toString());
        //boolean为基本类型无需判断是否为空
        cookieMap.put("secure", String.valueOf(cookie.isSecure()));
        cookieMap.put("httpOnly", String.valueOf(cookie.isHttpOnly()));
        return cookieMap;
    }

    /**
     * 将Map转换为Cookie
     * @param cookieMap 保存Cookie信息的Map
     * @return 保存在Cookie对象中的Cookie
     */
    public static Cookie transMapToCookie(Map<String, String> cookieMap) throws InvalidInputFileException {
        String cookieName = cookieMap.get("name");
        String cookieValue = cookieMap.get("value");
        if (cookieName == null || cookieValue == null) {
            throw new InvalidInputFileException("Cookie缺失name或value属性");
        }
        String cookieDomain = cookieMap.get("domain");
        String cookiePath = cookieMap.get("path");
        String cookieExpiry = cookieMap.get("expires/max-age");
        String cookieSecure = cookieMap.get("secure");
        String cookieHttpOnly = cookieMap.get("httpOnly");
        BasicClientCookie cookie = new BasicClientCookie(cookieName, cookieValue);
        if (cookieDomain != null) cookie.setDomain(cookieDomain);
        if (cookiePath != null) cookie.setPath(cookiePath);
        if (cookieExpiry != null) cookie.setExpiryDate(Instant.parse(cookieExpiry));
        if (cookieSecure != null) cookie.setSecure(Boolean.parseBoolean(cookieSecure));
        if (cookieHttpOnly != null) cookie.setHttpOnly(Boolean.parseBoolean(cookieHttpOnly));
        return cookie;
    }
}
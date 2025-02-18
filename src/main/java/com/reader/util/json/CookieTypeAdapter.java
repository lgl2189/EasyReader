package com.reader.util.json;

import com.google.gson.*;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * @author      ：李冠良
 * @description ：无描述

 * @date        ：2025 2月 17 23:42
 */


public class CookieTypeAdapter implements JsonSerializer<Cookie>, JsonDeserializer<Cookie> {

    @Override
    public JsonElement serialize(Cookie src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject cookieJson = new JsonObject();
        cookieJson.addProperty("name", src.getName());
        cookieJson.addProperty("value", src.getValue());
        cookieJson.addProperty("domain", src.getDomain());
        cookieJson.addProperty("path", src.getPath());
        if (src.getExpiryDate() != null) {
            cookieJson.addProperty("expiryDate", src.getExpiryDate().getTime());
        }
        cookieJson.addProperty("isSecure", src.isSecure());
        return cookieJson;
    }

    @Override
    public Cookie deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String value = jsonObject.get("value").getAsString();
        String domain = jsonObject.get("domain").getAsString();
        String path = jsonObject.get("path").getAsString();
        Date expiryDate = null;
        if (jsonObject.has("expiryDate")) {
            expiryDate = new Date(jsonObject.get("expiryDate").getAsLong());
        }
        boolean isSecure = jsonObject.get("isSecure").getAsBoolean();

        // 创建BasicClientCookie实例
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        if (expiryDate != null) {
            cookie.setExpiryDate(expiryDate);
        }
        cookie.setSecure(isSecure);
        return cookie;
    }
}
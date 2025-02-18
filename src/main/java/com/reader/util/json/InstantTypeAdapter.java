package com.reader.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

/**
 * @author      ：李冠良
 * @description ：无描述

 * @date        ：2025 2月 17 23:52
 */

public class InstantTypeAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
    @Override
    public JsonElement serialize(Instant instant, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(instant.getEpochSecond());
    }

    @Override
    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Instant.ofEpochSecond(json.getAsLong());
    }
}
package com.zhangyiwen.caviar.codec;

import com.google.gson.*;
import org.apache.commons.codec.Charsets;

import java.lang.reflect.Type;
import java.time.Instant;

/**
 * Created by zhangyiwen on 2017/12/14.
 * 序列化工具——Gson实现
 *
 */
public class GsonSerialization implements Serialization{

    //==========
    private static volatile Gson gson;

    public GsonSerialization() {
        this(FieldNamingPolicy.IDENTITY);
    }

    public GsonSerialization(FieldNamingPolicy fieldNamingPolicy) {
        GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(fieldNamingPolicy).registerTypeAdapter(Instant.class,
                InstantCodec.instance());
        gson = builder.create();
    }

    public static class InstantCodec implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

        public static InstantCodec instance() {
            return instance;
        }

        private static final InstantCodec instance = new InstantCodec();

        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return Instant.ofEpochMilli(json.getAsLong());
        }

        public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toEpochMilli());
        }
    }

    //==========
    private static volatile GsonSerialization gsonSerialization = null;

    public static GsonSerialization getGsonSerialization(){
        if(gsonSerialization == null){
            gsonSerialization = new GsonSerialization();
        }
        return gsonSerialization;
    }

    //==========
    public byte[] serialize(Object obj) {
        return gson.toJson(obj).getBytes(Charsets.UTF_8);
    }

    public <T> T deserialize(byte[] in, Class<T> type) {
        return gson.fromJson(new String(in, Charsets.UTF_8), type);
    }
}

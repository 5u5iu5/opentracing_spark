package com.sucius.spark.opentracing.carrier;

import io.opentracing.propagation.TextMap;
import okhttp3.Request;

import java.util.Iterator;
import java.util.Map;

public class RequestBuilderCarrier implements TextMap {
    private final spark.Response builder;

    public RequestBuilderCarrier(spark.Response builder) {
        this.builder = builder;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("carrier is write-only");
    }

    @Override
    public void put(String key, String value) {
        builder.header(key, value);
    }
}

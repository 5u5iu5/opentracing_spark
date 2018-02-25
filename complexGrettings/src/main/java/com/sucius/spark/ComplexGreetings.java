package com.sucius.spark;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.Scope;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

import static com.sucius.spark.endpoints.Endpoints.setEndpoints;
import static com.sucius.spark.opentracing.OpenTracingConfiguration.configOpenTracing;
import static spark.Spark.*;

public class ComplexGreetings {

    private static final Logger log = LoggerFactory.getLogger(ComplexGreetings.class);

    static Optional<Tracer> tracer;

    public static void main(String[] args) throws Exception {

        /** SERVER **/
        port(8081);

        /** OPENTRACING CONFIG **/
        tracer = configOpenTracing();

        /** FILTERS **/
        configFilters();

        /** ENDPOINTS **/
        setEndpoints();
    }

    private static void configFilters() {
        before((request, response) -> {
            System.out.println(request.pathInfo());
            log.info("Before call -> " + request.pathInfo());



            Span simpleSpan = getSpan(tracer, request);
            request.attribute("span", simpleSpan);
        });

        after((request, response) -> {
            System.out.println(request.headers());
            log.info(request.pathInfo() + " -> After call");
            Span simpleSpan = request.attribute("span");
            simpleSpan.finish();

        });
    }

    private static Span getSpan(Request request) {
        Span simpleSpan = null;
        if (tracer.isPresent()) {
            simpleSpan = tracer.get().buildSpan(request.pathInfo()).start();
        }
        return simpleSpan;
    }

    private Consumer<Exception> initExceptionHandler = (e) -> {
        log.error("ignite failed", e);
        System.exit(100);
    };

    public static Scope startServerSpan(Tracer tracer, javax.ws.rs.core.HttpHeaders httpHeaders,
                                        String operationName) {
        // format the headers for extraction
        MultivaluedMap<String, String> rawHeaders = httpHeaders.getRequestHeaders();
        final HashMap<String, String> headers = new HashMap<String, String>();
        for (String key : rawHeaders.keySet()) {
            headers.put(key, rawHeaders.get(key).get(0));
        }

        Tracer.SpanBuilder spanBuilder;
        try {
            SpanContext parentSpan = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapExtractAdapter(headers));
            if (parentSpan == null) {
                spanBuilder = tracer.buildSpan(operationName);
            } else {
                spanBuilder = tracer.buildSpan(operationName).asChildOf(parentSpan);
            }
        } catch (IllegalArgumentException e) {
            spanBuilder = tracer.buildSpan(operationName);
        }
        return spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER).startActive(true);
    }
}

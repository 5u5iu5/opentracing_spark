package com.sucius.spark;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import static com.sucius.spark.endpoints.Endpoints.setEndpoints;
import static com.sucius.spark.opentracing.OpenTracingConfiguration.configOpenTracing;
import static spark.Spark.*;

public class ComplexGreetings {

    private static final Logger log = LoggerFactory.getLogger(ComplexGreetings.class);

    public static void main(String[] args) throws Exception {

        /** SERVER **/
        port(8081);

        /** OPENTRACING CONFIG **/
        Optional<Tracer> tracer = configOpenTracing();

        /** FILTERS **/
        configFilters(tracer);

        /** ENDPOINTS **/
        setEndpoints();
    }

    private static void configFilters(Optional<Tracer> tracer) {
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

    private static Span getSpan(Optional<Tracer> tracer, Request request) {
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
}

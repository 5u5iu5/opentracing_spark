package com.sucius.spark;

import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.sucius.spark.endpoints.Endpoints.setEndpoints;
import static com.sucius.spark.opentracing.OpenTracingConfiguration.configOpenTracing;
import static spark.Spark.*;

public class ComplexGreetings {

    private static final Logger log = LoggerFactory.getLogger(ComplexGreetings.class);

    public static void main(String[] args) throws Exception {

        /** SERVER **/
        port(8081);

        /** FILTERS **/
        configFilters();

        /** OPENTRACING CONFIG **/
        configOpenTracing();

        /** ENDPOINTS **/
        setEndpoints();
    }

    private static void configFilters() {
        before((request, response) -> {
            System.out.println(request.pathInfo());
            log.info("Before call -> " + request.pathInfo());
            Span simpleSpan = GlobalTracer.get().buildSpan(request.pathInfo()).start();
            request.attribute("span", simpleSpan);
        });

        after((request, response) -> {
            System.out.println(request.headers());
            log.info(request.pathInfo() + " -> After call");
            Span simpleSpan = request.attribute("span");
            simpleSpan.finish();

        });
    }

    private Consumer<Exception> initExceptionHandler = (e) -> {
        log.error("ignite failed", e);
        System.exit(100);
    };
}

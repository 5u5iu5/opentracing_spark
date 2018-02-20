package com.sucius.spark;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.util.Optional;

import static com.sucius.spark.endpoints.Endpoints.setEndpoints;
import static com.sucius.spark.opentracing.OpenTracingConfiguration.configOpenTracing;
import static spark.Spark.*;

public class SimpleGreetings {

    private static final Logger log = LoggerFactory.getLogger(SimpleGreetings.class);

    public static void main(String[] args) throws Exception {
        /** SERVER **/
        port(8080);

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


}

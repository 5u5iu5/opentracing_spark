package com.sucius.spark;

import com.sucius.spark.opentracing.carrier.RequestBuilderCarrier;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import javax.servlet.annotation.HttpConstraint;
import java.util.Optional;

import static com.sucius.spark.endpoints.Endpoints.setEndpoints;
import static com.sucius.spark.opentracing.OpenTracingConfiguration.configOpenTracing;
import static spark.Spark.*;

public class SimpleGreetings {

    private static Optional<Tracer> tracer;

    private static final Logger log = LoggerFactory.getLogger(SimpleGreetings.class);

    public static void main(String[] args) throws Exception {
        /** SERVER **/
        port(8080);

        /** OPENTRACING CONFIG **/
        tracer = configOpenTracing();

        /** FILTERS **/
        configFilters();


        /** ENDPOINTS **/
        setEndpoints();
    }


    private static void configFilters() {

        final String HELLO_ENDPOINT = "hello_span";

        before("/hello/*", (request, response) -> {
            System.out.println(request.pathInfo());
            log.info("Before call -> " + request.pathInfo());
            Span simpleSpan = createSpan(HELLO_ENDPOINT);
            Tags.SPAN_KIND.set(simpleSpan, Tags.SPAN_KIND_CLIENT);
            Tags.HTTP_METHOD.set(simpleSpan, "GET");
            Tags.HTTP_URL.set(simpleSpan, request.uri());
            response.header("nuevoHeader", "prueba");
            tracer.get().inject(simpleSpan.context(), Format.Builtin.HTTP_HEADERS, new RequestBuilderCarrier(response));
            request.attribute(HELLO_ENDPOINT, simpleSpan);
        });

        after("/hello/*",(request, response) -> {
            System.out.println(request.headers());
            log.info(request.pathInfo() + " -> After call");
            Span simpleSpan = request.attribute(HELLO_ENDPOINT);
            simpleSpan.finish();

        });
    }

    private static Span createSpan(String spanId) {
        Span simpleSpan = null;
        if (tracer.isPresent()) {
            simpleSpan = tracer.get().buildSpan(spanId).start();
        }
        return simpleSpan;
    }
}

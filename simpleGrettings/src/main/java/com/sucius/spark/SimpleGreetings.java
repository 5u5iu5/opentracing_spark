package com.sucius.spark;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

import static spark.Spark.*;

public class SimpleGreetings {

    private static final Logger log = LoggerFactory.getLogger(SimpleGreetings.class);

    public static void main(String[] args) throws Exception {
        /** SERVER **/
        port(8080);

        /** FILTERS **/
        configFilters();

        /** OPENTRACING CONFIG **/
        configOpenTracing();


        /** ENDPOINTS **/
        setEndpoints();
    }

    private static Properties configOpenTracing() throws Exception {
        Properties config = loadConfig();
        if (!configureGlobalTracer(config, "grettingsSimple"))
            throw new Exception("Could not configure the global tracer");

        return config;
    }

    static boolean configureGlobalTracer(Properties config, String componentName)
            throws MalformedURLException {
        String tracerName = config.getProperty("tracer");
        if ("jaeger".equals(tracerName)) {
            GlobalTracer.register(
                    new com.uber.jaeger.Configuration(
                            componentName,
                            new com.uber.jaeger.Configuration.SamplerConfiguration("const", 1),
                            new com.uber.jaeger.Configuration.ReporterConfiguration(
                                    true,  // logSpans
                                    config.getProperty("jaeger.reporter_host"),
                                    Integer.decode(config.getProperty("jaeger.reporter_port")),
                                    1000,   // flush interval in milliseconds

                                    10000)  // max buffered Spans
                    ).getTracer());
        }

        return true;
    }

    static Properties loadConfig() throws IOException, URISyntaxException {
        File file = new File(SimpleGreetings.class.getClassLoader().getResource("tracer_config.properties").toURI());
        FileInputStream fs = new FileInputStream(file);
        Properties config = new Properties();
        config.load(fs);
        return config;
    }

    private static void setEndpoints() {
        get("/hello/:name", (request, response) -> {
            return makeCallToHelloComplexGrettingsService(request.params(":name"));
        });

        get("/say/*/to/*", (request, response) -> {
            log.info("Number of splat parameters: " + Arrays.stream(request.splat()).reduce((str1, str2) -> str1.concat(" - " + str2)).get());
            return makeCallToComplexGrettingsService(request.splat()[0], request.splat()[1]);
        });
    }

    private static void configFilters() {
        before("/hello/*", (request, response) -> {
            System.out.println(request.headers());
            log.info("Before call");
            Span simpleSpan = GlobalTracer.get().buildSpan("simple_hello_span").start();
            request.attribute("span", simpleSpan);
        });

        after("/hello/*", (request, response) -> {
            System.out.println(request.headers());
            log.info("After call");
            Span simpleSpan = request.attribute("span");
            simpleSpan.finish();

        });
    }

    private static String makeCallToHelloComplexGrettingsService(String hello) {
        String response = null;
        try {
            HttpResponse<String> stringResponse = Unirest.get("http://localhost:8081/hello/{hello}")
                    .header("accept", "application/string")
                    .routeParam("hello", hello)
                    .asString();
            response = stringResponse.getBody();
        } catch (UnirestException e) {
            log.error(e.getMessage());
        }
        return response;
    }

    private static String makeCallToComplexGrettingsService(String say, String to) {
        String response = null;
        try {
            HttpResponse<String> stringResponse = Unirest.get("http://localhost:8081/say/{say}/to/{to}")
                    .header("accept", "application/json")
                    .routeParam("say", say)
                    .routeParam("to", to)
                    .asString();
            response = stringResponse.getBody();
        } catch (UnirestException e) {
            log.error(e.getMessage());
        }
        return response;
    }
}

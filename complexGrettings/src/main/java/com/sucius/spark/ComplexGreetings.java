package com.sucius.spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

import static spark.Spark.*;

public class ComplexGreetings {

    private static final Logger log = LoggerFactory.getLogger(ComplexGreetings.class);

    public static void main(String[] args) {

        /** SERVER **/
        port(8081);

        /** FILTERS **/

        configFilters();

        /** ENDPOINTS **/

        setEndpoints();
    }

    private static void setEndpoints() {
        get("/hello/:name", (request, response) -> {
            return "And Welcome " + request.params(":name").toUpperCase();
        });

        get("/say/*/to/*", (request, response) -> {
            log.info("Number of splat parameters: " + Arrays.stream(request.splat()).reduce((str1, str2) -> str1.concat(" - " + str2)).get());
            return "bien";
        });
    }

    private static void configFilters() {
        before("/hello", (request, response) -> {
            System.out.println(request.headers());
            log.info("Before call");
        });

        after("/hello", (request, response) -> {
            System.out.println(request.headers());
            log.info("After call");
        });
    }

    private Consumer<Exception> initExceptionHandler = (e) -> {
        log.error("ignite failed", e);
        System.exit(100);
    };
}

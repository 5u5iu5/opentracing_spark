package com.sucius.spark.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static spark.Spark.get;

public class Endpoints {

    private static final Logger log = LoggerFactory.getLogger(Endpoints.class);

    public static void setEndpoints() {
        get("/hello/:name", (request, response) -> {
            return "And Welcome " + request.params(":name").toUpperCase();
        });

        get("/say/*/to/*", (request, response) -> {
            log.info("Number of splat parameters: " + Arrays.stream(request.splat()).reduce((str1, str2) -> str1.concat(" - " + str2)).get());
            return "bien";
        });
    }
}

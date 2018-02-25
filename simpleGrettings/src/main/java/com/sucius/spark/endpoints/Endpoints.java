package com.sucius.spark.endpoints;

import com.sucius.spark.SimpleGreetings;
import com.sucius.spark.services.ClientREST;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static spark.Spark.get;
import static com.sucius.spark.services.ClientREST.*;

public class Endpoints {

    private static final Logger log = LoggerFactory.getLogger(Endpoints.class);

    public static void setEndpoints() {
        get("/hello/:name", (request, response) -> {
            return makeCallToHelloComplexGrettingsService(request.params(":name"));
        });

        get("/say/*/to/*", (request, response) -> {
            log.info("Number of splat parameters: " + Arrays.stream(request.splat()).reduce((str1, str2) -> str1.concat(" - " + str2)).get());
            return makeCallToComplexGrettingsService(request.splat()[0], request.splat()[1]);
        });
    }
}

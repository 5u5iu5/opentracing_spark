package com.sucius.spark;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static spark.Spark.*;

public class SimpleGreetings {

    private static final Logger log = LoggerFactory.getLogger(SimpleGreetings.class);

    public static void main(String[] args) {

        /** SERVER **/
        port(8080);

        /** FILTERS **/

        configFilters();

        /** ENDPOINTS **/

        setEndpoints();
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
        before("/hello", (request, response) -> {
            System.out.println(request.headers());
            log.info("Before call");
        });

        after("/hello", (request, response) -> {
            System.out.println(request.headers());
            log.info("After call");
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

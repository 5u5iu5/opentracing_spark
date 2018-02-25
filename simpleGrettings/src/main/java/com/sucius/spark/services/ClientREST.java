package com.sucius.spark.services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sucius.spark.opentracing.carrier.RequestBuilderCarrier;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientREST {

    private static final Logger log = LoggerFactory.getLogger(ClientREST.class);

    public static String makeCallToHelloComplexGrettingsService(String hello) {
        String response = null;
        try {
            HttpResponse<String> stringResponse = Unirest.get("http://complex:8081/hello/{hello}")
                    .header("accept", "application/string")
                    .routeParam("hello", hello)
                    .asString();
            response = stringResponse.getBody();
        } catch (UnirestException e) {
            log.error(e.getMessage());
        }
        return response;
    }

    public static String makeCallToComplexGrettingsService(String say, String to) {
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

package com.sucius.spark.opentracing;

import com.sucius.spark.ComplexGreetings;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Properties;

public class OpenTracingConfiguration {

    public static Optional<Tracer> configOpenTracing() throws Exception {
        Optional<Tracer> tracer = configureGlobalTracer(loadConfig(), "grettingsSimple");
        if (!tracer.isPresent())
            throw new Exception("Could not configure the global tracer");

        return tracer;
    }

    static Optional<Tracer> configureGlobalTracer(Properties config, String componentName)
            throws MalformedURLException {
        String tracerName = config.getProperty("tracer");
        Optional<Tracer> tracer = Optional.empty();
        if ("jaeger".equals(tracerName)) {
            tracer = Optional.of(getTracer(config, componentName));
            GlobalTracer.register(tracer.get());
        }

        return tracer;
    }

    private static Tracer getTracer(Properties config, String componentName) {
        return new com.uber.jaeger.Configuration(
                componentName,
                new com.uber.jaeger.Configuration.SamplerConfiguration("const", 1),
                new com.uber.jaeger.Configuration.ReporterConfiguration(
                        true,  // logSpans
                        config.getProperty("jaeger.reporter_host"),
                        Integer.decode(config.getProperty("jaeger.reporter_port")),
                        1000,   // flush interval in milliseconds

                        10000)  // max buffered Spans
        ).getTracer();
    }

    static Properties loadConfig() throws IOException, URISyntaxException {
        File file = new File(ComplexGreetings.class.getClassLoader().getResource("tracer_config.properties").toURI());
        FileInputStream fs = new FileInputStream(file);
        Properties config = new Properties();
        config.load(fs);
        return config;
    }
}

# OpenTracing demo and training.
## This Branch is a RPC demostration.

The main goal is to understand how to works OpenTracing with a couple of simple Spark core microservices.

## Simple Flow

![Easy diagram](https://github.com/sucius/opentracing_spark/blob/master/images/diagram.PNG "Diagram")

```mermaid
sequenceDiagram
SimpleGrettings ->> ComplexGrettings: Hello <requested name>
ComplexGrettings-->>SimpleGrettings: And Welcome <requested name>
```

# Things that you need

## Docker, of course

For this training I'm using jaeger, but you have more options: http://opentracing.io/documentation/pages/supported-tracers

> docker run -d -p 5775:5775/udp -p 16686:16686 jaegertracing/all-in-one:latest

## Install artifacts from parent project as follow :
> mvn clean install

## Running microservices
The next steep is up docker containers
> docker-compose up

## You need to do the request.

For example:
> curl http://localhost:8080/hello/Arya

You can check the result opening http://localhost:16686/ and see the simple traces in Jaeger UI.
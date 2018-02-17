# Opentracing demo.

The main goal is to understand how to works opentracing with a couple of simple Spark core microservices.

## Simple Flow

![alt text](/images/diagram.PNG "Diagram")

```mermaid
sequenceDiagram
SimpleGrettings ->> ComplexGrettings: Hello <requested name>
ComplexGrettings-->>SimpleGrettings: And Welcome <requested name>
```

And this will produce a flow chart:


# opentracing_spark

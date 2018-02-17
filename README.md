# OpenTracing demo.

The main goal is to understand how to works OpenTracing with a couple of simple Spark core microservices.

## Simple Flow

![Easy diagram](https://github.com/sucius/opentracing_spark/blob/master/images/diagram.PNG "Diagram")

```mermaid
sequenceDiagram
SimpleGrettings ->> ComplexGrettings: Hello <requested name>
ComplexGrettings-->>SimpleGrettings: And Welcome <requested name>
```




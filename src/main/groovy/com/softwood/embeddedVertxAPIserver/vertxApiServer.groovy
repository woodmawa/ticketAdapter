package com.softwood.embeddedVertxAPIserver


import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer;

// Create an HTTP server which simply returns "Hello World!" to each request.

Vertx vertx = Vertx.vertx()

println "starting api server "
HttpServer server = vertx.createHttpServer()
System.addShutdownHook { server.close {println "forced shutdown down api server "};System.exit(-1)}

server.requestHandler{req -> req.response().end("Hello World!")}.listen(8090)
println "processing requests"

println "type q to quit the server, or stop process from inside your IDE "

for (;;){
    String input
    System.in.withReader { input = it.readLine() }
    if (input[0].trim().toLowerCase() == 'q')
        break;

}


server.close {println "closed down api server "}


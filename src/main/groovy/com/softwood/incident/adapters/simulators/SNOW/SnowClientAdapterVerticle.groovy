package com.softwood.incident.adapters.simulators.SNOW

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient

class SnowClientAdapterVerticle extends AbstractVerticle implements Verticle {

    WebClient client
    String error = ""
    Vertx vertx

    void start(Future<Void> future) {
        println "starting SNOW Api Client .. "
        //client = configureHttpClient()

        println "started SNOW Api Client "
        future.complete()
    }

    void stop(Future<Void> future) {
        println "stopped SNOW server simulator "
        future.complete()

    }

    void configureHttpClient() {
        Map options = [:] //[userAgent:"", ]
        if (!vertx)
            vertx = Vertx.vertx()

        client = WebClient.create(vertx)
        client


    }

    HttpRequest<Buffer> apiAddQueryparams (String uri, queryParams = [:] ) {
        queryParams.each {
            client.get(uri).addQueryParam(it.key, it.value)

        }
        HttpRequest<Buffer> request = client.get()
    }


    HttpRequest apiGet (String uri, host="localhost", port=8081) {
        apiGet (client.get(uri), host, port)
    }

    HttpRequest apiGet (HttpRequest<Buffer> request, host="localhost", port=8081) {
        request.host(host).port(port)
        request.putHeader("accept", "application/text")
        request.method (HttpMethod.GET)
        request

    }

    void apiSend (HttpRequest<Buffer> request, Closure handler) {
        println "sending request host: ${request.host}:${request.port}" + request.uri + " to server"
        request.send {ar ->
            HttpResponse<Buffer> getResult
            if (ar.succeeded()) {
                //obtain the response
                getResult = ar.result()
            } else {
                error = ar.cause().getMessage()
            }
            handler (getResult)
        }
    }


    boolean hasError () {
        error ? true : false
    }

    void clearError () {
        error = ""
    }

}

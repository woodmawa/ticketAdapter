package com.softwood.incident.adapters.simulators.SNOW

import com.softwood.application.Application
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
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
            vertx = Application.application.vertx

        client = WebClient.create(vertx)
        client


    }

    /**
     * post messaging
     * @param uri
     * @param host
     * @param port
     * @return
     */
    HttpRequest apiJsonPost (String uri, String bodyString,  host="localhost", port=8081) {

        JsonObject jsonBody = new JsonObject(bodyString)
        apiGet (client.post(uri), jsonBody, host, port)
    }

    HttpRequest apiJsonPost (String uri, def instance,  host="localhost", port=8081) {

        JsonObject jsonBody = new JsonObject (Json.encode(instance))
        apiGet (client.post(uri), jsonBody, host, port)
    }

    HttpRequest apiJsonPost (HttpRequest<Buffer> request, JsonObject jsonBody, host="localhost", port=8081) {
        request.host(host).port(port)
        request.putHeader("accept", "application/text")
        request.putHeader("content-length", "${jsonBody.size()}" )
        request.method (HttpMethod.POST)
        request

    }

    /**
     * get messaging
     * @param uri
     * @param queryParams
     * @return
     */

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

        switch (request.method) {
            case  HttpMethod.GET :
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
                break
            case HttpMethod.POST :
                request.post {ar ->
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

    }



    boolean hasError () {
        error ? true : false
    }

    void clearError () {
        error = ""
    }

}

package com.softwood.incident.adapters.simulators.SNOW

import com.softwood.application.Application
import com.softwood.incident.adapters.IncidentTicketAdapter
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

class SnowClientAdapterVerticle extends AbstractVerticle implements Verticle, IncidentTicketAdapter {

    String name
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

    HttpRequest apiPost (String uri, JsonObject postBody,  host="localhost", port=8081) {
        apiPost (client.post(uri), postBody, host, port)
    }

    HttpRequest apiPost (String uri, String bodyString,  host="localhost", port=8081) {

        JsonObject jsonBody = new JsonObject(bodyString)
        apiPost (client.post(uri), jsonBody, host, port)
    }

    HttpRequest apiPost (String uri, def object,  host="localhost", port=8081) {

        JsonObject jsonBody = new JsonObject (Json.encode(object))
        apiPost (client.post(uri), jsonBody, host, port)
    }

    HttpRequest apiPost (HttpRequest<Buffer> request, JsonObject jsonBody, host="localhost", port=8081) {
        request.host(host).port(port)
        request.putHeader("accept", "application/text")
        request.putHeader("content-type", "application/json" )
        request.putHeader("content-length", "${jsonBody.encode().size()}" )
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

    void apiSendGet (HttpRequest<Buffer> request,  Closure handler) {
        println "sending request [$request.method] to host: ${request.host}:${request.port}" + request.uri + " to server"
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

    void apiSendPost(HttpRequest<Buffer> request, JsonObject postBody,  Closure handler) {
        request.sendJsonObject(postBody) {ar ->
            HttpResponse<Buffer> postResult
            if (ar.succeeded()) {
                //obtain the response
                postResult = ar.result()
            } else {
                error = ar.cause().getMessage()
            }
            handler (postResult)
        }
    }

        void apiSend (HttpRequest<Buffer> request, JsonObject postBody,  Closure handler) {
        println "sending request [$request.method] to host: ${request.host}:${request.port}" + request.uri + " to server"
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
                request.sendJsonObject(postBody) {ar ->
                    HttpResponse<Buffer> postResult
                    if (ar.succeeded()) {
                        //obtain the response
                        postResult = ar.result()
                    } else {
                        error = ar.cause().getMessage()
                    }
                    handler (postResult)
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

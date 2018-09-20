/*
 * Copyright 2018 author : Will woodman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.softwood.request.requestApi


import com.softwood.application.Application
import com.softwood.cmdb.cmdbApi.CmdbDbServices
import com.softwood.cmdb.views.Bearer
import com.softwood.cmdb.views.ConnectionService
import com.softwood.cmdb.views.Device
import com.softwood.cmdb.views.PackageService
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

class RequestApiServerVerticle extends AbstractVerticle implements Verticle {

    String name
     HttpServer server
    String host
    int port

    RequestDbServices requestServices

    RequestApiServerVerticle() {
        requestServices = new RequestDbServices()
    }

    void start(Future<Void> future) {
        println "starting Request server .. "
        //server = configureHttpServer()
        //server.listen(8081, "localhost")

        //SnowSimulatorIMDB snowImdb = SnowSimulatorIMDB.getInstance()
        future.complete()
    }

    void stop(Future<Void> future) {
        println "stopped Request server  "
        future.complete()

    }


    def configureHttpServer() {

        host = Application.application.binding.config.requestServer.host
        port = Application.application.binding.config.requestServer.port

        Vertx vertx = Vertx.vertx()
        HttpServer server = vertx.createHttpServer()  //start server after defining the routes?


        Router requestApiRouter = Router.router(vertx)

        /**
         * cant get two routers with different methods to listen on same URI
         * so do it as one route - but switch on method in the handler
        * get all paths and subpaths below /api/now/table/incident
         * setup a body handler to process the post bodies
         */
        requestApiRouter.route("/api/request/*")
                .handler(io.vertx.ext.web.handler.BodyHandler.create())
                .blockingHandler { routingContext ->

            def request = routingContext.request()
            HttpMethod method = request.method()

            def uri = routingContext.request().absoluteURI()

            //split uri into path segments and look at last segment matched
            String[] segments = uri.split("/")
            def trailingParam = (segments[-1] != "ci") ? segments[-1] : null //get last segment

            println "processing http [$method] Request and found trailing param as $trailingParam on uri : $uri "

            def response = routingContext.response()

            switch (method) {
                case HttpMethod.POST:
                    //get post body as Json text
                    JsonObject postBody = routingContext.getBodyAsJson()

                    def ci = processInventoryRequest (trailingParam, postBody)
                    JsonObject jsonAlarm = generateResponse(trailingParam, ci)
                    def resultBody = jsonAlarm?.encodePrettily() ?: ""

                    response.putHeader("content-type", "application/json")
                    def length = resultBody.getBytes().size() ?: 0
                    response.putHeader("content-length", "$length")

                    println "returning  Ci Post result with length $length to client"
                    response.end(resultBody)

                    break

                case HttpMethod.GET:

                    def requests = getRequestTickets (trailingParam)
                    JsonObject jsonAlarm = generateResponse(trailingParam, requests)
                    def resultBody = jsonAlarm?.encodePrettily() ?: ""

                    response.putHeader("content-type", "application/json")
                    def length = resultBody.getBytes().size() ?: 0
                    response.putHeader("content-length", "$length")

                    println "returning  Requests get result with length $length to client"
                    response.end(resultBody)

                    break

            }

        }

        server.requestHandler(requestApiRouter.&accept)
        server.listen(port, host)
        println "started RequestManagement httpServer listening on port $host:$port"
        server

    }


    private def processRequestTicket (String param, JsonObject postRequestBody) {

        //todo rewrite all this
        def ci

        switch (postRequestBody?.getString("type")) {
            case "Device" :
                ci = new Device()
                break
            case "Circuit" :
                ci = new ConnectionService()
                break
            case "Bearer" :
                ci =new Bearer()
                break
            case "PackageService" :
                ci = new PackageService()
                break

        }


        //loop through PostBody and assign all params into the ci
        ci.name = postRequestBody?.getString("name")
        /*for (entry in specDetail)
            alarm.eventCharacteristics.put (entry.key, entry.value )
         */


          //post to event bus
        requests << ci
        println "created inventory $ci, and added to IMDB Cmdb"
        ci

    }

    //Todo - process query params on the end
    private def getRequestTickets (String param) {


        requestServices.requestList ()
    }

    private JsonObject generateResponse(String param, def request) {

        JsonObject jsonObject
        if (request instanceof List) {
            JsonArray jsonArray = new JsonArray()
            request.each {req ->
                def json = req.toJson()
                jsonArray.add(json)}
            jsonObject = new JsonObject ()
            jsonObject.put ("requestList", jsonArray)
        } else {
            jsonObject = request.toJson()
        }

        jsonObject

    }
}
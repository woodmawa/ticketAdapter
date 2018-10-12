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

package com.softwood.management

import com.softwood.alarmsAndEvents.Alarm
import com.softwood.alarmsAndEvents.Event
import com.softwood.application.Application
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

import java.time.LocalDateTime

class ManagementApiServerVerticle extends AbstractVerticle implements Verticle {

    String name
     HttpServer server
    String host
    int port
    ApplicationManagement managementService = new ApplicationManagement()


    void start(Future<Void> future) {
        println "starting Alarm server .. "
        //server = configureHttpServer()
        //server.listen(8081, "localhost")

        //SnowSimulatorIMDB snowImdb = SnowSimulatorIMDB.getInstance()
        future.complete()
    }

    void stop(Future<Void> future) {
        println "stopped Alarm server  "
        future.complete()

    }


    def configureHttpServer() {

        host = Application.application.binding.config.management.host
        port = Application.application.binding.config.management.port

        Vertx vertx = Vertx.vertx()
        HttpServer server = vertx.createHttpServer()  //start server after defining the routes?


        Router managementApiRouter = Router.router(vertx)

        /**
         * cant get two routers with different methods to listen on same URI
         * so do it as one route - but switch on method in the handler
        * get all paths and subpaths below /api/now/table/incident
         * setup a body handler to process the post bodies
         */
        managementApiRouter.route("/api/management/*")
                .handler(io.vertx.ext.web.handler.BodyHandler.create())
                .blockingHandler { routingContext ->

            def request = routingContext.request()
            HttpMethod method = request.method()

            def uri = routingContext.request().absoluteURI()

            //split uri into path segments and look at last segment matched
            String[] segments = uri.split("/")
            def trailingParam = (segments[-1] != "alarm") ? segments[-1] : null //get last segment

            println "Management server, processing http [$method] request and found trailing param as '$trailingParam' on uri : $uri "

            def response = routingContext.response()

            switch (method) {
                case HttpMethod.POST:
                    //get post body as Json
                    JsonObject postBody = routingContext.getBodyAsJson()

                    JsonObject jsonResponse = processManagementRequest (routingContext, trailingParam, postBody)
                    def resultBody = jsonResponse?.encodePrettily() ?: ""

                    response.putHeader("content-type", "application/json")
                    def length = resultBody.getBytes().size() ?: 0
                    response.putHeader("content-length", "$length")

                    response.end(resultBody)

                    break

                case HttpMethod.GET:

                    JsonObject jsonResponse  = processManagementRequest (routingContext, trailingParam, new JsonObject ())
                    def resultBody = jsonResponse?.encodePrettily() ?: ""

                    response.putHeader("content-type", "application/json")
                    def length = resultBody.getBytes().size() ?: 0
                    response.putHeader("content-length", "$length")

                    response.end(resultBody)

                    break
            }

        }

        server.requestHandler(managementApiRouter.&accept)
        server.listen(port, host)
        println "started App Management httpServer listening on port $host:$port"
        server

    }


    private JsonObject processManagementRequest (RoutingContext rc, String param, JsonObject postRequestBody) {

        //build new generic alarm from postRequestBody - ignore param
        JsonObject responseBody
        Closure action

        action = managementService.actions."${param.toLowerCase()}"
        if (action) {
            println "performing management action $param"
            responseBody = action (rc, postRequestBody)
        } else {
            responseBody = new JsonObject ()
            JsonObject err = new JsonObject()
            err.put ("unknown management action", "$param")
            responseBody.put ("NoAction", err)
        }
        responseBody
    }


}
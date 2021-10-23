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
import com.softwood.cmdb.Customer
import com.softwood.cmdb.RoleType
import com.softwood.request.Request
import com.softwood.utils.JsonEncodingStyle
import com.softwood.utils.JsonUtils
import groovy.json.JsonSlurper
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router


import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

class RequestApiServerVerticle extends AbstractVerticle implements Verticle {

    String name
    HttpServer server
    String host
    int port

    RequestDbServices requestServices
    JsonUtils summaryJsonGenerator
    JsonUtils jsonGenerator
    AtomicLong sequenceGenerator

    def customersDb
    def requestsDb

    RequestApiServerVerticle() {
        requestServices = new RequestDbServices()
        customersDb = Application.application.binding.customers
        requestsDb = Application.application.binding.requests
        sequenceGenerator = Application.application.binding.config.requestServer.sequenceGenerator

        JsonUtils.Options sumOptions = new JsonUtils.Options()
        sumOptions.registerConverter(LocalDateTime) { it.toString() }
        sumOptions.excludeFieldByNames("ci")
        sumOptions.excludeNulls(true)
        sumOptions.summaryClassFormEnabled(true)
        sumOptions.setJsonEncodingStyle(JsonEncodingStyle.softwood)

        summaryJsonGenerator = sumOptions.build()

        JsonUtils.Options options = new JsonUtils.Options()
        options.setExpandLevels(2)
        options.registerConverter(LocalDateTime) { it.toString() }
        options.excludeFieldByNames("ci")
        options.excludeNulls(true)
        options.summaryClassFormEnabled(false)
        sumOptions.setJsonEncodingStyle(JsonEncodingStyle.softwood)

        jsonGenerator = options.build()

        host = Application.application.binding.config.requestServer.host
        port = Application.application.binding.config.requestServer.port
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
                    def trailingParam = (segments[-1] != "request") ? segments[-1] : null //get last segment

                    println "processing http [$method] Request and found trailing param as $trailingParam on uri : $uri "

                    def response = routingContext.response()

                    switch (method) {
                        case HttpMethod.POST:
                            //get post body as Json text
                            JsonObject postBody = routingContext.getBodyAsJson()

                            def req = processRequestTicketPost(trailingParam, postBody)
                            JsonObject jsonReq = generateResponse(trailingParam, req)
                            def resultBody = jsonReq?.encodePrettily() ?: ""

                            response.putHeader("content-type", "application/json")
                            def length = resultBody.getBytes().size() ?: 0
                            response.putHeader("content-length", "$length")

                            println "returning  Request Post result with length $length to client"
                            response.end(resultBody)

                            break

                        case HttpMethod.GET:

                            def requests = getRequestTickets(trailingParam)
                            JsonObject getResult = generateResponse(trailingParam, requests)
                            def resultBody = getResult?.encodePrettily() ?: ""

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


    private def processRequestTicketPost(String param, JsonObject postRequestBody) {

        Map graph = new JsonSlurper().parseText(postRequestBody.encode())

        def postCust = graph?.entityData?.attributes?.customer
        Customer customer

        def postRequest = graph?.entityData

        //see if we have existing customer defined
        if (postCust) {
            if (postCust?.value?.data) {
                //todo check this assumption !
                //looks like a new customer entity has been presented
                //create new customer record
                customer = new Customer()
                customer.name = postCust?.attributes?.name?.value
                customer.role = RoleType.CUSTOMER
                println "looks like a new customer record with data has been presented in post,  so create it : $customer"
            } else {
                //looks as though it references exiting customer so try a lookup
                customer = customersDb.find { it.id == postCust?.id || it.name == postCust?.name }
                if (!customer) {
                    //create new customer record
                    customer = new Customer()
                    customer.name = postCust?.name
                    customer.role = RoleType.CUSTOMER
                    println "couldnt find $postCust in customersDB, create new one as : $customer"
                    customersDb << customer
                }
            }
        }


        Request request = new Request()
        request.id = sequenceGenerator.next()
        request.status = postRequest.attributes?.status?.value
        request.priority = postRequest?.attributes?.priority?.value
        request.requestIdentifier = postRequest?.attributes?.requestIdentifier?.value
        if (postRequest?.attributes?.authorisedDate)
            request.authorisedDate = LocalDateTime.parse(postRequest?.attributes?.authorisedDate?.value)
        if (postRequest?.attributes?.requiredDate)
            request.requiredDate = LocalDateTime.parse(postRequest?.attributes?.requiredDate?.value)
        request.contactDetails = postRequest?.attributes?.contactDetails?.value
        request.title = postRequest?.attributes?.title?.value
        if (customer)
            customer.addRequest(request)

        requestsDb << request
        request


    }

    //Todo - process query params on the end
    private def getRequestTickets(String param) {

        if (param == 'count')  //not really REST more of an action but ...
            requestServices.requestListSize()
        else if (param) {
            requestServices.getRequestById(param)
        } else
            requestServices.requestList()
    }

    private JsonObject generateResponse(String param, def request) {

        JsonObject jsonObject
        if (request instanceof List) {
            def formattedResp = summaryJsonGenerator.toJson(request)

            jsonObject = new JsonObject()
            jsonObject = jsonGenerator.toJson(request, "requestList")
            //jsonObject.put ("requestList", formattedResp)
        } else if (param == "count") {
            jsonObject = jsonGenerator.toJson(request as Long, "requestListSize")
        } else {
            jsonObject = jsonGenerator.toJson(request)

        }

        jsonObject

    }
}
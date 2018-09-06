package com.softwood.incident.adapters.simulators.SNOW

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router


class SnowApiServerSimulatorVerticle extends AbstractVerticle implements Verticle {

    SnowSimulatorIMDB snowImdb = SnowSimulatorIMDB.getInstance()
    HttpServer server

    void start(Future<Void> future) {
        println "starting SNOW server simulator.. "
        //server = configureHttpServer()
        //server.listen(8081, "localhost")

        //SnowSimulatorIMDB snowImdb = SnowSimulatorIMDB.getInstance()
        future.complete()
    }

    void stop(Future<Void> future) {
        println "stopped SNOW server simulator "
        future.complete()

    }


    def configureHttpServer() {
        Vertx vertx = Vertx.vertx()
        HttpServer server = vertx.createHttpServer()  //start server after defining the routes?


        Router allApiRouter = Router.router(vertx)

        /**
         * cant get two routers with different methods to listen on same URI
         * so do it as one route - but switch on method in the handler
        * get all paths and subpaths below /api/now/table/incident
         * setup a body handler to process the post bodies
         */
        allApiRouter.route("/api/now/table/incident/*")
                .handler(io.vertx.ext.web.handler.BodyHandler.create())
//getRouter.routeWithRegex(HttpMethod.GET, "\\/api\\/.*incident\\/(?<sysId>[^\\/]+)" )
//        .failureHandler{failureRoutingContext  ->
//          String validationError = failureRoutingContext.getMessage()
//          println "failed to process request : " + failureRoutingContext.statusCode() + " with error : " +validationError}
//          failureRoutingContext,response().setStatusCode (400).end()
                .blockingHandler { routingContext ->

            def request = routingContext.request()
            HttpMethod method = request.method()

            def uri = routingContext.request().absoluteURI()

            //split uri into path segments and look at last segment matched
            String[] segments = uri.split("/")
            def trailingParam = (segments[-1] != "incident") ? segments[-1] : null //get last segment

            println "processing http [$method] request and found trailing param as $trailingParam on uri : $uri "


            def response = routingContext.response()

            switch (method) {
                case HttpMethod.GET:  //todo getList processing
                    JsonObject jsonTicket = generateGetResponse(snowImdb, trailingParam)
                    def resultBody = jsonTicket?.encodePrettily() ?: ""

                    response.putHeader("content-type", "application/json")
                    def length = resultBody.getBytes().size() ?: 0
                    response.putHeader("content-length", "$length")

                    println "returning  get result with length $length to client"
                    response.end(resultBody)

                    break

                case HttpMethod.POST:
                    //get post body as Json text
                    JsonObject postBody = routingContext.getBodyAsJson()

                    JsonObject jsonTicket = generatePostResponse(snowImdb, trailingParam, postBody)
                    def resultBody = jsonTicket?.encodePrettily() ?: ""

                    response.putHeader("content-type", "application/json")
                    def length = resultBody.getBytes().size() ?: 0
                    response.putHeader("content-length", "$length")

                    println "returning  Post result with length $length to client"
                    response.end(resultBody)

                    break
            }

        }

        server.requestHandler(allApiRouter.&accept)
        server.listen(8081, "localhost")
        println "started SNOW httpServer listening on port localhost:8081"
        server

    }

    private JsonObject generateGetResponse(snowImdb, String param) {
        def ticket 
        if (param == null) {
            ticket = snowImdb.getLatestTicket()
        } else if (param == 'list') {
            JsonArray ticketList = snowImdb.listTickets()
            assert ticketList.size() == snowImdb.count()
        } else {
            println "get ticket from IMDB using sysid : $param"
            ticket = snowImdb.getTicket(param)
        }

    }

    private JsonArray generateGetListResponse(snowImdb, String param) {
        if (param == 'list') {
            JsonArray ticketList = snowImdb.listTickets()
            assert ticketList.size() == snowImdb.count()
        }
    }

    private JsonObject generatePostResponse(snowImdb, String param, JsonObject postRequestBody) {

        long count = snowImdb.count()
        println "processing http POST request, current IMDB record counts is $count "


        JsonObject responseBody = snowImdb.createTicket(postRequestBody)
        count = snowImdb.count()
        println "returning  post createTicket result, new record count in IMDB $count"
        responseBody

    }
}
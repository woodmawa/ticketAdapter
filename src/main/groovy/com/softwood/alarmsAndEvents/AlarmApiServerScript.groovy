package com.softwood.alarmsAndEvents

import com.softwood.application.Application
import com.softwood.incident.adapters.simulators.SNOW.SnowSimulatorIMDB
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

import java.time.LocalDateTime

//this script just calculates packet size - rest disabled

def alarm = new Alarm()
alarm.ciReference = "192.168"
alarm.id = 27
alarm.name = "my alarm"
alarm.type = "critical"
alarm.eventCharacteristics.put('threshold', '100% ')

JsonObject result = alarm.toJson2()
println "json result " + result.encodePrettily()

System.exit(0)

def resultLength = """{
  "name" : "my alarm",
  "ciReference" : "192.168.1.24",
  "id" : "101",
  "type" : "critical",
  "details" : {"threshold alert" : "exceeded critical temperature"}
}"""

println resultLength.size()

System.exit(0)

Vertx vertx = Vertx.vertx()
HttpServer server = vertx.createHttpServer()  //start server after defining the routes?


Router alarmApiRouter = Router.router(vertx)

int baseAlarmId = 100

/**
 * cant get two routers with different methods to listen on same URI
 * so do it as one route - but switch on method in the handler
 * get all paths and subpaths below /api/now/table/incident
 * setup a body handler to process the post bodies
 */
alarmApiRouter.route("/api/alarm/*")
        .handler(BodyHandler.create())
        .blockingHandler { routingContext ->

            def request = routingContext.request()
            HttpMethod method = request.method()

            def uri = routingContext.request().absoluteURI()

            //split uri into path segments and look at last segment matched
            String[] segments = uri.split("/")
            def trailingParam = (segments[-1] != "alarm") ? segments[-1] : null //get last segment

            println "processing http [$method] request and found trailing param as $trailingParam on uri : $uri "


            def response = routingContext.response()

            switch (method) {
                case HttpMethod.POST:
                    //get post body as Json text
                    JsonObject postBody = routingContext.getBodyAsJson()

                    JsonObject newAlarm = generatePostResponse(trailingParam, postBody)
                    def resultBody = newAlarm?.encodePrettily() ?: ""

                    response.putHeader("content-type", "application/json")
                    def length = resultBody.getBytes().size() ?: 0
                    response.putHeader("content-length", "$length")

                    println "returning  Post result with length $length to client"
                    response.end(resultBody)

                    break
            }

        }


JsonObject generatePostResponse(String param, JsonObject postRequestBody) {

    Alarm alarm = new Alarm(new Event())

    alarm.ciReference = postRequestBody.ciReference
    alarm.createdDate = LocalDateTime.now()
    alarm.id = baseAlarmId++
    JsonObject responseBody = snowImdb.createTicket(postRequestBody)
    println "returning  post createTicket result, new record count in IMDB $count"
    responseBody

}

//
server.requestHandler(alarmApiRouter.&accept)

def host = Application.application.binding.config.apiServer.host
def port = Application.application.binding.config.apiServer.port

server.listen(port, host)

println "started Api httpServer listening on $port $host:"

//older stuff to check back on

/*
Router getRouter = Router.router(vertx)
getRouter.route (HttpMethod.GET, "/api/now/table/incident/*")
//getRouter.routeWithRegex(HttpMethod.GET, "\\/api\\/.*incident\\/(?<sysId>[^\\/]+)" )
//        .failureHandler{failureRoutingContext  ->
//          String validationError = failureRoutingContext.getMessage()
//          println "failed to process request : " + failureRoutingContext.statusCode() + " with error : " +validationError}
//          failureRoutingContext,response().setStatusCode (400).end()
        .blockingHandler { routingContext ->


    def request = routingContext.request()
    def sysId //= routingContext.request().getParam ("params0")- to hard - used brute force string manip

    def uri = routingContext.request().absoluteURI()

    println "processing http GET request found sys_id as $sysId on uri : $uri "


    String[] segments = uri.split("/")
    sysId = (segments[-1] != "incident") ? segments[-1]: null //get last segment

    //println "processing http GET request on uri : $uri "

    JsonObject ticket
    if (sysId == null) {
        ticket = snowImdb.getLatestTicket()
    }else if (sysId == 'list') {
        JsonArray ticketList = snowImdb.listTickets()
        assert ticketList.size() == snowImdb.count()
    } else {
        println "get ticket from IMDB using sysid $sysId"
        ticket = snowImdb.getTicket(sysId)
    }


    assert ticket

    def response = routingContext.response()
    response.putHeader ("content-type", "application/json")
    def length = ticket?.encodePrettily().getBytes().size() ?: 0
    response.putHeader("content-length", "$length")
    def resultBody = ticket?.encodePrettily() ?: ""

    println "returning  get result with length $length to client"
    response.end (resultBody)
}

Router postRouter = Router.router(vertx)
postRouter.post("/api/now/table/incident/*")
        .handler(BodyHandler.create())  //process the post payload to be ready
//        .failureHandler{failureRoutingContext  ->
//          String validationError = failureRoutingContext.getMessage()
//          println "failed to process request : " + failureRoutingContext.statusCode() + " with error : " +validationError}
//          failureRoutingContext,response().setStatusCode (400).end()
        .blockingHandler { routingContext ->

    println "processing http POST request, current IMDB record counts is $count "

    def request = routingContext.request()

    long count = snowImdb.count()
    def sysId

    def uri = routingContext.request().absoluteURI()

    String[] segments = uri.split("/")
    sysId = (segments[-1] != "incident") ? segments[-1]: null //get last segment

    println "processing http GET request found sys_id as $sysId on uri : $uri "

    JsonObject postBody = routingContext.getBodyAsJson()
    //todo : going to ignore post body validations for now can be added later for hardening
    String postBodyText = routingContext.getBodyAsJson().encodePrettily()
    println "received post body as : $postBodyText"

    JsonObject responseBody = snowImdb.createTicket(postBody)

    String responseBodyText = responseBody?.encodePrettily()
    int length = responseBodyText?.getBytes().size() ?: 0

    count = snowImdb.count()
    println "returning  post result with length $length to client, new record count in IMDB $count"

    def response = routingContext.response()
    response.putHeader ("content-type", "application/json")
    response.putHeader ("content-length", "$length" )
    //response.setChunked(true)
    response.end (responseBodyText)

}

server.requestHandler(getRouter.&accept)
server.requestHandler(postRouter.&accept)
*/

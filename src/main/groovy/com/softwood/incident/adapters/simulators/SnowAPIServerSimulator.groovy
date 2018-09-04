package com.softwood.incident.adapters.simulators

import io.netty.buffer.ByteBuf
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.time.LocalDateTime

//priority, severity being assigned by platform ?
Map requestBody = new HashMap ([customer:"ACME shops", short_description:"my phone is broken", category:"phone", urgency:"1", impact:"high"])
JsonObject createIncident = new JsonObject(requestBody)
String json = createIncident.encodePrettily()

//def reqBodyLen = json.getBytes(Charset.forName("UTF-16")).size()
def reqBodyLen = json.getBytes().size()

println "content lengh $reqBodyLen with body as json $json"

String  resultString = """
{
  "result": {
    "upon_approval": "proceed",
    "location": "",
    "expected_start": "",
    "reopen_count": "0",
    "close_notes": "",
    "additional_assignee_list": "",
    "impact": "${requestBody.impact}",
    "urgency": "${requestBody.urgency}",
    "correlation_id": "",
    "sys_tags": "",
    "sys_domain": {
      "link": "https://instance.service-now.com/api/now/table/sys_user_group/global",
      "value": "global"
    },
    "description": "",
    "group_list": "",
    "priority": "3",
    "delivery_plan": "",
    "sys_mod_count": "0",
    "work_notes_list": "",
    "business_service": "",
    "follow_up": "",
    "closed_at": "",
    "sla_due": "",
    "delivery_task": "",
    "sys_updated_on": "${LocalDateTime.now()}",
    "parent": "",
    "work_end": "",
    "number": "INC0010002",
    "closed_by": "",
    "work_start": "",
    "calendar_stc": "",
    "category": "${requestBody.category}",  //{inqury } 
    "business_duration": "",
    "incident_state": "1",
    "activity_due": "",
    "correlation_display": "",
    "company": "${requestBody.customer}",
    "active": "true",
    "due_date": "",
    "assignment_group": {
      "link": "https://instance.service-now.com/api/now/table/sys_user_group/287ebd7da9fe198100f92cc8d1d2154e",
      "value": "287ebd7da9fe198100f92cc8d1d2154e"
    },
    "caller_id": "",
    "knowledge": "false",
    "made_sla": "true",
    "comments_and_work_notes": "",
    "parent_incident": "",
    "state": "1",
    "user_input": "",
    "sys_created_on": "${LocalDateTime.now()}",
    "approval_set": "",
    "reassignment_count": "0",
    "rfc": "",
    "child_incidents": "0",
    "opened_at": "${LocalDateTime.now()}",
    "short_description": "${requestBody.short_description}",
    "order": "",
    "sys_updated_by": "admin",
    "resolved_by": "",
    "notify": "1",
    "upon_reject": "cancel",
    "approval_history": "",
    "problem_id": "",
    "work_notes": "",
    "calendar_duration": "",
    "close_code": "",
    "sys_id": "c537bae64f411200adf9f8e18110c76e",
    "approval": "not requested",
    "caused_by": "",
    "severity": "3",
    "sys_created_by": "admin",
    "resolved_at": "",
    "assigned_to": "",
    "business_stc": "",
    "wf_activity": "",
    "sys_domain_path": "/",
    "cmdb_ci": "",
    "opened_by": {
      "link": "https://instance.service-now.com/api/now/table/sys_user/6816f79cc0a8016401c5a33be04be441",
      "value": "6816f79cc0a8016401c5a33be04be441"
    },
    "subcategory": "",
    "rejection_goto": "",
    "sys_class_name": "incident",
    "watch_list": "",
    "time_worked": "",
    "contact_type": "phone",
    "escalation": "0",
    "comments": ""
  }
}
"""

JsonObject createIncidentResponse  = new JsonObject(resultString)
createIncidentResponse

def jsonResult = createIncidentResponse.encodePrettily()

def respBodyLen = jsonResult.getBytes().size()

println "response content lengh $respBodyLen with body as json $json"
println jsonResult

SnowSimulatorIMDB snowImdb = new SnowSimulatorIMDB()


Vertx vertx = Vertx.vertx()
HttpServer server = vertx.createHttpServer()  //start server after defining the routes?


Router allRouter = Router.router(vertx)

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

    String[] segments = uri.split("/")
    sysId = (segments[-1] != "incident") ? segments[-1]: null //get last segment

    println "processing http GET request found sys_id as $sysId on uri : $uri "
    //println "processing http GET request on uri : $uri "

    JsonObject ticket
    if (sysId == null) {
        ticket = snowImdb.getLatestTicket()
    }else {
        println "get ticket from IMDB using sysid $sysId"
        ticket = snowImdb.getTicket(sysId)
    }

    assert ticket

    def response = routingContext.response()
    response.putHeader ("content-type", "application/json")
    def length = ticket?.encodePrettily().getBytes().size() ?: 0
    response.putHeader("content-length", "$length")
    def resultBody = ticket?.encodePrettily() ?: ""

    println "returning  result with length $length to client"
    response.end (resultBody)
}

Router postRouter = Router.router(vertx)
postRouter.post("/api/now/table/incident/*")
//        .failureHandler{failureRoutingContext  ->
//          String validationError = failureRoutingContext.getMessage()
//          println "failed to process request : " + failureRoutingContext.statusCode() + " with error : " +validationError}
//          failureRoutingContext,response().setStatusCode (400).end()
        .blockingHandler { routingContext ->
    def request = routingContext.request()

    println "processing http POST request "

    def sysId

    def uri = routingContext.request().absoluteURI()

    String[] segments = uri.split("/")
    sysId = (segments[-1] != "incident") ? segments[-1]: null //get last segment

    println "processing http GET request found sys_id as $sysId on uri : $uri "
    //println "processing http GET request on uri : $uri "

    String reply = "howdi will"
    def response = routingContext.response()
    response.putHeader ("content-type", "text/plain")
    //response.putHeader ("content-length", "${reply.getBytes(Charset.forName("UTF-16")).size()}" )
    //response.setChunked(true)
    //response.write (createIncidentResponse.encodePrettily())
    response.end (reply)

    //response.write (reply)
    //routingContext.response().end ()  //end handler chaining
}

//now try and setup the route for API path interception
allRouter.route ( "/api/now/table/incident")
        .handler(BodyHandler.create())
        .failureHandler{failureRoutingContext  -> println "failed to process request : " + failureRoutingContext.statusCode() + " with resp body : " + failureRoutingContext.getBodyAsJson().encodePrettily()}
        .blockingHandler { routingContext ->

    def request  = routingContext.request()
    HttpMethod method = request.method()

    def response = routingContext.response()
    response.putHeader ("content-type", "text/plain")

    def uri = routingContext.request().absoluteURI()
    switch (method) {
        case HttpMethod.GET:
            println "processing a resource GET on uri : $uri "

            JsonObject ticket = snowImdb.getLatestTicket()
            response.end (ticket.encodePrettily())
            break

        case HttpMethod.POST:

            //request.bodyHandler(BodyHandler.create())
            String bodyEnc = routingContext.getBodyAsJson().encodePrettily()

            println "processing a resource POST on uri : $uri"

            println "post request received post data : " + bodyEnc

            response.end ("(POST) howdi will")
            break
    }

}

server.requestHandler(getRouter.&accept)

//server.requestHandler(allRouter.&accept)
server.listen(8081, "localhost")

println "started SNOW httpServer listening on port localhost:8081"

//older stuff to check back on
/**
Router postRouter = Router.router(vertx)
postRouter.post("/api/now/table/incident/new").blockingHandler { routingContext ->
//postRouter.route (HttpMethod.POST, "/api/now/table/incident/new").blockingHandler { routingContext ->
    def request = routingContext.request()

    println "processing http POST request "

    String reply = "howdi will"
    def response = routingContext.response()
    response.putHeader ("content-type", "text/plain")
    //response.putHeader ("content-length", "${reply.getBytes(Charset.forName("UTF-16")).size()}" )
    //response.setChunked(true)
    //response.write (createIncidentResponse.encodePrettily())
    response.end (reply)

    //response.write (reply)
    //routingContext.response().end ()  //end handler chaining
}.failureHandler{failureRoutingContext  ->
    println "Post Error handler: write failed : " + failureRoutingContext.statusCode() + " with resp body : " + failureRoutingContext.bodyAsString()
}

Router getRouter = Router.router(vertx)
getRouter.route (HttpMethod.GET, "/api/now/table/incident").blockingHandler { routingContext ->

    def request = routingContext.request()

    println "processing http GET request "

    String reply = "(GET) howdi will"
    def response = routingContext.response()
    response.putHeader ("content-type", "text/plain")
    //response.putHeader ("content-length", "${reply.getBytes(Charset.forName("UTF-16")).size()}" )
    //response.setChunked(true)
    //response.write (createIncidentResponse.encodePrettily())
    response.end (reply)
    //routingContext.response().end ()  //end handler chaining
}.failureHandler{failureRoutingContext  ->
    println "Get Error handler (GET): write failed : " + failureRoutingContext.statusCode() + " with resp body : " + failureRoutingContext.bodyAsString()
}

 //server.requestHandler(postRouter.&accept)
 //server.requestHandler(getRouter.&accept)

 */
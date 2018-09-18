package com.softwood.embeddedVertxAPIserver

import io.vertx.core.MultiMap
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.TemplateHandler
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

// Create an HTTP server which simply returns "Hello World!" to each request.

Vertx vertx = Vertx.vertx()

println "starting api server "

HttpServerOptions options = new HttpServerOptions ([host:"localhost", port:8080])
HttpServer server = vertx.createHttpServer(options)

//System.addShutdownHook { server.close {println "forced shutdown down api server "}

//create a thymeleaf template handler passing in the correct engine
def engine = ThymeleafTemplateEngine.create()
def tymeleafHandler = TemplateHandler.create (engine)

def index = """
<html>
<form align="center" method="POST">
<p>hello william, basic form with submit </p>
Name: <input type="text" name="name.name" /> </br>
<input type = "submit" value= "Submit" />
</form>

</html>
"""

def postForm = """
<html>
<form align="center" method="POST">
<p>hello william, basic form with submit </p>
Name: <input type="text" name="name.name" /> </br>
<input type = "submit" value= "Submit" />
</form>

</html>
"""

Router reqApiRouter = Router.router(vertx)

reqApiRouter.route("/*")
        .handler(io.vertx.ext.web.handler.BodyHandler.create())
        //.handler(tymeleafHandler)
        .blockingHandler { ctx ->

    def request = ctx.request()
    HttpMethod method = request.method()

    def uri = ctx.request().absoluteURI()
    def response = ctx.response()

    Controller controller = resolveController (uri)
    //look at last name but one

    response.putHeader("content-type", "text/html")

    //write a tag into context for thymeleaf
    ctx.put ("welcome", "hello william, basic form with submit" )

    if (method == HttpMethod.GET) {
        println "received get"

        //request.response().end(postForm)  //reply with form from server
        //engine.
        engine.render (ctx, "views/requests/index.html") {res->
            if (res.succeeded()) {
                ctx.response().end(res.result())
            } else {
                ctx.fail(res.cause())
            }
        }
    } else if (method == HttpMethod.POST) {

        String body = ctx.getBodyAsString()

        MultiMap frmAtt = request.formAttributes()
        def params = frmAtt.asList()
        request.response().end("read params as $params")  //reply with form from server


        println "received post with body as :  $body, and form atts as $params"

        }
    }

server.requestHandler(reqApiRouter.&accept)  //set handler to be notified
server.listen(8080)

println "processing requests"

println "type q to quit the server, or stop process from inside your IDE "

for (;;){
    String input
    System.in.withReader { input = it.readLine() }
    if (input[0].trim().toLowerCase() == 'q')
        break;

}


server.close {println "closed down api server "}


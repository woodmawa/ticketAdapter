package com.softwood.application

import com.softwood.alarmsAndEvents.Alarm
import com.softwood.alarmsAndEvents.Event
import com.softwood.incident.ManageIncidentFacadeService
import com.softwood.incident.adapters.AdapterFactory
import com.softwood.incident.adapters.AdapterFactoryType
import com.softwood.incident.adapters.MailAdapterPlugin
import com.softwood.incident.adapters.simulators.SNOW.SnowApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowClientAdapterVerticle
import io.vertx.core.Verticle
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse

class Application {
    public static void main (args) {

        ConfigurableProjectApplication application = ProjectApp.run (Application, args)
        def vertx = application.vertx

        //application.vertx.deployVerticle(SnowApiServerSimulatorVerticle as Verticle)
        //application.vertx.deployVerticle(SnowClientAdapterVerticle as Verticle)

        SnowApiServerSimulatorVerticle server = AdapterFactory.newAdapter ("SNOW", AdapterFactoryType.server)
        server.configureHttpServer()

        SnowClientAdapterVerticle client = AdapterFactory.newAdapter ("SNOW", AdapterFactoryType.client)
        client.configureHttpClient()

        HttpRequest request = client.apiGet("/api/now/table/incident")
        client.apiSend (request) {ar ->
            if (ar.statusCode() == 200)
                println "Snow api got response " + ar.bodyAsJsonObject().encodePrettily()
            else
                println "status: "+ ar.statusMessage()
        }


        Thread.sleep 1000
        System.exit(0)

        /*com.softwood.incident.adapters.MailAdapterPlugin mail = new MailAdapterPlugin()

        Map incDetails = [title:"help me", description: "its broke again", item:"damn printer",
                          urgency:"high", requester:"Will Woodman", customer:"softwood", site:"home",
                        priority : 'high', severity : 'high', urgency: 'PDQ']

        def body  = mail.generateFormattedMailBody(incDetails)
        mail.sendMail("new incident", "$body", "will.woodman@outlook.com")
    */

        //dagger IoC not working yet
        def incidentProcessor = new ManageIncidentFacadeService(application)


        Event event = new Event (id:1, type:'critical', ciReference:"192.168.1.24", name:"Temperature Threshold Breached ")

        Alarm alarm = new Alarm (application, event)

        alarm.generateAlarm()

        println "resting "
        Thread.sleep(2000)

    }
}

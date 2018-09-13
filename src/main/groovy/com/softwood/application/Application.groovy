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
package com.softwood.application

import com.softwood.alarmsAndEvents.Alarm
import com.softwood.alarmsAndEvents.AlarmApiServerVerticle
import com.softwood.alarmsAndEvents.AlarmMessageCodec
import com.softwood.alarmsAndEvents.Event
import com.softwood.incident.ManageIncidentFacadeService
import com.softwood.incident.adapters.AdapterFactory
import com.softwood.incident.adapters.AdapterFactoryType
import com.softwood.incident.adapters.MailAdapterPlugin
import com.softwood.incident.adapters.simulators.SNOW.SnowApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowClientAdapterVerticle
import com.softwood.management.ManagementApiServerVerticle
import io.vertx.core.Verticle
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse

class Application {
    //frig to get round dependency injection issue with dagger, just store on the Application directly
    static ConfigurableProjectApplication application

    public static void main (args) {

        application = ProjectApp.run (Application, args)
        def vertx = application.vertx


        /*com.softwood.incident.adapters.MailAdapterPlugin mail = new MailAdapterPlugin()

        Map incDetails = [title:"help me", description: "its broke again", item:"damn printer",
                          urgency:"high", requester:"Will Woodman", customer:"softwood", site:"home",
                        priority : 'high', severity : 'high', urgency: 'PDQ']

        def body  = mail.generateFormattedMailBody(incDetails)
        mail.sendMail("new incident", "$body", "will.woodman@outlook.com")
    */

        def manHost = Application.application.binding.config.management.host
        def manPort = Application.application.binding.config.management.port
        def managementServer = new ManagementApiServerVerticle()
        managementServer.configureHttpServer()
        println "started management actions listener service on : $manHost:$manPort "

        //register the Alarm codec with the event bus - one time registration
        vertx?.eventBus()?.registerDefaultCodec(Alarm.class, new AlarmMessageCodec())


        def alarmHost = Application.application.binding.config.alarmServer.host
        def alarmPort = Application.application.binding.config.alarmServer.port
        def alarmServer = new AlarmApiServerVerticle ()
        alarmServer.configureHttpServer()
        println "started Alarm Api Server listener service on : $manHost:$manPort "

        def simulatedTicketServerEnabled = application.binding.config.ticketAdapter.simulatorEnabled

        if (simulatedTicketServerEnabled) {
            String flavour = application.binding.config.ticketAdapter.system
            println "Application startup: app has been configured to start with a simulated server"
            def server = AdapterFactory.newAdapter(flavour, AdapterFactoryType.server)
        }

        def incidentProcessor = new ManageIncidentFacadeService()


        /* Event event = new Event (id:1, type:'critical', ciReference:"192.168.1.24", name:"Temperature Threshold Breached ")

        Alarm alarm = new Alarm (event)

        alarm.generateAlarm() */

        println "resting "

        //Thread.sleep(5000)
        //println "closing down "

        //System.exit(0)
    }
}

//application.vertx.deployVerticle(SnowApiServerSimulatorVerticle as Verticle)
//application.vertx.deployVerticle(SnowClientAdapterVerticle as Verticle)
/*
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
*/
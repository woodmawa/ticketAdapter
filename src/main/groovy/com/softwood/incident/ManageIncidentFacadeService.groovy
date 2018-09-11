package com.softwood.incident

import com.softwood.application.Application
import com.softwood.application.ConfigurableProjectApplication
import com.softwood.alarmsAndEvents.Alarm
import com.softwood.bus.HackEventBus
import com.softwood.cmdb.ConfigurationItem
import com.softwood.cmdb.Site
import com.softwood.cmdb.views.Device
import com.softwood.incident.adapters.AdapterFactory
import com.softwood.incident.adapters.AdapterFactoryType
import com.softwood.incident.incidentFacadeProcessingCapabilities.CiContextResolver
import com.softwood.incident.incidentFacadeProcessingCapabilities.FacadeRouter
import groovy.json.JsonSlurper
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest

import javax.annotation.PostConstruct
import javax.inject.Inject


class ManageIncidentFacadeService {

    //constructor injection not working - frig
    @Inject ConfigurableProjectApplication app = Application.application

    //todo make this more dynamic later
    FacadeRouter router = new FacadeRouter ()
    CiContextResolver resolver = new CiContextResolver ()

    ManageIncidentFacadeService () {
        def vertx = app.vertx

        def vEventBus = vertx.eventBus()

        //determine which client adapter for get from factory, and save into the binding
        String system = Application.application.binding.config.ticketAdapter.system
        def ticketAdapter = AdapterFactory.newAdapter(system, AdapterFactoryType.client)
        Application.application.binding.clientTicketAdapter = ticketAdapter

        //setup the consumer endpoint to be invoked on inbound message
        Closure handler = this.&vertxOnCpeAlarm
        vEventBus.consumer("cpeAlarm", handler)
    }


    //handler for alarms published from messaging system, using vertx event messaging
    void vertxOnCpeAlarm (message) {
        Alarm alarm = message.body()
        println "vertx MIFS tracing> on event closure: received alarm $alarm on topic $message.address"
        def matchedCi = router.route (alarm)  //should match on instances of Device
        println "vertx MIFS tracing> matched alarm ciReference to ci : $matchedCi"

        matchedCi.each { Device  dci ->

            def ci = dci.ci

            def ticketAdapter = router.route(ci)
            println "vertx MIFS tracing> resolved ticket adapter to use as : $ticketAdapter, for ci : $ci"

            //fixed flow model at present
            def ticket = new IncidentTicket (title: "my $ci.category, is broken" ) // as Json - ticketAdapter.createTicket ()
            ticket.customerName = ci.customer.name
            ticket.siteName = ci.site?.name
            ticket.sitePostalCode = ci.site?.postalCode
            ticket.urgency = "1"
            ticket.priority = "high"
            ticket.severity = "high"
            ticket.impact = "cant trade"
            ticket.item = ci.name
            ticket.requester = "will.woodman@techmahindra.com"
            ticket.category = ci.category
            ticket.description = """
received alarm details on :
${alarm.ciReference}, type $alarm.type,  with event charactistics 
${alarm.eventCharacteristics}
"""

            JsonObject postBody = ticket.asJson()
            def text = postBody.encodePrettily()
            println "postBody as $text"

             def stem = Application.application.binding.uriApiStemPath
            HttpRequest request = ticketAdapter.apiPost ("$stem/incident", postBody)
            ticketAdapter.apiSendPost (request, postBody) {ar ->
                if (ar.statusCode() == 200)
                    println "vertx MIFS tracing> Snow api POST request received response " + ar.bodyAsJsonObject().encodePrettily()
                else
                    println "vertx MIFS tracing> error, status: "+ ar.statusMessage()
            }

        }

        println "vertx MIFS tracing> finished OnCpeAlarm event: done Alarm processing for $alarm"

    }
}

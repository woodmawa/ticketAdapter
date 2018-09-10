package com.softwood.incident

import com.softwood.application.Application
import com.softwood.application.ConfigurableProjectApplication
import com.softwood.alarmsAndEvents.Alarm
import com.softwood.bus.HackEventBus
import com.softwood.cmdb.ConfigurationItem
import com.softwood.cmdb.views.Device
import com.softwood.incident.incidentFacadeProcessingCapabilities.CiContextResolver
import com.softwood.incident.incidentFacadeProcessingCapabilities.FacadeRouter
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
            println "vertx MIFS tracing> resolved ticket adapter to use as : $ticketAdapter"

            //fixed flow model at present
            //def ticket = new Ticket () as Json - ticketAdapter.createTicket ()
            //ticket.setContextDetails (ci)
            //ticket.setAlarmDetails (alarm)
            //ticketAdapter.apiPost(tciket)
            //HttpRequest request = client.apiGet("/api/now/table/incident")
            HttpRequest request = ticketAdapter.apiGet ("/api/now/table/incident")
            ticketAdapter.apiSend (request) {ar ->
                if (ar.statusCode() == 200)
                    println "vertx MIFS tracing> Snow api GET request received response " + ar.bodyAsJsonObject().encodePrettily()
                else
                    println "vertx MIFS tracing> error, status: "+ ar.statusMessage()
            }
            //todo - write the post action now for the alarm

        }

        println "vertx MIFS tracing> finished OnCpeAlarm event: done Alarm processing for $alarm"

    }
}

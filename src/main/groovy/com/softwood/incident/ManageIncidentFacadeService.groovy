package com.softwood.incident

import com.softwood.application.Application
import com.softwood.application.ConfigurableProjectApplication
import com.softwood.alarmsAndEvents.Alarm
import com.softwood.bus.HackEventBus
import com.softwood.cmdb.ConfigurationItem
import com.softwood.cmdb.views.Device
import com.softwood.incident.incidentFacadeProcessingCapabilities.CiContextResolver
import com.softwood.incident.incidentFacadeProcessingCapabilities.FacadeRouter

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
        println "vertx MIFS, on event closure: received alarm $alarm on topic $message.address"
        def matchedCi = router.route (alarm)  //should match on instances of Device

        matchedCi.each { Device  dci ->

            def ci = dci.ci
            def ticketAdapter = router.route(ci)

            //fixed flow model at present
            //def ticket = new Ticket () as Json - ticketAdapter.createTicket ()
            //ticket.setContextDetails (ci)
            //ticket.setAlarmDetails (alarm)
            //ticketAdapter.apiPost(tciket)
            def result = ticketAdapter.apiGet ("/api/now/table/incident")
            println "on routed alarm : result from get was $result"
        }

    }
}

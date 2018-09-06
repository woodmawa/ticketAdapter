package com.softwood.incident

import com.softwood.application.ConfigurableProjectApplication
import com.softwood.alarmsAndEvents.Alarm
import com.softwood.bus.HackEventBus
import com.softwood.cmdb.ConfigurationItem
import com.softwood.incident.incidentFacadeProcessingCapabilities.CiContextResolver
import com.softwood.incident.incidentFacadeProcessingCapabilities.FacadeRouter

import javax.annotation.PostConstruct
import javax.inject.Inject


class ManageIncidentFacadeService {

    @Inject ConfigurableProjectApplication app

    //todo make this more dynamic later
    FacadeRouter router = new FacadeRouter ()
    CiContextResolver resolver = new CiContextResolver ()

    //constructor injection not working - frig
    ManageIncidentFacadeService (ConfigurableProjectApplication app) {
        this()  //now call default constructor
        this.app = app
        def vertx = app.vertx

        def vEventBus = vertx.eventBus()

        Closure handler = this.&vertxOnCpeAlarm
        vEventBus.consumer("cpeAlarm", handler)
    }

    ManageIncidentFacadeService () {

    }

    //handler for alarms published from messaging system, using vertx event messaging
    void vertxOnCpeAlarm (message) {
        Alarm alarm = message.body()
        println "vertx MIFS, on event closure: received alarm $alarm on topic $message.address"
        ConfigurationItem ci = router.route (alarm)
        def ticketAdapter = router.route (ci)


        //fixed flow model at present
        def ticket = ticketAdapter.createTicket ()
        ticket.setContextDetails (ci)
        ticket.setAlarmDeatils (alarm)
        ticket.send ()

    }
}

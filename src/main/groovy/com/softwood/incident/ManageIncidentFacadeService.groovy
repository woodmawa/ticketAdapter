package com.softwood.incident

import com.softwood.Application.ConfigurableProjectApplication
import com.softwood.alarmEvent.Alarm
import com.softwood.bus.HackEventBus


import javax.annotation.PostConstruct
import javax.inject.Inject


class ManageIncidentFacadeService /*implements EventBusAware */{

    @Inject ConfigurableProjectApplication app

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
        //Closure handler = this.&onCpeAlarm
        //HackEventBus.subscribe ("cpeAlarm", handler) //temp hack*/
        //println "subscribed on 'cpeAlarm'"
        //fix all these blasted event Bus's
        //eventBus.consumer("cpeAlarm", handler)
        /*eventBus.consumer("cpeAlarm") {message ->
            println "message is of class : " + message.getClass()

            String bod  = message.body()
            println "MIFS, on event closure: received alarm $bod on topic 'cpeAlarm'"
        }*/
    }


    //get message from eventBus
    void onCpeAlarm (alarm, topic) {

        println "MIFS, on event closure: received alarm $alarm on topic $topic"
    }

    //handler for vertx event messaging
    void vertxOnCpeAlarm (message) {
        Alarm alarm = message.body()
        println "vertx MIFS, on event closure: received alarm $alarm on topic $message.address"
    }
}

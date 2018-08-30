package com.softwood.incident

import com.softwood.bus.HackEventBus
import grails.events.annotation.Subscriber
import grails.events.bus.EventBusAware

import javax.annotation.PostConstruct


class ManageIncidentFacadeService implements EventBusAware {

    //could have just used @subscribe tx - but wanted to see the workings
    //@PostConstruct - needs a framework - call manually for now
    void init () {
        /*println "subscribe on 'cpeAlarm'"
        Closure handler = this.&onCpeAlarm
        eventBus.subscribe("cpeAlarm", handler)
        //HackEventBus.subscribe ("cpeAlarm", handler) //temp hack
        */
    }

    @Subscriber ('cpeAlarm')
    void onCpeAlarm (alarm, topic=null) {
        println "MIFS, on event closure: received alarm $alarm on topic $topic"
    }
}

package com.softwood.alarmEvent

import com.softwood.bus.HackEventBus
import grails.events.EventPublisher
import grails.events.annotation.Publisher

//import grails.events.annotation.*


class Alarm implements EventPublisher, Serializable{

    //we want Alarm to be a flavour of event so use delegation
    @Delegate final Event event
    Alarm (event) {
        this.event = event
    }

    //could use @Publisher ('<name>') defaults same as method
    @Publisher('cpeAlarm')
    def generateAlarm () {
        println "alarm: publish alarm on 'cpeAlarm'"
        //publish ("cpeAlarm", this)  //manually publish event on 'cpeAlarm' topic
        //HackEventBus.publish ("cpeAlarm", this) //hack
        this
    }

    String toString () {
        "Alarm (id:$event.id, type:$event.type, name:$event.name, emitter:$event.ciReference)"
    }

}

package com.softwood

import com.softwood.alarmEvent.Alarm
import com.softwood.alarmEvent.Event
import com.softwood.incident.ManageIncidentFacadeService

class Application {
    public static void main (args) {

        def incidentProcessor = new ManageIncidentFacadeService()
        incidentProcessor.init()


        Event event = new Event (id:1, type:'critical', objId:"192.168.1.24", name:"Temperature Threshold Breached ")

        Alarm alarm = new Alarm (event)

        alarm.generateAlarm()

        println "resting "
        Thread.sleep(2000)
    }
}

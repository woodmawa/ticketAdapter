package com.softwood.Application

import com.softwood.alarmEvent.Alarm
import com.softwood.alarmEvent.Event
import com.softwood.incident.ManageIncidentFacadeService
import com.softwood.incident.adapters.MailAdapterPlugin

class Application {
    public static void main (args) {

        ProjectApp application = ProjectApp.run (Application, args)


        com.softwood.incident.adapters.MailAdapterPlugin mail = new MailAdapterPlugin()

        Map incDetails = [title:"help me", description: "its broke again", item:"damn printer",
                          urgency:"high", requester:"Will Woodman", customer:"softwood", site:"home",
                        priority : 'high', severity : 'high', urgency: 'PDQ']

        def body  = mail.generateFormattedMailBody(incDetails)
        mail.sendMail("new incident", "$body", "will.woodman@outlook.com")

        def incidentProcessor = new ManageIncidentFacadeService()
        incidentProcessor.init()


        Event event = new Event (id:1, type:'critical', ciReference:"192.168.1.24", name:"Temperature Threshold Breached ")

        Alarm alarm = new Alarm (event)

        alarm.generateAlarm()

        println "resting "
        Thread.sleep(2000)

    }
}

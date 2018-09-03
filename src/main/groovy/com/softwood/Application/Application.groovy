package com.softwood.Application

import com.softwood.alarmEvent.Alarm
import com.softwood.alarmEvent.Event
import com.softwood.incident.ManageIncidentFacadeService
import com.softwood.incident.adapters.MailAdapterPlugin

class Application {
    public static void main (args) {

        ConfigurableProjectApplication application = ProjectApp.run (Application, args)

        assert application
        assert application.vertx

        /*com.softwood.incident.adapters.MailAdapterPlugin mail = new MailAdapterPlugin()

        Map incDetails = [title:"help me", description: "its broke again", item:"damn printer",
                          urgency:"high", requester:"Will Woodman", customer:"softwood", site:"home",
                        priority : 'high', severity : 'high', urgency: 'PDQ']

        def body  = mail.generateFormattedMailBody(incDetails)
        mail.sendMail("new incident", "$body", "will.woodman@outlook.com")
    */

        //dagger IoC not working yet
        def incidentProcessor = new ManageIncidentFacadeService(application)


        Event event = new Event (id:1, type:'critical', ciReference:"192.168.1.24", name:"Temperature Threshold Breached ")

        Alarm alarm = new Alarm (application, event)

        alarm.generateAlarm()

        println "resting "
        Thread.sleep(2000)

    }
}

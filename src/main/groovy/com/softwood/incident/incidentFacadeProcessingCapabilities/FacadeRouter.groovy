package com.softwood.incident.incidentFacadeProcessingCapabilities

import com.softwood.alarmsAndEvents.Alarm
import com.softwood.application.Application
import com.softwood.cmdb.ConfigurationItem
import com.softwood.incident.IncidentTicket


//simple fixed flow at present
//get alarm -> which ci -> use adapter to post ticket
//todo make dyanmic chaining later
class FacadeRouter {

    //returns an Optional
    def  route (Alarm alarm, args=null) {

        CiContextResolver resolver = new CiContextResolver()
        resolver.resolve (alarm, args) {argList ->
            //implement resolution strategy

            def cmdbInstance

            def ciRef = alarm.ciReference
            //todo : frig to get round dependency injection proble with Dagger

            def app = Application.application
            def inventory = app.binding.inventory
            def matched = inventory.findAll { ci ->
                if (ci.hasCharacteristic('managementAddress') &&
                        ci.managementAddress == ciRef) {
                    ci
                }
                else
                    []
            }
            if (matched.size() == 1) {
                cmdbInstance = matched[0]
                new Optional (cmdbInstance)
            } else
                new Optional (matched)

        }


    }

    def route (ConfigurationItem ci) {
        def ticketAdapter

        //determine which adapter for get from factory

        ticketAdapter
    }
}

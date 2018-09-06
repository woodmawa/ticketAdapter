package com.softwood.incident.incidentFacadeProcessingCapabilities

import com.softwood.alarmsAndEvents.Alarm
import com.softwood.cmdb.ConfigurationItem
import com.softwood.incident.IncidentTicket


//simple fixed flow at present
//get alarm -> which ci -> use adapter to post ticket
//todo make dyanmic chaining later
class FacadeRouter {

    def  route (Alarm alarm) {

        CiContextResolver resolver = new CiContextResolver()
        resolver.resolve (alarm) {args ->
            def cmdbInstance
            //implement resolution strategy

            cmdbInstance
        }


    }

    def route (ConfigurationItem ci) {
        def ticketAdapter

        //determine which adapter for get from factory

        ticketAdapter
    }
}

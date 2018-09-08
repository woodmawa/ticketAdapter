package com.softwood.incident.incidentFacadeProcessingCapabilities

import com.softwood.alarmsAndEvents.Alarm
import com.softwood.application.Application
import com.softwood.application.ConfigurableProjectApplication
import com.softwood.cmdb.ConfigurationItem
import com.softwood.incident.IncidentTicket
import com.softwood.incident.adapters.AdapterFactory
import com.softwood.incident.adapters.AdapterFactoryType


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
                //def withManIp = ci.hasCharacteristic('managementIpAddress')
                //def match = (ci.managementIpAddress == ciRef)
                (ci.hasCharacteristic('managementIpAddress') &&
                        ci.managementIpAddress == ciRef) ? true : false
                //def result = (withManIp && match) ? true : false
            }
            if (matched.size() == 1) {
                cmdbInstance = matched[0]
                cmdbInstance
            } else
                matched

        }


    }

    def route (ConfigurationItem ci) {
        def ticketAdapter

        ConfigurableProjectApplication app = Application.application
        def binding = app.binding
        def confAdapterSystem = binding.getProperty("defaultTicketAdapter.system")



        //determine which adapter for get from factory

        ticketAdapter = AdapterFactory.newAdapter(confAdapterSystem, AdapterFactoryType.client)
        ticketAdapter
    }
}

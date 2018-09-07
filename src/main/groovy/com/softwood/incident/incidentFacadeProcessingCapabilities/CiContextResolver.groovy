package com.softwood.incident.incidentFacadeProcessingCapabilities

import com.softwood.alarmsAndEvents.Alarm
import com.softwood.application.Application
import com.softwood.cmdb.ConfigurationItem

import java.util.concurrent.ConcurrentHashMap

class CiContextResolver {

    def registry = new ConcurrentHashMap()

    CiContextResolver() {
        registry.put ("192.168.1.24", "myCpe")
    }

    /**
     *
     * @param alarm
     * @param args
     * @param resolutionStrategy
     * @return Optional with a value or empty
     */
    def resolve (Alarm alarm, args=null, Closure resolutionStrategy =null) {
        def cmdbInstance
        if (resolutionStrategy == null) {
            // if no strategy presented, fall back on the default strategy
            cmdbInstance = defaultResolve (alarm)
        } else {

            def resolver = resolutionStrategy?.clone()
            resolver.delegate = alarm

            //set alarm as the delegate for the closure and call it, passing any args if any
            cmdbInstance = resolver (args )
        }
        cmdbInstance

    }

    def defaultResolve (alarm) {
        new Optional<ConfigurationItem>(registry.get(alarm.ciReference))

    }
}

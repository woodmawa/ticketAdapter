package com.softwood.incident.incidentFacadeProcessingCapabilities

import com.softwood.alarmsAndEvents.Alarm
import com.softwood.cmdb.ConfigurationItem

import java.util.concurrent.ConcurrentHashMap

class CiContextResolver {

    def registry = new ConcurrentHashMap()

    CiContextResolver() {
        registry.put ("192.168.1.24", "myCpe")
    }

    def resolve (Alarm alarm, args=null, Closure resolutionStrategy =null) {
        def cmdbInstance
        if (resolutionStrategy == null) {

            cmdbInstance = new Optional<ConfigurationItem>()
        } else {

            def resolver = resolutionStrategy?.clone()
            resolver.delegate = alarm

            cmdbInstance = resolver (args )
        }
        cmdbInstance

    }

    def deterministicResolve (alarm) {
        registry.get(alarm.ciReference)

    }
}

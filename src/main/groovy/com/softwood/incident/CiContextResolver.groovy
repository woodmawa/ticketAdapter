package com.softwood.incident

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class CiContextResolver {

    def registry = new ConcurrentHashMap()

    CiContextResolver() {
        registry.put ("192.168.1.24", "myCpe")
    }
    def resolve (objRef, resolver=null) {
        if (!resolver) {
            new Optional<ConfigurationItem>()
        } else {
            resolver(objRef)
        }

    }

    def deterministicResolve (objId) {
        registry.get(objId)

    }
}

package com.softwood.cmdb

import java.util.concurrent.ConcurrentHashMap

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

package com.softwood.incident

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class CiContextResolver {

    def registry = new ConcurrentHashMap()

    CiContextResolver() {
        registry.put ("192.168.1.24", "myCpe")
    }
    def resolve (objId) {
        registry.get(objId)

    }
}

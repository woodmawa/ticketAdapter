package com.softwood.cmdb

import com.softwood.cmdb.views.Device
import spock.lang.Specification

class DeviceCiTest extends Specification {

    def "create device and confirm delegation for relationships" () {
        given: "a new device "
        Device router = new Device (name:"my router", ipAddress: "192.168.1.60")

        when : ""
        def methods = router.metaClass.methods.collect {it.name}
        println "list of methods $methods"

        then : "device responds to 'addRelationshipTo'"
        router.respondsTo('addRelationshipTo')
    }
}

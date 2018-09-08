package com.softwood.cmdb

import spock.lang.Specification
import com.softwood.cmdb.views.Device

class DelegateTransformWithMethodMissingTest extends Specification {

    def "access attribute that does on exist on CMDB viewObject" () {
        given : "create a new ci "
        Device dev = new Device (ipAddress: "192.168.1.1", hostname: "uk-hse-R1",  name: "myCpe" )
        //dev.hostname = "uk-hse-R1"

        when : "read delegated and actual atributes back "
        def ip = dev.ipAddress
        def name = dev.name
        def ciAtts = dev.ci.attributes  //get map of attVals, where propName is key
        def specChar  = ciAtts.hostname

        then : "confirm expected results "
        ip == "192.168.1.1"
        name == "myCpe"
        dev.ci.name == "myCpe"
        ciAtts.size() == 1
        specChar.getName() == "hostname"
        dev.hostname == "uk-hse-R1"
    }
}

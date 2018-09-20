package com.softwood.scripts


import com.softwood.cmdb.Contract
import com.softwood.cmdb.Customer
import com.softwood.cmdb.RoleType
import com.softwood.cmdb.Site
import com.softwood.cmdb.views.Device
import com.softwood.utils.JsonUtils
import io.vertx.core.json.JsonObject

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue


JsonUtils.Options options = new JsonUtils.Options()
options.registerConverter(LocalDateTime) {it.toString()}
//options.excludeFieldByNames("class")
//options.excludeClass (false)
options.excludeFieldByNames("ci")
//options.summaryClassFormEnabled(true)

def generator = options.build()

class A {
    String name = "a"
    B binst = new B()
    ConcurrentLinkedQueue list = new ConcurrentLinkedQueue()
    String toString () {
        "A(name: $name)"
    }
}

class B {
    String name = "b"

    String toString () {
        "B(name: $name)"
    }
}

def a = new A()
a.list<< new B()
a.list << new B()

println "basic array : " + generator.toJson (a).encodePrettily()
System.exit(0)

//println "basic array : " + generator.toJson ([1,2,3])

//println "basic map : " + generator.toJson ([fred:1,joe:2])

Customer cust = new Customer (name:"HSBC", role : RoleType.CUSTOMER )
//println "basic cust, no reference fields  : " + generator.toJson(cust).encodePrettily()

println "----"


Site site = new Site (name:"canary wharf HQ")
def device = new Device (name:"fred")
cust.addSite(site)
//println "cust, with 1 site  : " + generator.toJson(cust).encodePrettily()
println "----"
println "site, encodes as  : " + generator.toJson(site).encodePrettily()
println "----"

System.exit(0)
Contract con = new Contract (reference: "digitalBank")
cust.addContract(con)
println "cust, with 1 site, 1 contract  : " + generator.toJson(cust).encodePrettily()

Device router = new Device(name:"Wan gateway ASR", category:"Router", hostname:"UK-LON-ROUTER-CWHQ", ipAddress: "192.168.1.60", managementIpAddress: "192.168.1.24", alias:"access router")
router.customer = cust
router.site = site


println "cust one site, one contract : " + generator.toJson(cust).encodePrettily()

println "----"
println "site : " + generator.toJson(site).encodePrettily()

println "----"



JsonObject json
//json= generator.toJson (site )
//println "site as json : " + json.encodePrettily()

json = generator.toJson (router)

println "router as json" + json.encodePrettily()

/* def attributes =  []
attributes << new CiSpecificationCharacteristic ("airbrush", "blue")
attributes << new CiSpecificationCharacteristic ("car", "bmw")

println "attributes array : " + attributes
println "attributes array as json : " + generator.toJson(attributes )

class Test {
    String nullStr = null
    LocalDateTime ldt = LocalDateTime.now()
    List listInt = [1,2,3]
}


def map = [:]
map << ["hello":"there"] << ["and now": "try this"] << [test: new Test() ]
println "map as json : " + generator.toJson(map )
*/


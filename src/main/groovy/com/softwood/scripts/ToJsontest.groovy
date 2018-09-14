package com.softwood.scripts

import com.softwood.cmdb.CiSpecificationCharacteristic
import com.softwood.cmdb.Contract
import com.softwood.cmdb.Customer
import com.softwood.cmdb.RoleType
import com.softwood.cmdb.Site
import com.softwood.cmdb.views.Device
import com.softwood.utils.JsonUtils
import groovy.json.JsonOutput
import io.vertx.core.json.JsonObject

import java.time.LocalDateTime


JsonUtils.Options options = new JsonUtils.Options()
options.registerConverter(LocalDateTime) {it.toString()}
options.excludeByNames("class")

def generator = options.build()


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


Customer cust = new Customer (name:"HSBC", role : RoleType.CUSTOMER )


Contract con = new Contract (reference: "digitalBank")
Site site = new Site (name:"canary wharf HQ")
def device = new Device (name:"fred")
cust.addSite(site)
cust.addContract(con)


Device router = new Device(name:"Wan gateway ASR", category:"Router", hostname:"UK-LON-ROUTER-CWHQ", ipAddress: "192.168.1.60", managementIpAddress: "192.168.1.24", alias:"access router")
router.customer = cust
router.site = site


println "cust : " + generator.toJson(cust).encodePrettily()

println "----"
println "site : " + generator.toJson(site).encodePrettily()

println "----"



JsonObject json
//json= generator.toJson (site )
//println "site as json : " + json.encodePrettily()

json = generator.toJson (router)

println "router as json" + json.encodePrettily()

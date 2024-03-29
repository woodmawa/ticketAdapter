package com.softwood.scripts


import com.softwood.cmdb.Contract
import com.softwood.cmdb.Customer
import com.softwood.cmdb.RoleType
import com.softwood.cmdb.Site
import com.softwood.cmdb.views.Device
import com.softwood.utils.JsonUtils
import io.vertx.core.json.JsonObject

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/*JsonGenerator defgen = new JsonGenerator.Options()
.excludeNulls()
.excludeFieldsByType(Class)
.build()

Site newsite = new Site()
newsite.name = "mysite"

BillOfMaterials bom = new BillOfMaterials()
bom.addSite(newsite)
bom.addToBasket(newsite, new ProductOffering(name:"my offer "))

println "bom looks like "
println defgen.toJson(bom)
System.exit(0)*/

JsonUtils.Options options = new JsonUtils.Options()
options.registerConverter(LocalDateTime) { it.toString() }
//options.excludeFieldByNames("class")
//options.excludeClass (false)
options.excludeFieldByNames("ci")
options.jsonApiCompoundDocument(true)
options.jsonApiOptionalLinks(true)
options.excludeNulls(true)
//options.summaryClassFormEnabled(true)

def generator = options.build()


println "basic array : " + generator.toJson([1, 2, 3]).encode()
println "basic map : " + generator.toJson(["a": 1, "b": 2, "c": 3]).encode()
println "------"

class A {
    String name = "a"
    int id
    B binst = new B(id: 200)
    List basicNumericList = [1, 2, 3]
    List mixedList = [1, new D(id: 1000)]
    ConcurrentLinkedQueue listOfC = new ConcurrentLinkedQueue()

    String toString() {
        "A(name: $name, id:$id)"
    }

    ConcurrentHashMap<String, Object> objectMap = new ConcurrentHashMap()
}

println "basic map : " + generator.toJson([fred: 1, joe: 2])

class B {
    String name = "b"
    int id

    String toString() {
        "B(name: $name, id:$id)"
    }
}

class C {
    String name = "c"
    int id

    String toString() {
        "C(name: $name, id:$id)"
    }
}

class D {
    String name = "d"
    int id

    String toString() {
        "D(name: $name, id:$id )"
    }
}

def a = new A(id: 100)
a.listOfC << new C(id: 1)
a.listOfC << new C(id: 2)

a.objectMap.put("firstD", new D(id: 300))

println "enc as jsonApi: A with B and array of C, and map of D's : " + generator.toJsonApi(a).encodePrettily()

println "-----"

println "enc with wills json : A with B and array of C, and map of D's : " + generator.toJson(a).encodePrettily()

/*def js = JsonOutput.toJson (a)
        println JsonOutput.prettyPrint(js)*/



System.exit(0)

Customer cust = new Customer(name: "HSBC", role: RoleType.CUSTOMER)
//println "basic cust, no reference fields  : " + generator.toJsonApi(cust).encodePrettily()


println "----"


Site site = new Site(name: "canary wharf HQ")
def device = new Device(name: "fred")
cust.addSite(site)
//println "cust, with 1 site  : " + generator.toJson(cust).encodePrettily()
println "----"
println "site, as jsonapi encodes as  : " + generator.toJsonApi(site).encodePrettily()
println "----"
println "site, as json encodes as  : " + generator.toJson(site).encodePrettily()
//println "----"
//System.exit (0)


Contract con = new Contract(reference: "digitalBank")
cust.addContract(con)
println "cust, with 1 site, 1 contract  : " + generator.toJson(cust).encodePrettily()

//System.exit (0)

Device router = new Device(name: "Wan gateway ASR", category: "Router", hostname: "UK-LON-ROUTER-CWHQ", ipAddress: "192.168.1.60", managementIpAddress: "192.168.1.24", alias: "access router")
router.customer = cust
router.site = site

/*
println "cust one site, one contract : " + generator.toJson(cust).encodePrettily()

println "----"
println "site : " + generator.toJsonApi(site).encodePrettily()

println "----"
*/


JsonObject json
//json= generator.toJson (site )
//println "site as json : " + json.encodePrettily()

json = generator.toJson(router)

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


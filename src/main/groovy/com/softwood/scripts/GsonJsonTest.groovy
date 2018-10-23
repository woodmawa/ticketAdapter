package com.softwood.scripts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.softwood.cmdb.Contract
import com.softwood.cmdb.Customer
import com.softwood.cmdb.RoleType
import com.softwood.cmdb.Site
import com.softwood.request.Request

import java.util.concurrent.ConcurrentLinkedQueue

Customer hsbc = new Customer (name:"HSBC", role : RoleType.CUSTOMER )

Site site = new Site (name:"canary wharf HQ")
site.postalCode = "E14 5AH"

hsbc.addSite (site)

site = new Site (name:"HSBC Docklands Datacentre")
site.postalCode = "E14 3AB"
hsbc.addSite(site)

Request req = new Request(id: 1, status: "open", requestIdentifier: "first request", title: "brand new ticket")
req.customer = hsbc //done in addRequest
//hsbc.addRequest (req)

Gson gson = new GsonBuilder().setPrettyPrinting().create() //new Gson()


//JsonElement elem  = gson.toJson(req)

//println gson.toJson(req)

class AParent {
   String name
    Queue listOfB = new ConcurrentLinkedQueue<Bchild>()

    String toString () {
        "A (name:$name, listOfB:$listOfB)"
    }
}

class Bchild {
    String name
    //AParent parent

    String toString() {
        //"B (name:$name, parent:$parent)"
        "B (name:$name )"
    }
}

def p = new AParent (name:"parent")
new Bchild (name:"child")
p.listOfB << [new Bchild (name:"child"), "myChild"]

//println gson.toJson(p)

AParent newP =  gson.fromJson(gson.toJson(p), AParent)
//println newP

println gson.toJson (2)
println gson.toJson ([a:1])
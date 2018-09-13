package com.softwood.scripts

import com.softwood.cmdb.Contract
import com.softwood.cmdb.Customer
import com.softwood.cmdb.RoleType
import com.softwood.cmdb.Site
import com.softwood.cmdb.views.Device

Customer cust = new Customer (name:"HSBC", role : RoleType.CUSTOMER )
Contract con = new Contract (reference: "digitalBank")

Site site = new Site (name:"canary wharf HQ")
def device = new Device (name:"fred")
cust.addSite(site)
cust.addContract(con)

Device router = new Device(name:"Wan gateway ASR", category:"Router", hostname:"UK-LON-ROUTER-CWHQ", ipAddress: "192.168.1.60", managementIpAddress: "192.168.1.24", alias:"access router")
router.customer = cust
router.site = site


println router.toJson()
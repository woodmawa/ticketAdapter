package com.softwood.cmdb

import com.softwood.utils.UuidUtil
import groovy.transform.Canonical
import groovy.transform.MapConstructor

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

//ensure role type is correctly set
//@MapConstructor (post = {it.role = RoleType.CUSTOMER})

class Customer extends OrgRoleInstance {
    UUID id = UuidUtil.timeBasedUuid
    String name
    RoleType role
    LocalDateTime createdDateTime = LocalDateTime.now()
    ConcurrentLinkedQueue<Contract> sites = new ConcurrentLinkedQueue()
    ConcurrentLinkedQueue<Contract> contracts = new ConcurrentLinkedQueue()

    void addContract (Contract contract) {
        if (!contracts.contains(contract)){
            contract.customer = this
            contracts << contract
        }
    }

    void removeContract (Contract contract) {
        contract.customer = null
        contracts.remove(contract)
    }

    void addSite (Site site) {
        if (!sites.contains(site)){
            sites << site
            site.customer = this
        }
    }

    void removeSite (Site site) {
        site.customer = null
        sites.remove(site)
    }

    String toString() {
        "Customer ($name)"
    }

}

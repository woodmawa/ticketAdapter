package com.softwood.cmdb

import com.softwood.utils.UuidUtil
import groovy.transform.MapConstructor

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

//ensure role type is correctly set
@MapConstructor (post = {it.role = RoleType.CUSTOMER})
class Customer extends OrgRoleInstance {
    UUID id = UuidUtil.timeBasedUuid
    LocalDateTime createdDateTime = LocalDateTime.now()
    ConcurrentLinkedQueue<Contract> contracts = new ConcurrentLinkedQueue()

    void addContract (Contract contract) {
        if (!contracts.contains(contract)){
            contracts << contract
        }
    }

    void removeContract (Contract contract) {
        contracts.remove(contract)
    }
}

package com.softwood.cmdb

import com.softwood.utils.UuidUtil

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

class Contract {
    UUID id = UuidUtil.getTimeBasedUuid ()
    String reference
    Customer customer
    LocalDateTime createdDateTime = LocalDateTime.now()
    ConcurrentLinkedQueue<ConfigurationItem> inventory = new ConcurrentLinkedQueue()
    ConcurrentLinkedQueue<ServiceLevelAgreement> serviceLevels = new ConcurrentLinkedQueue<>()
    ConcurrentLinkedQueue<MaintainerAgreement> mags = new ConcurrentLinkedQueue<>()


    void addInventory (ConfigurationItem ci) {
        if (!inventory.contains(ci)){
            inventory << ci
        }
    }

    void removeInventory (ConfigurationItem ci) {
        inventory.remove(ci)
    }

    void addSLA (ServiceLevelAgreement sla) {
        if (!serviceLevels.contains(sla)){
            serviceLevels << ci
        }
    }

    void removeSLA (ServiceLevelAgreement sla) {
        inventory.remove(sla)
    }

    void addMAG (MaintainerAgreement mag) {
        if (!serviceLevels.contains(mag)){
            serviceLevels << mag
        }
    }

    void removeMAG (MaintainerAgreement mag) {
        inventory.remove(mag)
    }
}

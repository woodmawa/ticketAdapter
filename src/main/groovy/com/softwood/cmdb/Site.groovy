package com.softwood.cmdb

import com.softwood.utils.UuidUtil

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

class Site {
    UUID id = UuidUtil.getTimeBasedUuid ()
    String name
    String siteContact
    Customer customer
    LocalDateTime createdDateTime = LocalDateTime.now()
    ConcurrentLinkedQueue<ConfigurationItem> inventory = new ConcurrentLinkedQueue()

    void addInventory (ConfigurationItem ci) {
        if (!inventory.contains(ci)){
            inventory << ci
        }
    }

    void removeInventory (ConfigurationItem ci) {
        inventory.remove(ci)
    }
}

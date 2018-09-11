package com.softwood.cmdb

import com.softwood.utils.UuidUtil
import groovy.transform.Canonical

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue


class Site {
    UUID id = UuidUtil.getTimeBasedUuid ()
    String name
    String siteContact
    String address  //simple for now
    String postalCode
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

    String toString() {
        "Site ($name)"
    }
}

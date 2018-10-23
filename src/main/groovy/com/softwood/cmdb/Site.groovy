/*
 * Copyright 2018 author : Will woodman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.softwood.cmdb

import com.softwood.utils.UuidUtil

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * base alignment with tmf GeographicSite
 */
class Site {
    UUID id = UuidUtil.getTimeBasedUuid ()
    String name
    String description
    String code
    String status
    String siteContact
    String address  //simple for now
    String postalCode
    Customer customer
    // GeographicLocation geographicLocation
    //Party relatedParties = new ConcurrentLinkedQueue()
    //Relationship siteRelationships = new ConcurrentLinkedQueue()
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
        "Site (name:$name, postalCode:$postalCode) [id:${id.toString()}] )"
    }
}

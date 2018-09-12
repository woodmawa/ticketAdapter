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
import groovy.transform.Canonical

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

    String toString() {
        "Contract ($reference)"
    }
}

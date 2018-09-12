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

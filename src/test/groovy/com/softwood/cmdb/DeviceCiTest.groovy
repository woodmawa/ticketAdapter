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

import com.softwood.cmdb.views.Device
import spock.lang.Specification

class DeviceCiTest extends Specification {

    def "create device and confirm delegation for relationships"() {
        given: "a new device "
        Device router = new Device(name: "my router", ipAddress: "192.168.1.60")

        when: ""
        def methods = router.metaClass.methods.collect { it.name }
        def relations = router.metaClass.methods.findAll { it.name.contains "add" }
        println "list of methods $methods"
        println "list of relationship methods $relations"

        then: "device responds to 'addRelationshipTo'"
        router.respondsTo('addRelationshipTo')
    }
}

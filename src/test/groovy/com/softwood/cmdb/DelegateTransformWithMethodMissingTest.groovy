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

import spock.lang.Specification
import com.softwood.cmdb.views.Device

class DelegateTransformWithMethodMissingTest extends Specification {

    def "access attribute that does on exist on CMDB viewObject" () {
        given : "create a new ci "
        Device dev = new Device (ipAddress: "192.168.1.1", hostname: "uk-hse-R1",  name: "myCpe" )
        //dev.hostname = "uk-hse-R1"

        when : "read delegated and actual atributes back "
        def ip = dev.ipAddress
        def name = dev.name
        def ciAtts = dev.ci.ciAttributes  //get map of attVals, where propName is key
        def specChar  = ciAtts.hostname

        then : "confirm expected results "
        ip == "192.168.1.1"
        name == "myCpe"
        dev.ci.name == "myCpe"
        ciAtts.size() == 1
        specChar.getName() == "hostname"
        dev.hostname == "uk-hse-R1"
    }
}

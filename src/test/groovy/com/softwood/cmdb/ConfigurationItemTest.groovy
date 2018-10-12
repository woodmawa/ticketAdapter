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
import spock.lang.Specification

import java.time.LocalDateTime

class ConfigurationItemTest extends Specification {

    def "new ci should have a unique id created "() {
        given: "create a new ci "
        ConfigurationItem ci = new ConfigurationItem(name:"myCPE")
        println ci.id
       LocalDateTime time = UuidUtil.getLocalDateTimeFromUuid(ci.id)
        println time

        expect:"uuid should exist "
        ci.id
        ci.name == "myCPE"
        ci.ciAttributes.size() == 0
    }

    def "test add and rerieve attributes for a ci "() {
        given: "create a new ci "
        ConfigurationItem ci = new ConfigurationItem(name:"myCPE")
        ci.addCharacteristic ("hostname", "localhost")

        expect:"uuid should exist "
        ci.id
        ci.name == "myCPE"
        ci.getCharacteristic("hostname") == "localhost"
    }

    def "test missing property access resolved dyanamically "() {
        given: "a new ci "
        ConfigurationItem ci = new ConfigurationItem(name:"myCPE")
        when: "we save a property thats not defined in the ci"
        ci.hostname = ["localhost", "tent"] //trigger prop missing
        Site site = new Site (name:"hq")  //use existing prop
        ci.site = site

        then:  " we can access that property as though it had always been there "
        ci.hostname == ["localhost", "tent"]
        ci.site.name == "hq"


    }
}

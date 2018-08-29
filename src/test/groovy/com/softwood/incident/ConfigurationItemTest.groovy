package com.softwood.incident

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
        ci.attributes.size() == 0
    }
}

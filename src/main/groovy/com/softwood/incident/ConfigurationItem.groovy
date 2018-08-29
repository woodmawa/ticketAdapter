package com.softwood.incident

import com.softwood.utils.UuidUtil

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class ConfigurationItem {
    UUID id = UuidUtil.getTimeBasedUuid ()  //generate a time based uuid
    String name
    String alias
    String status
    ConcurrentHashMap<String, ciSpecificationCharacteristic> attributes = new ConcurrentHashMap()
    LocalDateTime createdDate =  LocalDateTime.now()

    void setProperty (name, value) {
        def attVal = new ciSpecificationCharacteristic(propertyName:name, value:value)
        attributes.put (name, attVal)
    }

    def getProperty (name) {
        def attVal = attributes.get(name)
        attVal?.propertyName
    }
}

class ciSpecificationCharacteristic {
    String propertyName
    String value
    String[] arrayValues = []

    boolean isMultivalued () {arrayValues}

    String getName() {
        propertyName
    }

    String toString () {
        "ciAttVal (name:$propertyName, value:${value ?: arrayValues} )"
    }
}

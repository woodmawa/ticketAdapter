package com.softwood.cmdb

import com.softwood.utils.UuidUtil

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class ConfigurationItem {
    UUID id = UuidUtil.getTimeBasedUuid ()  //generate a time based uuid
    String name
    String alias
    String status
    Customer customer
    Optional<Site> owningSite
    Optional<Site> remoteSite     //optional

    Optional<ServiceLevelAgreement> sla = new Optional()

    Optional<Site> site = new Optional()
    Optional<Contract> contract = new Optional()

    ConcurrentHashMap<String, ciSpecificationCharacteristic> attributes = new ConcurrentHashMap()
    LocalDateTime createdDate =  LocalDateTime.now()

    void addCharacteristic (name, value) {
        def attVal = new ciSpecificationCharacteristic(propertyName:name, value:value)
        attributes.put (name, attVal)
    }

    def getCharacteristic (name) {
        def attVal = attributes.get(name)
        !attVal?.isMultivalued() ? attVal?.value : attVal?.arrayValues
    }

    def setCharacteristic (name, value) {
        def attVal = attributes.get(name)
        !attVal?.isMultivalued() ? attVal?.value = value : attVal?.arrayValues << value
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

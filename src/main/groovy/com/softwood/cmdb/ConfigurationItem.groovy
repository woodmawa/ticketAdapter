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

    Optional<ServiceLevelAgreement> sla = new Optional()

    Optional<Site> site = new Optional()
    Optional<Contract> contract = new Optional()

    ConcurrentHashMap<String, ciSpecificationCharacteristic> attributes = new ConcurrentHashMap()
    LocalDateTime createdDate =  LocalDateTime.now()

    void addCharacteristic (String name, value) {
        def attVal = new ciSpecificationCharacteristic(name, value)
        attributes.put (name, attVal)
    }

    def getCharacteristic (String name) {
        def attVal = attributes.get(name)
        !attVal?.isMultivalued() ? attVal?.value : attVal?.arrayValues
    }

    def setCharacteristic (String name, value) {
        def attVal = attributes.get(name)
        !attVal?.isMultivalued() ? attVal?.value = value : attVal?.arrayValues << value
    }

    boolean hasCharacteristic (String name) {
        attributes.contains(name)
    }


    void setSite (site) {
        Optional optSite = new Optional<Site> (site)
        this.site = optSite
    }

    Site getSite () {
        site.get()
    }


    void setContract (contract) {
        Optional optContract = new Optional<Contract> (contract)
        this.contract = optContract
    }

    Site getContract () {
        contract.get()
    }

    void setSla (sla) {
        Optional optSla = new Optional<ServiceLevelAgreement> (sla)
        this.sla = optSla
    }

    Site getSla () {
        sla.get()
    }

    /**
     * if property being set is not on add class add as characteristic spec value
     * @param name
     * @param value
     */
    void setProperty (String name, value) {
        //if not a fixed class property
        //def props = this.metaClass.properties.collect {it.name}
        if (!metaClass.hasProperty(this, name)) {
            def attVal = new ciSpecificationCharacteristic(name, value)
            attributes.put (name, attVal)

        }
        else
            metaClass.setProperty(this, name, value)

    }

    def getProperty (String name) {
        //def props = this.metaClass.properties.collect {it.name}
        if (!metaClass.hasProperty(this, name)) {
            def attVal = attributes."$name"
            attVal.getValue()
        }
        else
            this.metaClass.getProperty(this, name)
    }
}

class ciSpecificationCharacteristic {
    String propertyName
    def value
    Collection arrayValues = []

    ciSpecificationCharacteristic () {}

    ciSpecificationCharacteristic (name, value) {
        propertyName = name
        if (!(value instanceof Collection) )
            this.value = value
        else
            arrayValues =  value
    }

    boolean isMultivalued () {arrayValues}

    String getName() {
        propertyName
    }

    String setName (name) {
        propertyName = name
    }

    def getValue () {
        if (!isMultivalued())
            value
        else
            arrayValues
    }

    void setValue (value) {
        if (!(value instanceof Collection) )
            this.value = value
        else
            arrayValues = value
    }

    String toString () {
        "ciAttVal (name:$propertyName, value:${value ?: arrayValues} )"
    }
}

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

import com.softwood.request.ProductOffering
import com.softwood.utils.UuidUtil

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class ConfigurationItem {
    UUID id = UuidUtil.getTimeBasedUuid ()  //generate a time based uuid
    String name
    String alias
    String status
    Customer customer
    String type
    String category
    ProductOffering offering
    Map ciHierarchy = [:]  //level1, to level5 - level names as key, value to hold visible display value

    //related CI where each entry is [ci, relationship] entries
    ConcurrentHashMap<Object, Relationship> relatedToCi = new ConcurrentHashMap()

    Optional<ServiceLevelAgreement> sla = Optional.empty()

    //Optional<Site> site = Optional.ofNullable(null) - having problems with ifPresent
    Site site
    Optional<Contract> contract = Optional.empty()

    ConcurrentHashMap<String, CiSpecificationCharacteristic> ciAttributes = new ConcurrentHashMap()
    LocalDateTime createdDate =  LocalDateTime.now()

    void addRelationshipTo (toCi, String relationshipName) {
        Relationship relationship = new Relationship (toCi: toCi, fromCi: this,  name:relationshipName)
        def entry = [toCi: relationship]
        if (!relatedToCi.contains (entry ))
            relatedToCi << entry

    }

    void removeRelationshipTo (toCi) {
        if (relatedToCi.contains (toCi))
            relatedToCi.remove(toCi)

    }

    Collection<Relationship> getRelationships (ci=null) {
        if (!ci)
            relatedTo.collect {it.value}
        else if (relatedTo.contains(ci))
            [relatedTo.ci]
        else []
    }

    void addCharacteristic (String name, value) {
        def attVal = new CiSpecificationCharacteristic(name, value)
        ciAttributes.put (name, attVal)
    }

    def getCharacteristic (String name) {
        def attVal = ciAttributes.get(name)
        !attVal?.isMultivalued() ? attVal?.value : attVal?.arrayValues
    }

    def setCharacteristic (String name, value) {
        def attVal = ciAttributes.get(name)
        !attVal?.isMultivalued() ? attVal?.value = value : attVal?.arrayValues << value
    }

    boolean hasCharacteristic (String name) {
        ciAttributes.containsKey(name)
    }


    void setContract (contract) {
        Optional optContract = new Optional<Contract> (contract)
        this.contract = optContract
    }

    def getContract () {
        if (contract.ifPresent())
            contract.get()
        else
            Optional.empty()
    }

    void setSla (sla) {
        Optional optSla = new Optional<ServiceLevelAgreement> (sla)
        this.sla = optSla
    }

    def getSla () {
        if (sla.ifPresent())
            sla.get()
        else
            Optional.empty()
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
            def attVal = new CiSpecificationCharacteristic(name, value)
            ciAttributes.put (name, attVal)

        }
        else
            metaClass.setProperty(this, name, value)

    }

    def getProperty (String name) {
        //def props = this.metaClass.properties.collect {it.name}
        if (!metaClass.hasProperty(this, name)) {
            def attVal = ciAttributes."$name"
            attVal?.getValue()
        }
        else
            this.metaClass.getProperty(this, name)
    }

    String toString() {
        "Ci (id:$id, type: $type, name:$name)"
    }
}

class CiSpecificationCharacteristic {
    String propertyName
    def value
    Collection arrayValues = []

    CiSpecificationCharacteristic() {}

    CiSpecificationCharacteristic(name, value) {
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

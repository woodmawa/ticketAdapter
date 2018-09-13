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
import groovy.json.JsonGenerator
import io.vertx.core.json.JsonObject

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class ConfigurationItem {
    UUID id = UuidUtil.getTimeBasedUuid ()  //generate a time based uuid
    String name
    String alias
    String status
    Customer customer
    String type
    String category
    Map hierarchy = [:]  //level1, to level5 - level names as key, value to hold visible display value

    //related CI where each entry is [ci, relationship] entries
    ConcurrentHashMap<Object, Relationship> relatedTo = new ConcurrentHashMap()

    Optional<ServiceLevelAgreement> sla = Optional.empty()

    //Optional<Site> site = Optional.ofNullable(null) - having problems with ifPresent
    Site site
    Optional<Contract> contract = Optional.empty()

    ConcurrentHashMap<String, ciSpecificationCharacteristic> attributes = new ConcurrentHashMap()
    LocalDateTime createdDate =  LocalDateTime.now()

    void addRelationshipTo (toCi, String relationshipName) {
        Relationship relationship = new Relationship (toCi: toCi, fromCi: this,  name:relationshipName)
        def entry = [toCi: relationship]
        if (!relatedTo.contains (entry ))
            relatedTo << entry

    }

    void removeRelationshipTo (toCi) {
        if (relatedTo.contains (toCi))
            relatedTo.remove(toCi)

    }

    Collection<Relationship> getRelationships (ci=null) {
        if (!ci)
            relatedTo.collect {it.value}
        else if (relatedTo.contains(ci))
            [relatedTo.ci]
        else []
    }

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
        attributes.containsKey(name)
    }


    /*
    void setSite (site) {
        Optional optSite = new Optional<Site> (site)
        this.site = optSite
    }

    def getSite () {
        if (site.ifPresent())
            site.get()
        else
            Optional.empty()
    }*/


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
            attVal?.getValue()
        }
        else
            this.metaClass.getProperty(this, name)
    }

    String toString() {
        "Ci (id:$id, type: $type, name:$name)"
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

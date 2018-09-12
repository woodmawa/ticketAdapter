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
package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

import java.util.concurrent.ConcurrentLinkedQueue

class PackageService {

    @Delegate ConfigurationItem pci

    String piName
    ConcurrentLinkedQueue ConfigurationItems = new ConcurrentLinkedQueue()


    PackageService() {
        ConfigurationItem pci = new ConfigurationItem()
    }

    PackageService(ConfigurationItem ci) {
        assert ci

        if (!ConfigurationItems.contains(ci ))
            ConfigurationItems << ci
    }

    void addConfigurationItem (ci) {
        assert ci

        if (!ConfigurationItems.contains(ci ))
            ConfigurationItems << ci
    }

    void removeConfigurationItem (ci) {
        assert ci

        if (!ConfigurationItems.contains(ci ))
            ConfigurationItems.remove(ci)
    }

    /**
     *     catch property missing on map constructor call, and delegate to the embedded ci
     */
    def propertyMissing (String name) {
        getProperty(name)
    }

    def propertyMissing (String name, value) {
        setProperty(name, value)
    }

    /**
     * intercept regular property accesses and delegate to embedded ci
     */
    void setProperty (String name, value) {
        if (!metaClass.hasProperty(this, name)) {
            pci?."$name" = value
        }
        else
            metaClass.setProperty(this, name, value)
    }

    def getProperty (String name) {
        if (!metaClass.hasProperty(this, name)) {
            pci?."$name"
        }
        else
            this.metaClass.getProperty(this, name)
    }
}
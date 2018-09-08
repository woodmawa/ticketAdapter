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
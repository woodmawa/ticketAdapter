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

    void setProperty (String name, value) {
        if (!metaClass.hasProperty(this, name)) {
            ci."$name" = value
        }
        else
            metaClass.setProperty(this, name, value)
    }

    def getProperty (String name) {
        if (!metaClass.hasProperty(this, name)) {
            ci."$name"
        }
        else
            this.metaClass.getProperty(this, name)
    }
}
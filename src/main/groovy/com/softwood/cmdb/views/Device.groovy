package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

class Device {
    @Delegate ConfigurationItem ci

    String ipAddress

    Device() {
        ci = new ConfigurationItem()
        println "default constructor called - create delegate ci "
        //this.metaClass.propertyMissing
    }

    Device(ConfigurationItem ci) {
        assert ci
        this.ci = ci
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
        println "invoked set property for $name with value $value "
        if (!metaClass.hasProperty(this, name)) {
            ci?."$name" = value
        }
        else
            metaClass.setProperty(this, name, value)
    }

    def getProperty (String name) {
        if (!metaClass.hasProperty(this, name)) {
            ci?."$name"
        }
        else
            this.metaClass.getProperty(this, name)
    }

    String toString(){
        "Device (name:$name, host:$ci.hostname, managementIpAddress:$ci.managementIpAddress)"
    }
}
package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

class Device {
    @Delegate ConfigurationItem ci

    String ipAddress

    Device() {
        ci = new ConfigurationItem()
    }

    Device(ConfigurationItem ci) {
        assert ci
        this.ci = ci
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

    String toString(){
        "Device (name:$name, host:$ci.hostname, managementAddress:$ci.managementAddress)"
    }
}
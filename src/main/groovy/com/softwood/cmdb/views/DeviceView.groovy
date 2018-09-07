package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem
import com.softwood.cmdb.Site

class DeviceView {
    @Delegate ConfigurationItem device


    DeviceView () {
        device = new ConfigurationItem()
    }

    DeviceView (ConfigurationItem ci) {
        assert ci
        device = ci
    }

    def getMissingProperty (String name) {
        device.name
    }

    def setMissingProperty (String name, value) {
        device.name = value
    }

    String toString(){
        "Device (name:$name, host:$device.hostname, managementAddress:$device.managementAddress)"
    }
}
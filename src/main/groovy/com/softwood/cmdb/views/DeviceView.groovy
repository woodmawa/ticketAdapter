package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

class DeviceView {
    @Delegate ConfigurationItem

    String hostname
    String managementAddress
    String alias

    Device () {
        ConfigurationItem = new ConfigurationItem()
    }

    Device (ConfigurationItem ci) {
        assert ci
        ConfigurationItem = ci
    }


}
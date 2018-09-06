package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

class DeviceView {
    @Delegate ConfigurationItem device

    String hostname
    String managementAddress
    String alias

    DeviceView () {
        device = new ConfigurationItem()
    }

    DeviceView (ConfigurationItem ci) {
        assert ci
        device = ci
    }


}
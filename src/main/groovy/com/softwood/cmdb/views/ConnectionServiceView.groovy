package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

class ConnectionServiceView {
    @Delegate ConfigurationItem



    ConnectionServiceView () {
        ConfigurationItem = new ConfigurationItem()
    }

    ConnectionServiceView (ConfigurationItem ci) {
        assert ci
        ConfigurationItem = ci
    }


}
package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

class ConnectionServiceView {
    @Delegate ConfigurationItem connection



    ConnectionServiceView () {
        connection = new ConfigurationItem()
    }

    ConnectionServiceView (ConfigurationItem ci) {
        assert ci
        connection = ci
    }


}
package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

class BearerView {
    @Delegate ConfigurationItem


    BearerView() {
        ConfigurationItem = new ConfigurationItem()
    }

    BearerView(ConfigurationItem ci) {
        assert ci
        ConfigurationItem = ci
    }


}
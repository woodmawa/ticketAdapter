package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

class BearerView {
    @Delegate ConfigurationItem bearer


    BearerView() {
        bearer = new ConfigurationItem()
    }

    BearerView(ConfigurationItem ci) {
        assert ci
        bearer = ci
    }


}
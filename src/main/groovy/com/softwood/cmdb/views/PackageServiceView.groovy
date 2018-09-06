package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

import java.util.concurrent.ConcurrentLinkedQueue

class PackageServiceView {

    String piName
    ConcurrentLinkedQueue ConfigurationItems = new ConcurrentLinkedQueue()


    PackageServiceView() {
        //ConfigurationItem ci = new ConfigurationItem()
    }

    PackageServiceView(ConfigurationItem ci) {
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

}
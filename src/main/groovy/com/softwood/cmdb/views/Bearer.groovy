package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

class Bearer {
    @Delegate ConfigurationItem ci


    Bearer() {
        ci = new ConfigurationItem()
    }

    Bearer(ConfigurationItem ci) {
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

}
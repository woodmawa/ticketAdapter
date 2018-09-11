package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

class ConnectionService {
    @Delegate ConfigurationItem ci


    ConnectionService() {
        ci = new ConfigurationItem()
    }

    ConnectionService(ConfigurationItem ci) {
        assert ci
        this.ci = ci
    }

    //alias method for CI name
    String getServiceIdentifier () {
        name
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

    String toString () {
        "ConnectionService (serviceIdentifier:$name, owningSite $site)"
    }
}
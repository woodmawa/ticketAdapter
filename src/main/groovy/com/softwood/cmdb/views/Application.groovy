/*
 * Copyright 2018 author : Will woodman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.softwood.cmdb.views

import com.softwood.cmdb.ConfigurationItem

class Application {
    @Delegate ConfigurationItem ci

    String ipAddress

    Application() {
        ci = new ConfigurationItem()
        ci.type = "Application"
    }

    Application(ConfigurationItem ci) {
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
        //println "invoked set property for $name with value $value "
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


    String toString(){
        "Device (name:$name, software: $ci.software, host:$ci.hostname) [type:$ci.type, id:$ci.id]"
    }
}
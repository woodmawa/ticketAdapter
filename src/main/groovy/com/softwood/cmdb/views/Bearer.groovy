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
import groovy.json.JsonGenerator
import io.vertx.core.json.JsonObject

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

class Bearer {
    @Delegate
    ConfigurationItem ci


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
    def propertyMissing(String name) {
        getProperty(name)
    }

    def propertyMissing(String name, value) {
        setProperty(name, value)
    }

    /**
     * intercept regular property accesses and delegate to embedded ci
     */
    void setProperty(String name, value) {
        if (!metaClass.hasProperty(this, name)) {
            ci?."$name" = value
        } else
            metaClass.setProperty(this, name, value)
    }

    def getProperty(String name) {
        if (!metaClass.hasProperty(this, name)) {
            ci?."$name"
        } else
            this.metaClass.getProperty(this, name)
    }

    /**
     * better cleaner implementation using groovy's JsonGenerator to control the format
     * @return Alarm as JsonObject
     */
    JsonObject toJson() {
        def generator = new JsonGenerator.Options()
                .excludeNulls()
                .excludeFieldsByType(Class)
                .excludeFieldsByType(Closure)
                .addConverter(ConcurrentLinkedQueue) { ConcurrentLinkedQueue queue, String key -> queue.toArray() }
                .addConverter(LocalDateTime) { LocalDateTime t, String key -> t.toString() }
                .addConverter(UUID) { UUID uuid, String key -> uuid.toString() }
                .addConverter(Optional) { Optional opt, String key ->
                    if (opt.isPresent())
                        opt.get().toString()
                }
                .build()

        String result = generator.toJson(this)
        new JsonObject(result)

    }

    String toString() {
        "BearerService (serviceIdentifier:$name, owningSite $site)  [type:$ci.type, id:$ci.id]"
    }

}
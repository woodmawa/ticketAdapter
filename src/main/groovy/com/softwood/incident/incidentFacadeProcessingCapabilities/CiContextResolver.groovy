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
package com.softwood.incident.incidentFacadeProcessingCapabilities

import com.softwood.alarmsAndEvents.Alarm
import com.softwood.application.Application
import com.softwood.cmdb.ConfigurationItem

import java.util.concurrent.ConcurrentHashMap

class CiContextResolver {

    def registry = new ConcurrentHashMap()

    CiContextResolver() {
        registry.put("192.168.1.24", "myCpe")
    }

    /**
     *
     * @param alarm
     * @param args
     * @param resolutionStrategy
     * @return Optional with a value or empty
     */
    def resolve(Alarm alarm, args = null, Closure resolutionStrategy = null) {
        def cmdbInstance
        if (resolutionStrategy == null) {
            // if no strategy presented, fall back on the default strategy
            cmdbInstance = defaultResolve(alarm)
        } else {

            def resolver = resolutionStrategy?.clone()
            resolver.delegate = alarm

            //set alarm as the delegate for the closure and call it, passing any args if any
            cmdbInstance = resolver(args)
        }
        cmdbInstance

    }

    def defaultResolve(alarm) {
        new Optional<ConfigurationItem>(registry.get(alarm.ciReference))

    }
}

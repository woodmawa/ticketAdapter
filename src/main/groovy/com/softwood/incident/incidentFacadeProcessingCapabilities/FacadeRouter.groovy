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
import com.softwood.application.ConfigurableProjectApplication
import com.softwood.cmdb.ConfigurationItem
import com.softwood.incident.IncidentTicket
import com.softwood.incident.adapters.AdapterFactory
import com.softwood.incident.adapters.AdapterFactoryType
import com.softwood.incident.adapters.IncidentTicketAdapter


//simple fixed flow at present
//get alarm -> which ci -> use adapter to post ticket
//todo make dyanmic chaining later
class FacadeRouter {

    //returns an Optional
    def route(Alarm alarm, args = null) {

        CiContextResolver resolver = new CiContextResolver()
        resolver.resolve(alarm, args) { argList ->
            //implement resolution strategy

            def cmdbInstance

            def ciRef = alarm.ciReference
            //todo : frig to get round dependency injection proble with Dagger

            def app = Application.application
            def inventory = app.binding.inventory
            def matched = inventory.findAll { ci ->
                //def withManIp = ci.hasCharacteristic('managementIpAddress')
                //def match = (ci.managementIpAddress == ciRef)
                (ci.hasCharacteristic('managementIpAddress') &&
                        ci.managementIpAddress == ciRef) ? true : false
                //def result = (withManIp && match) ? true : false
            }
            if (matched.size() == 1) {
                cmdbInstance = matched[0]
                cmdbInstance
            } else
                matched

        }


    }

    def route(ConfigurationItem ci) {
        IncidentTicketAdapter ticketAdapter


        //get configured ticket adapter client as return to facade
        ticketAdapter = Application.application.binding.clientTicketAdapter

    }
}

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
package com.softwood.incident

import com.softwood.application.Application
import com.softwood.application.ConfigurableProjectApplication
import com.softwood.alarmsAndEvents.Alarm
import com.softwood.bus.HackEventBus
import com.softwood.cmdb.ConfigurationItem
import com.softwood.cmdb.Site
import com.softwood.cmdb.views.Device
import com.softwood.incident.adapters.AdapterFactory
import com.softwood.incident.adapters.AdapterFactoryType
import com.softwood.incident.incidentFacadeProcessingCapabilities.CiContextResolver
import com.softwood.incident.incidentFacadeProcessingCapabilities.FacadeRouter
import groovy.json.JsonSlurper
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest

import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * this is front end service class for processing alarms from OSS VIM platform
 *
 * alarms expected to be posted on the service bus, and processing starts on receipt of
 * standard alarm subscription
 *
 * First the alarm is processed by the FacadeRouter to determine which CIs in the cmdb
 * maybe related to alarm details
 *
 * for each ci we route again to get the TicketAdapter to use to send messages with
 *
 * config for this is set as start of ApplicationCconfiguration.groovy and read at runtime
 * and uses factory to return an instance of the right type
 *
 * coupling with alarms is via the system event bus
 *
 * alarms can be injected via script, or via api post on AlarmAndEvents package
 *
 */
class ManageIncidentFacadeService {

    //constructor injection not working - frig
    @Inject
    ConfigurableProjectApplication app = Application.application

    //todo make this more dynamic later
    FacadeRouter router = new FacadeRouter()
    CiContextResolver resolver = new CiContextResolver()

    ManageIncidentFacadeService() {
        def vertx = app.vertx

        def vEventBus = vertx.eventBus()

        //determine which client adapter for get from factory, and save into the binding
        String system = Application.application.binding.config.ticketAdapter.system
        def ticketAdapter = AdapterFactory.newAdapter(system, AdapterFactoryType.client)
        Application.application.binding.clientTicketAdapter = ticketAdapter

        //setup the consumer endpoint to be invoked on inbound message
        Closure handler = this.&vertxOnCpeAlarm
        vEventBus.consumer("cpeAlarm", handler)
    }


    //handler for alarms published from messaging system, using vertx event messaging
    void vertxOnCpeAlarm(message) {
        Alarm alarm = message.body()
        println "vertx MIFS tracing> on event closure: received alarm $alarm on topic $message.address"
        def matchedCi = router.route(alarm)  //should match on instances of Device
        println "vertx MIFS tracing> matched alarm ciReference to ci : $matchedCi"

        matchedCi.each { Device dci ->

            def ci = dci.ci

            def ticketAdapter = router.route(ci)
            println "vertx MIFS tracing> resolved ticket adapter to use as : $ticketAdapter, for ci : $ci"

            //fixed flow model at present
            def newTicket = new IncidentTicket(title: "my $ci.category, is broken") // as Json - ticketAdapter.createTicket ()
            newTicket.customerName = ci.customer.name
            newTicket.siteName = ci.site?.name
            newTicket.sitePostalCode = ci.site?.postalCode
            newTicket.urgency = "1"
            newTicket.priority = "high"
            newTicket.severity = "high"
            newTicket.impact = "cant trade"
            newTicket.item = ci.name
            newTicket.cmdb_ci = ci.toString()
            newTicket.requester = "will.woodman@techmahindra.com"
            newTicket.category = ci.category
            newTicket.description = """
received alarm details on :
${alarm.ciReference}, type $alarm.type,  with event charactistics 
${alarm.eventCharacteristics}
"""

            JsonObject postBody = newTicket.toJson()
            def text = postBody.encodePrettily()
            println "postBody as $text"

            def stem = Application.application.binding.uriApiStemPath
            //HttpRequest request = ticketAdapter.apiPost ("$stem/incident", postBody)//.sendJsonObject (postBody)
            HttpRequest request = ticketAdapter.apiPost("$stem/incident", newTicket) { ar ->
                //ticketAdapter.apiSend (request, postBody)

                if (ar.statusCode() == 200)
                    println "vertx MIFS tracing> Snow api POST request received response " + ar.bodyAsJsonObject().encodePrettily()
                else
                    println "vertx MIFS tracing> error, status: " + ar.statusMessage()
            }

        }

        println "vertx MIFS tracing> finished OnCpeAlarm event: done Alarm processing for $alarm"

    }
}

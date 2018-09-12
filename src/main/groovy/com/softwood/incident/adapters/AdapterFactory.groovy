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
package com.softwood.incident.adapters

import com.softwood.application.Application
import com.softwood.incident.adapters.simulators.ITSM.ItsmApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.ITSM.ItsmClientAdapterVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowClientAdapterVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient

enum AdapterFactoryType {
    client,
    server
}

enum AdapterProtocolType {
    Json, mail
}

/**
 * factory class for creating suitable client or server adapters
 *
 */
class AdapterFactory {

    //read from config held in ApplicationConfiguration.groovy
    static def adapterFactories = Application.application.binding.config.ticketAdapter.adapterFactories

    //= [SNOW :[apiSimulatorServer: SnowApiServerSimulatorVerticle, apiClient: SnowClientAdapterVerticle ],
    //        ITSM: [ApiSimulatorServer: ItsmApiServerSimulatorVerticle, apiClient: ItsmClientAdapterVerticle] ]

    def static factory

    static def newAdapter (String system, AdapterFactoryType type, Map properties=null) {

        factory = adapterFactories."${system.toUpperCase()}"

        def instance

        switch (system.toUpperCase()) {
            case "SNOW" :
                Application.application.binding.uriApiStemPath = "/api/now/table"
                break
            case "ITSM" :
                Application.application.binding.uriApiStemPath = "/api/arsys/v1"
                break
        }

        switch (type) {
            case AdapterFactoryType.server :
                 instance = (factory.apiSimulatorServer).newInstance()
                instance.configureHttpServer()
                instance.name = "$system Simulated Server"
                break;
            case AdapterFactoryType.client  :
                instance = (factory.apiClient).newInstance()
                instance.configureHttpClient()
                instance.name = "$system Simulated Client"
                break;

        }

        if (instance) {
            properties?.each { name, value ->
                instance."$name" = value        //setup any properties
            }
        }
        instance
    }
}


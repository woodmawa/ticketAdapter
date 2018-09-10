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

        switch (type) {
            case AdapterFactoryType.server :
                 instance = (factory.apiSimulatorServer).newInstance()
                instance.configureHttpServer()
                break;
            case AdapterFactoryType.client  :
                instance = (factory.apiClient).newInstance()
                instance.configureHttpClient()
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


package com.softwood.incident.adapters

import com.softwood.incident.adapters.simulators.ITSM.ItsmApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.ITSM.ItsmClientAdapterVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowClientAdapterVerticle

enum AdapterFactoryType {
    client,
    server
}

/**
 * factory class for creating suitable client or server adapters
 *
 */
class AdapterFactory {

    static def adapterFactories = [SNOW :[apiSimulatorServer: SnowApiServerSimulatorVerticle, apiClient: SnowClientAdapterVerticle ],
            ITSM: [ApiSimulatorServer: ItsmApiServerSimulatorVerticle, apiClient: ItsmClientAdapterVerticle] ]

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

//todo - work out basic standard methods
interface IncidentSystemAdapter {
    String name
    def post (message )
    def get ()

}

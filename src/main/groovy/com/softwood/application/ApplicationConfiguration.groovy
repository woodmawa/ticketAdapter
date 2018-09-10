package com.softwood.application

import com.softwood.incident.adapters.AdapterProtocolType
import com.softwood.incident.adapters.simulators.ITSM.ItsmApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.ITSM.ItsmClientAdapterVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowClientAdapterVerticle


ticketAdapter {
    simulatorEnabled = true
    system = "Snow"
    type = AdapterProtocolType.Json

    adapterFactories = [SNOW :[apiSimulatorServer: SnowApiServerSimulatorVerticle,
                               apiClient: SnowClientAdapterVerticle ],
                        ITSM: [ApiSimulatorServer: ItsmApiServerSimulatorVerticle,
                               apiClient: ItsmClientAdapterVerticle] ]

}


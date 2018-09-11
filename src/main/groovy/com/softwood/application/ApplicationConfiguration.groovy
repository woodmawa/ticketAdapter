package com.softwood.application

import com.softwood.incident.adapters.AdapterProtocolType
import com.softwood.incident.adapters.MailAdapterPlugin
import com.softwood.incident.adapters.simulators.ITSM.ItsmApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.ITSM.ItsmClientAdapterVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowClientAdapterVerticle


ticketAdapter {
    simulatorEnabled = true
    system = "Snow"
    type = AdapterProtocolType.Json
    host = "localhost"
    port = 8081

    adapterFactories = [SNOW :[apiSimulatorServer: SnowApiServerSimulatorVerticle,
                               apiClient: SnowClientAdapterVerticle,
                               mailClient: MailAdapterPlugin ],
                        ITSM: [apiSimulatorServer: ItsmApiServerSimulatorVerticle,
                               apiClient: ItsmClientAdapterVerticle,
                               mailClient: MailAdapterPlugin]
                        ]

}

alarmServer {
    host = "localhost"
    port = 8090

}

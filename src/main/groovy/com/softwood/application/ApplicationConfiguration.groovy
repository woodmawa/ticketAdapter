package com.softwood.application

import com.softwood.incident.adapters.AdapterProtocolType
import com.softwood.incident.adapters.MailAdapterPlugin
import com.softwood.incident.adapters.simulators.ITSM.ItsmApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.ITSM.ItsmClientAdapterVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowApiServerSimulatorVerticle
import com.softwood.incident.adapters.simulators.SNOW.SnowClientAdapterVerticle

import java.util.concurrent.atomic.AtomicLong



alarmServer {
    host = "localhost"
    port = 8090

}

ticketAdapter {
    simulatorEnabled = true
    simulatorPort = 8191
    simulatorHost = "localhost"
    system = "Snow"
    type = AdapterProtocolType.Json
    host = "localhost"
    port = 8091
    mail {
        server = "mail.btinternet.com"
        port = 465
        protocol = "smtp"
        from = "will.woodman@btinternet.com"
        sslEnabled = true
        defaultSubject = "email from alarm event to ticket processor"
    }

    adapterFactories = [SNOW :[apiSimulatorServer: SnowApiServerSimulatorVerticle,
                               apiClient: SnowClientAdapterVerticle,
                               mailClient: MailAdapterPlugin ],
                        ITSM: [apiSimulatorServer: ItsmApiServerSimulatorVerticle,
                               apiClient: ItsmClientAdapterVerticle,
                               mailClient: MailAdapterPlugin]
                        ]

}

requestServer {
    host = "localhost"
    port = 8082
    sequenceGenerator = new AtomicLong(9)

}

cmdbServer {
    host = "localhost"
    port = 8093

}


management{
    //you can add aditional actions via config by adding the acction name and method closure to the map
    configurableActions = [:]
    host = "localhost"
    port = 8099
}

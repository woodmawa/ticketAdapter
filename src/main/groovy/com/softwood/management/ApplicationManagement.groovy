package com.softwood.management

import com.softwood.application.Application

class ApplicationManagement {

    Map actions = [stop:this.&shutdown]

    ApplicationManagement() {
        //merge others from config
        def confActions = Application.application.binding.config.management.configurableActions
        if (confActions) {
            confActions.each {actions.put (it.key, it.value)}
        }

    }

    /**
     * actions should respond with json response for the  action that is returned as Post response
     * @param args
     * @return
     */
    def shutdown (args) {
        println "invoked application shutdown, closing all verticles "
        Application.application.vertx.close()

        //other stuff
               """{ "shutdown: {"stopping application": "completed"}}"""

    }
}

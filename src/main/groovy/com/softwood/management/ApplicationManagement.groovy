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
package com.softwood.management

import com.softwood.application.Application
import com.softwood.application.ApplicationConfiguration
import io.vertx.core.json.JsonObject

class ApplicationManagement {

    Map actions = [stop:this.&shutdown, refresh:this.&refreshConfiguration]

    ApplicationManagement() {
        //merge any other management actions that may have been defined in the app config
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
             new JsonObject ( """{ "shutdown: {"stopping application": "completed"}}""")
        System.exit(0)
    }

    def refreshConfiguration (args) {
        println "invoked application context data refresh  "

        ConfigSlurper slurper = new ConfigSlurper()
        slurper.setBinding()
        ConfigObject conf = slurper.parse (ApplicationConfiguration)

        Map confMap = conf.toSorted()
        println "confMap $confMap"

        Application.application.binding.config = conf


        new JsonObject ("""{ "shutdown: {"stopping application": "completed"}}""")

    }
}

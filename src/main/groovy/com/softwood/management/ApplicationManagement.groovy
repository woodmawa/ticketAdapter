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
import io.vertx.ext.web.RoutingContext

class ApplicationManagement {

    Map actions = [stop: this.&shutdown, shutdown: this.&shutdown, refresh: this.&refreshConfiguration]

    ApplicationManagement() {
        //merge any other management actions that may have been defined in the app config
        def confActions = Application.application.binding.config.management.configurableActions
        if (confActions) {
            confActions.each { actions.put(it.key, it.value) }
        }

    }

    /**
     * actions should respond with json response for the  action that is returned as Post response
     * @param args
     * @return
     */
    def shutdown(RoutingContext rc, args) {
        println "invoked application shutdown, closing all verticles "

        //special case - will shutdown vertx - therefore post post response to client from here before exit()
        JsonObject jsonResponse = new JsonObject("""{ "shutdown action": {"stopping application": "completed"}}""")
        def response = rc.response()

        def resultBody = jsonResponse?.encodePrettily() ?: ""

        response.putHeader("content-type", "application/json")
        def length = resultBody.getBytes().size() ?: 0
        response.putHeader("content-length", "$length")

        response.end(resultBody)

        Application.application.vertx.close()


        //other stuff
        System.exit(0)
    }

    def refreshConfiguration(RoutingContext rc, args) {
        println "invoked application context data refresh  "

        ConfigSlurper slurper = new ConfigSlurper()
        slurper.setBinding()
        ConfigObject conf = slurper.parse(ApplicationConfiguration)

        Map confMap = conf.toSorted()
        println "confMap $confMap"

        Application.application.binding.config = conf


        new JsonObject("""{ "refresh action": {"refresh application configuration": "completed"}}""")

    }
}

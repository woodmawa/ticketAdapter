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
package com.softwood.application

import com.softwood.application.bootstrap.BootStrap
import dagger.Component
import dagger.Module
import dagger.Provides
import io.vertx.core.Vertx

import javax.inject.Inject
import javax.inject.Singleton

class ProjectApp implements ConfigurableProjectApplication {

    //use binding as a delegate - calling app.method will delegate calls to binding get/set property etc
    static Binding appBinding = new Binding()
    static Vertx vertx //= Vertx.vertx()      // not firing - no vertx is injected

    def appClassInstance

    static def run ( Class<?> appClass, args) {

        //creates new projectApp and runs bootstrap etc
        ProjectApp projectApp = new ProjectApp (appClass, args)

        ConfigSlurper slurper = new ConfigSlurper()
        slurper.setBinding()
        ConfigObject conf = slurper.parse (ApplicationConfiguration)

        Map confMap = conf.toSorted()
        println "confMap $confMap"

        appBinding.config = conf
        appBinding.configMap = confMap // belt and braces

        //todo: appBinding. conf.toProperties()
        projectApp.withBinding (args) {

            def binding = delegate
            BootStrap bootStrap = new BootStrap(binding)

            bootStrap.init()

            bootStrap.destroy()
        }

        projectApp
    }

    def runScript (String scriptName){
        assert scriptName
        File script = new File(scriptName)
        runScript (script)
    }

    /**
     * evaluate a groovy script and pass the ProjectApps appBinding to the script
     * @param script
     * @return
     */
    def runScript (File script) {
        assert script.exists()

        GroovyShell shell = new GroovyShell(appBinding)
        shell.evaluate (script)
    }

    //constructor - called with Application class
    ProjectApp (Class<?> appClass, args) {

        appClassInstance = appClass.newInstance(args )

        vertx = Vertx.vertx()   //have to force in constructor at mo

        appBinding.vertx = vertx
        appBinding.projectApplication = this

        //todo - other stuff as required

    }

    def withBinding(args, closure ) {

        //set the closur binding to be appBinding
        closure.delegate = appBinding
        //run the closure
        closure (args)
    }

    void setVertx (Vertx vertx) {
        if (!this.vertx) {
            this.vertx = vertx
            appBinding.vertx = vertx
        }
        else
            println "setVertix: tried to overwrite vertx - failed "
    }

    Vertx getVertx () {
        appBinding.vertx
    }

    def getApplication () {
        appBinding.projectApplication
    }

    Binding getBinding () {
        appBinding
    }
}

//ensure dagger will generate an implementation of this type
@Component (modules=ProjectAppModule)
interface ConfigurableProjectApplication {
    Vertx getVertx()
    Binding getBinding()
    def getApplication ()
}

/**
 * Dagger 2 factory builder for classes to be injected
 * by default methods start with provide<xxx>
 */
@Module
class ProjectAppModule {
    @Provides @Singleton static Vertx providesVertx () {
        Vertx.vertx()
    }

    @Provides @Singleton static ConfigurableProjectApplication provideProjectApp (Class<?> applicationClass, args) {
        new ProjectApp (applicationClass, args)
    }
}
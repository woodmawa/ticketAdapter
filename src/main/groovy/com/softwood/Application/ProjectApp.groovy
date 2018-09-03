package com.softwood.Application

import dagger.Component
import dagger.Module
import dagger.Provides
import io.vertx.core.Vertx

import javax.inject.Inject
import javax.inject.Singleton

class ProjectApp implements ConfigurableProjectApplication {

    //use binding as a delegate - calling app.method will delegate calls to binding get/set property etc
    @Delegate Binding appBinding = new Binding()
    @Inject Vertx vertx //= Vertx.vertx()      // not firing - no vertx is injected

    def appClassInstance

    static def run ( Class<?> appClass, args) {

        //creates new projectApp and runs bootstrap etc
        ProjectApp projectApp = new ProjectApp (appClass, args)

        projectApp.withBinding (args) {

            BootStrap bootStrap = new BootStrap()

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

    def withBinding(args, closure) {
        Closure context = closure.clone()
        context.delegate = appBinding
        //run the closure
        context(args)
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
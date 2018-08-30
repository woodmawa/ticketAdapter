package com.softwood.Application

class ProjectApp {

    Binding appBinding = new Binding()


    static def run ( Class<?> appClass, args) {

        ProjectApp projectApp = new ProjectApp (appClass, args)

        projectApp.withBinding (args) {
            BootStrap bootStrap = new BootStrap()

            bootStrap.init()


            bootStrap.destroy()
        }

        projectApp
    }

    //constructor
    ProjectApp (Class<?> appClass, args) {

        appBinding.application = this
        //todo
    }

    def withBinding(args, closure) {
        Closure context = closure.clone()
        context.delegate = appBinding
        //run the closure
        context(args)
    }
}

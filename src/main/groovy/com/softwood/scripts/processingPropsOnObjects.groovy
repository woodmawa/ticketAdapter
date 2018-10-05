package com.softwood.scripts

import java.lang.reflect.Field


class Parent {
    String ps
}

class MyObject extends Parent {
    //List defaultGroovyClassFields = ['$staticClassInfo', '__$stMC', 'metaClass', '$callSiteArray']
    String st

    String getS() {
        st
    }

    def getDeclaredProperties () {
        Class clazz = this.getClass()
        List thisFields = []

        //get all the fields all way up hiererachy
        while (clazz) {
            thisFields.addAll (clazz.declaredFields)
            clazz = clazz.getSuperclass()
        }

        Map props = [:]

        thisFields.each {Field f ->
            def synthetic = f.isSynthetic()
            if(!synthetic ) {
                def accessible = f.isAccessible()
                if (!accessible)
                    f.setAccessible(true)

                props << ["$f.name": f.get(this)]
                f.setAccessible(accessible)  //reset to orig

            }

        }
        props
    }
}

class Empty {

}


def obj = new MyObject()

def props = obj.properties
def mprops = obj.metaClass.properties*.name
def methods = obj.metaClass.methods*.name




println "std props "+props
println "metaclass prop names: "+ mprops
println "metaclass method names "+ methods
println "obj:"+ new Object().class.declaredFields*.name
println "empty:"+ new Empty().class.declaredFields*.name
println "MyObjct:"+obj.class.declaredFields*.name

println "only dec fields mybobj:" + obj.getDeclaredProperties()
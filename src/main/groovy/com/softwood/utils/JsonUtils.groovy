package com.softwood.utils

import groovy.transform.CompileStatic
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

import java.time.Instant


class JsonUtils {

    def classInstanceHasBeenEncodedOnce = new LinkedHashMap()

    private Options options
    private JsonUtils (Options options) {
        this.options = options
    }

    class Options {
        private boolean excludeNulls = true
        private boolean excludeClass = true
        private boolean summaryEnabled = false
        private List excludedFieldNames = []
        private List excludedFieldTypes = []
        Map converters = new HashMap()

        //methods use method chainimg style

        Options summaryClassFormEnabled  (boolean value) {
            summaryEnabled = value
            this
        }

        Options excludeNulls (boolean value) {
            excludeNulls = value
            this
        }

        Options excludeClass (boolean value) {
            excludeClass = value
            this
        }
        Options excludeFieldByNames(String name, args=null){
            excludedFieldNames << name
            if (args) {
                args.each {excludedFieldNames << it}
            }
            this
        }

        Options excludeFieldByTypes(Class clazz, args=null){
            excludedFieldTypes << clazz
            if (args) {
                args.each {excludedFieldTypes << it}
            }
            this
        }

        Options registerConverter (Class clazz, Closure converter){
            def entry = new HashMap<Class,Closure>()
            entry.put (clazz, converter)

            converters << entry
            this
        }

        JsonUtils build () {
            JsonUtils.newInstance(this)
        }
    }

    def toJson (def pogo) {

        def supportedStandardTypes = [Integer, Double, Float, byte[], Object, String, Boolean, Instant, JsonArray, JsonObject, CharSequence, Enum]
        JsonObject json = new JsonObject()

        if (classInstanceHasBeenEncodedOnce[(pogo)]) {
            def item = (pogo.hasProperty ("name")) ?pogo.name: pogo.getClass().simpleName
            json.put(item, pogo.toString())
            return
        }

        /* List || pogo instanceof Queue )*/
        if (pogo instanceof Iterable) {
            //println "process an list/queue type"
            if (options.excludeNulls) {
                if (pogo.size() == 0)
                    return
            }
            JsonArray jList = new JsonArray ()
            pogo.each {
                def jItem = this.toJson (it)
                if (jItem)
                    jList.add (jItem)
            }
            jList
            return jList
        } else if (pogo instanceof Map) {
            if (options.excludeNulls) {
                if (pogo.size() == 0)
                    return
            }
             pogo.each {
                if (supportedStandardTypes.contains (it.value.getClass())) {
                    json.put (it.key, it.value)
                } else {
                    def jval = this.toJson (it.value)
                    if (jval)
                        json.put  (it.key, jval )
                }
            }
            //println "> return json : $json"
            return json
        }

        Map props = pogo.properties
        Closure converter

        for (prop in props) {
            if (prop.value == null) {
                if (options.excludeNulls == true)
                    continue
                else
                    json.putNull(prop.key)
            }
            else if (prop.value instanceof Class && options.excludedFieldTypes.contains(prop.value))
                continue
            else if (options.excludedFieldNames.contains (prop.key) )
                continue
            else if ( (converter = options.converters.get (prop.value.getClass())) ) {
                json.put(prop.key, converter(prop.value))
                continue
            }
            else if (prop.value.getClass() == Optional ) {
                def value
                if (prop.value.isPresent()) {
                    value = prop.value.get()
                    def valueConverter = options.converters.get (prop.value.getClass())
                    if (valueConverter)
                        json.put (prop.key, valueConverter (value))  //will break for unsupported types
                    else
                        json.put (prop.key, prop.value)
                }
                continue
            }
            else if (prop.value.getClass() == UUID) {
                json.put (prop.key, prop.value.toString())
                continue
            }
            else if (prop.value.respondsTo("toJson")) {
                //type already has existing method to get JsonObject so use this
                println "prop '${prop?.key}', with type '${prop?.value?.getClass()}' supports toJson(), prop.value methods " + prop.value?.metaClass?.methods.collect {it.name}

                def retJson = prop.value.invokeMethod("toJson", null)
                json.put (prop.key, retJson)
                continue
            }
            else if (prop.key == "class" && prop.value instanceof Class ) {
                if (!options.excludeClass)
                    json.put ("classType", prop.value.canonicalName)
                continue
            } else if (prop.value instanceof Enum ) {
                json.put (prop.key, prop.value.toString())
            }
            else {
                if (supportedStandardTypes.contains (prop.value.getClass())) {
                    json.put (prop.key, prop.value)
                } else {
                    Map valProps = prop.value.properties
                    def jsonEncClass
                    classInstanceHasBeenEncodedOnce.putAll ([(pogo) : new Boolean (true)])

                    if (!options.summaryEnabled) {
                        jsonEncClass = this?.toJson(prop.value)
                        if (jsonEncClass)
                            json.put(prop.key, jsonEncClass)
                    }else {
                        //if summary enabled just put the field and toString form
                        if (options.excludeClass == false) {
                            def wrapper = new JsonObject ()
                            wrapper.put("classType", prop.value.getClass().canonicalName)
                            wrapper.put (prop.key, prop.value.toString())
                            json.put("$prop.key", wrapper)
                        } else
                            json.put (prop.key, prop.value.toString())
                    }
                }
            }
        }
        classInstanceHasBeenEncodedOnce.clear()
        json

    }
}
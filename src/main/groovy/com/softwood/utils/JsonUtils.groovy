package com.softwood.utils

import groovy.transform.CompileStatic
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.apache.tools.ant.taskdefs.PathConvert

import java.time.Instant
import java.time.temporal.Temporal
import java.util.concurrent.ConcurrentLinkedQueue


class JsonUtils {

    def supportedStandardTypes = [Integer, Double, Float, byte[], Object, String, Boolean, Instant, JsonArray, JsonObject, CharSequence, Enum]
    def classInstanceHasBeenEncodedOnce = new LinkedHashMap()
    def iterLevel = 0

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
        Map converters = new HashMap<Class, Closure>()

        //methods use method chainimg style

        Options () {
            converters.put(Date, {it.toString()})
            converters.put(Calendar, {it.toString()})
            converters.put(Temporal, {it.toString()})
            converters.put(URI, {it.toString()})
            converters.put(UUID, {it.toString()})
            this
        }

        Options summaryClassFormEnabled  (boolean value) {
            summaryEnabled = value
            this
        }

        Options excludeNulls (boolean value = true){
            excludeNulls = value
            this
        }

        Options excludeClass (boolean value = true) {
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
            converters.put (clazz, converter)
            this
        }

        JsonUtils build () {
            def generator = JsonUtils.newInstance(this)
            generator
        }
    }

    def toJson (def pogo) {

        def json
        iterLevel++

        if (Iterable.isAssignableFrom(pogo.getClass()) )
            json =  encodeIterableType(pogo)
        else if (Map.isAssignableFrom(pogo.getClass()))
            json =  encodeMapType(pogo )
        else {
            json = new JsonObject()
            if (classInstanceHasBeenEncodedOnce[(pogo)]) {
                println "already encoded pogo $pogo so just put toString summary and stop recursing"

                def item = (pogo.hasProperty ("name")) ? pogo.name : pogo.getClass().simpleName
                json.put(item, pogo.toString())
                iterLevel--
                return json
            }

            if (!classInstanceHasBeenEncodedOnce.containsKey((pogo))) {
                classInstanceHasBeenEncodedOnce.putAll([(pogo): new Boolean(true)])
                println "iterLev $iterLevel: adding pogo $pogo encoded once list"
            }


            Map props = pogo.properties
            def iterableFields = props.findAll {Iterable.isAssignableFrom(it.value.getClass())}
            def nonIterableFields = props - iterableFields

            def jsonFields = new JsonObject()
            for (prop in nonIterableFields) {
                def field = encodeFieldType(prop)
                if (field ) {
                    jsonFields.put(prop.key, field)
                    //json.put (pogo.getClass().simpleName, jsonFields)
                }

            }
            for (prop in iterableFields){
                def arrayResult = encodeIterableType ( prop.value)
                if (arrayResult) {
                    jsonFields.put(prop.key, arrayResult)
                    //json.put(pogo.getClass().simpleName, jsonFields)
                }
            }
            json.put (pogo.getClass().simpleName, jsonFields)
        }
        iterLevel--
        if (iterLevel == 0) {
            classInstanceHasBeenEncodedOnce.clear()
        }
        json

    }

    private def encodeFieldType (prop) {
        def json = new JsonObject()
        Closure converter

        if (prop.value == null) {
            if (options.excludeNulls == true)
                return
            else
                return json.putNull(prop.key)
        }
        else if (prop.value instanceof Class && options.excludedFieldTypes.contains(prop.value))
            return
        else if (options.excludedFieldNames.contains (prop.key) )
            return
        else if ( (converter = classImplementsConverterType (prop.value.getClass())) ) {
            converter.delegate = prop.value
            return converter(prop.value)
        }
        else if (prop.value.getClass() == Optional ) {
            def value
            def result
            if (prop.value.isPresent()) {
                value = prop.value.get()
                def valueConverter = options.converters.get (prop.value.getClass())
                if (valueConverter)
                    result = valueConverter (value)  //will break for unsupported types
                else
                    result = prop.value
            }
            return result
        }
        else if (prop.value.respondsTo("toJson")) {
            //type already has existing method to get JsonObject so use this
            println "prop '${prop?.key}', with type '${prop?.value?.getClass()}' supports toJson(), prop.value methods " + prop.value?.metaClass?.methods.collect {it.name}

            def retJson = prop.value.invokeMethod("toJson", null)
            return retJson
        }
        else if (prop.key == "class" && prop.value instanceof Class ) {
            def name
            if (!options.excludeClass) {
                name = prop.value.canonicalName
            }
            return name
        } else if (prop.value instanceof Enum ) {
            return prop.value.toString()
        }
        else {
            if (supportedStandardTypes.contains (prop.value.getClass())) {
                return prop.value
            } else {
                Map valProps = prop.value.properties
                def jsonEncClass

                if (!options.summaryEnabled) {
                    jsonEncClass = this?.toJson(prop.value)
                    if (jsonEncClass)
                        return jsonEncClass
                }else {
                    //if summary enabled just put the field and toString form
                    if (options.excludeClass == false) {
                        def wrapper = new JsonObject ()
                        wrapper.put("classType", prop.value.getClass().canonicalName)
                        wrapper.put (prop.key, prop.value.toString())
                        return wrapper
                    } else
                        return prop.value.toString()
                }
            }
        }
    }


    private JsonArray encodeIterableType (iterable) {
        JsonObject json = new JsonObject()
        JsonArray jList = new JsonArray ()

        /* List || instanceof Queue )*/
        if (Iterable.isAssignableFrom(iterable.getClass())) {
            //println "process an list/queue type"
            if (options.excludeNulls) {
                if (iterable.size() == 0) {
                    return
                }
            }

            iterable.each {
                println "encodeIterableType:  iterlevel $iterLevel:> given iterable field param, adding each item pogo: $it  to jsonArray "
                if (supportedStandardTypes.contains (it.getClass())) {
                    jList.add (it.value)
                } else {
                    def jItem = this.toJson(it)
                    if (jItem)
                        jList.add(jItem)
                }
            }

        }
        return jList

    }

    private JsonObject encodeMapType (map) {
        JsonObject json = new JsonObject()

        /* Map */
        if (map instanceof Map) {
            if (options.excludeNulls) {
                if (map.size() == 0) {
                    return
                }
            }

            map.each {
                println "encodeMapType:  iterlevel $iterLevel:> given map field param, adding each item pogo: $it  to JsonObject "
                if (supportedStandardTypes.contains (it.value.getClass())) {
                    json.put (it.key, it.value)
                } else {
                   json.put (it.key, this.toJson(it))
                }
            }

            return json
        }
    }

    private Closure classImplementsConverterType ( clazz ) {

        //eg. is Temporal assignable from LocalDateTime

        def entry = options.converters.find {Map.Entry rec ->
            def key = rec.key
            key.isAssignableFrom(clazz)
        }
        entry?.value
    }
}

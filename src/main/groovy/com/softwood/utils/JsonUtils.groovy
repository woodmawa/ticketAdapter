package com.softwood.utils

import groovy.transform.CompileStatic
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.apache.tools.ant.taskdefs.PathConvert

import java.time.Instant
import java.time.temporal.Temporal
import java.util.concurrent.ConcurrentLinkedQueue


class JsonUtils {

    List supportedStandardTypes = [Integer, Double, Float, byte[], Object, String, Boolean, Instant, JsonArray, JsonObject, CharSequence, Enum]
    Map classInstanceHasBeenEncodedOnce = new LinkedHashMap()
    int iterLevel = 0

    private Options options
    private JsonUtils (Options options) {
        this.options = options
    }

    class Options {

        private boolean optionalLinks = false
        private boolean includeVersion = false
        private boolean compoundDocument = false
        private boolean excludeNulls = true
        private boolean excludeClass = true
        private boolean summaryEnabled = false
        private List excludedFieldNames = []
        private List excludedFieldTypes = []
        Map converters = new HashMap<Class, Closure>()
        //todo too hard codes still
        Closure linkNamingStrategy = {String linkType, Class type, String attributeName, String id ->
            if (linkType == "self") {
                return "http://localhost:port/api/${type.simpleName}/$id"
            }
            if (linkType == "parent") {
                return "http://localhost:port/api/${type.simpleName}/$id/relationships/$attributeName"
            }
            if (linkType == "related") {
                return "http://localhost:port/api/${type.simpleName}/$id/$attributeName"

            }
        }

        //methods use method chainimg style

        Options () {
            converters.put(Date, {it.toString()})
            converters.put(Calendar, {it.toString()})
            converters.put(Temporal, {it.toString()})
            converters.put(URI, {it.toString()})
            converters.put(UUID, {it.toString()})
            this
        }

        Options jsonApiIncludeVersion  (boolean value = false) {
            includeVersion = value
            this
        }

        Options jsonApiOptionalLinks  (boolean value = false) {
            optionalLinks = value
            this
        }

        Options jsonApiCompoundDocument  (boolean value = false) {
            compoundDocument = value
            this
        }

        Options summaryClassFormEnabled  (boolean value = false) {
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

    Closure getLinkNamingStrategy () {
        options.linkNamingStrategy
    }

    @CompileStatic
    def toJsonApi (def pogo, JsonArray includedArray = null) {

        iterLevel++
        JsonArray compoundDocumentIncludedArray
        def jsonApiEncoded = true


        def jsonApiObject = new JsonObject()
        JsonObject jsonAttributes = new JsonObject()
        JsonObject jsonRelationships = new JsonObject()

        if (iterLevel == 1 && options.compoundDocument){
            compoundDocumentIncludedArray = new JsonArray()
            includedArray = compoundDocumentIncludedArray
        }

        if (Iterable.isAssignableFrom(pogo.getClass()) )
            jsonApiObject =  encodeIterableType(pogo as Iterable)
        else if (Map.isAssignableFrom(pogo.getClass()))
            jsonApiObject =  encodeMapType(pogo as Map, jsonApiEncoded, includedArray )
        else {
            def json = new JsonObject()
            if (classInstanceHasBeenEncodedOnce[(pogo)]) {
                println "already encoded pogo $pogo so just stop recursing"

                iterLevel--
                return
            }

            if (!classInstanceHasBeenEncodedOnce.containsKey((pogo))) {
                classInstanceHasBeenEncodedOnce.putAll([(pogo): new Boolean(true)])
                //println "iterLev $iterLevel: adding pogo $pogo encoded once list"
            }

            Map props = pogo.properties
            def iterableFields = props.findAll {Iterable.isAssignableFrom(it.value.getClass())}
            def nonIterableFields = props - iterableFields

            jsonAttributes = new JsonObject()
            //do attributes
            for (prop in nonIterableFields) {
                def field = encodeFieldType(prop, jsonApiEncoded, includedArray)
                if (field ) {
                    //if field is itself a JsonObject add to relationhips
                    if (field instanceof JsonObject) {
                        def id = (prop.value.hasProperty("id")) ? (pogo as GroovyObject).getProperty("id") : "tba"
                        def type = prop.value.getClass().simpleName
                        JsonObject container = new JsonObject()
                        JsonObject data = new JsonObject()
                        data.put("type", type)
                        data.put("id", id)
                        if (options.optionalLinks) {
                            JsonObject links = new JsonObject()
                            Closure linkNames = this.getLinkNamingStrategy()
                            links.put("self", linkNames ("parent", pogo.getClass(), (String)prop.key, "$id" ))
                            links.put("related", linkNames ("related", pogo.getClass(), (String)prop.key, "$id" ))
                            container.put("links", links)
                        }
                        container.put("data", data)
                        jsonRelationships.put ((String)prop.key, container)
                    }
                    else
                        //if basic field type - add to attributes
                        jsonAttributes.put((String)prop.key, field)
                }

            }

            //do relationships
            for (prop in iterableFields){
                def arrayResult = encodeIterableType ( (Iterable)prop.value, jsonApiEncoded, includedArray)
                if (arrayResult) {
                    JsonObject container = new JsonObject ()
                    if (options.optionalLinks) {
                        JsonObject links = new JsonObject()
                        //fix hard coded links, use naming Strategy
                        links.put("self", "http://xxx:yy/api/<parent res>/<p_id>/relationships/<entity> - array field type of related entity link uri here ")
                        links.put("related", "http://xxx:yy/api/<parent res>/<p_id>/<this entity> - array field type of related entity uri here ")
                        container.put("links", links)
                    }
                    container.put("data", arrayResult)
                    jsonRelationships.put ("$prop.key", container)
                }
            }

            if (options.compoundDocument) {
                if (iterLevel > 1) {
                    //construct this sublevels object for compoundDoc included section
                    //ensures we dont encode object as well as put in included section
                    JsonObject container = new JsonObject()

                    String type  = pogo.getClass().simpleName
                    String id = pogo.hasProperty("id") ? (pogo as GroovyObject)?.getProperty("id") : "1"
                    container.put("type", type)
                    container.put("id", id)
                    if (jsonAttributes && jsonAttributes.size() != 0)
                        container.put("attributes", jsonAttributes)
                    if (jsonRelationships && jsonRelationships.size() != 0)
                        container.put("relationships", jsonRelationships)
                    if (options.optionalLinks) {
                        JsonObject links = new JsonObject()
                        Closure linkNames = getLinkNamingStrategy()
                        links.put("self", linkNames ("self", pogo.getClass(), "", id ))
                        container.put("links", links)
                    }

                    (includedArray as JsonArray).add(container)
                }
            }

        }

        //for non sub level object
        if (iterLevel > 1) {
            if (jsonRelationships && jsonRelationships.size() >0)
                (jsonApiObject as JsonObject).put("relationships", jsonRelationships)

        }

        iterLevel--
        if (iterLevel == 0) {
            //format the final document to back to the client
            JsonObject container = new JsonObject()
            if (options.includeVersion)
                container.put ("jsonapi", "version1.0")
            String type = pogo.getClass().simpleName
            def  id = pogo.hasProperty("id") ? (pogo as GroovyObject)?.getProperty("id") : "1"
            container.put("type", type)
            container.put("id", id )
            if (jsonAttributes && jsonAttributes.size() != 0)
                container.put("attributes", jsonAttributes)
            if (options.optionalLinks) {
                JsonObject links = new JsonObject ()
                Closure linkNames = options.getLinkNamingStrategy()
                links.put ("self", linkNames ("self", pogo.getClass(), "", "$id"))
                container.put ("links", links)
            }
            if (jsonRelationships && jsonRelationships.size() != 0)
                container.put("relationships", jsonRelationships)
            if (options.compoundDocument) {
                 container.put ("included", compoundDocumentIncludedArray)
            }
            (jsonApiObject as JsonObject).put("data", container)

            classInstanceHasBeenEncodedOnce.clear()
        }
        jsonApiObject

    }

    @CompileStatic
    private def encodeFieldType (Map.Entry prop, boolean jsonApiEncoded = false, JsonArray includedArray = null) {
        def json = new JsonObject()
        Closure converter

        if (prop.value == null) {
            if (options.excludeNulls == true)
                return
            else
                return json.putNull((String)prop.key)
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
            if ((prop.value as Optional).isPresent()) {
                value = (prop.value as Optional).get()
                Closure valueConverter = options.converters.get (prop.value.getClass())
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
                name = (prop.value as Class).canonicalName
            }
            return name
        } else if (prop.value instanceof Enum ) {
            return prop.value.toString()
        }
        else {
            if (supportedStandardTypes.contains (prop.value.getClass())) {
                return prop.value
            } else {
                def jsonEncClass

                if (!options.summaryEnabled) {
                    if (!jsonApiEncoded) {
                        jsonEncClass = this?.toJson(prop.value)
                    } else {
                        jsonEncClass = this?.toJsonApi(prop.value, includedArray)
                    }
                    if (jsonEncClass)
                        return jsonEncClass
                }else {
                    //if summary enabled just put the field and toString form
                    if (options.excludeClass == false) {
                        def wrapper = new JsonObject ()
                        wrapper.put("classType", prop.value.getClass().canonicalName)
                        wrapper.put ((String)prop.key, prop.value.toString())
                        return wrapper
                    } else
                        return prop.value.toString()
                }
            }
        }
    }


    //@CompileStatic
    private JsonArray encodeIterableType (Iterable iterable, boolean jsonApiEncoded = false, JsonArray includedArray=null) {
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
                if (supportedStandardTypes.contains (it.getClass())) {
                    jList.add (it)
                } else {
                    def jItem
                    if (!jsonApiEncoded) {
                        jItem = this.toJson(it)
                    }
                    else {
                        //in json api the entries in array get encoded as rows of 'data':
                        def id = (it.hasProperty ("id")) ? (it as GroovyObject).getProperty("id") : "tba"
                        def type = it.getClass().simpleName
                        jItem = new JsonObject()
                        jItem.put ("type", type)
                        jItem.put ("id", id)
                        if (options.compoundDocument) {
                            //encode each iterable object, which will add and compoundDoc 'included' entries
                            def encodedClassInstance = toJsonApi (it, includedArray )
                            //println "iterLevel : $iterLevel, encIterableType, compoundDoc, enc class $it, encodes as $encodedClassInstance "
                        }

                    }
                    if (jItem)
                        jList.add(jItem)
                }
            }

        }
        return jList

    }

    @CompileStatic
    private JsonObject encodeMapType (map, jsonApiEncoded = false, JsonArray includedArray=null) {
        JsonObject json = new JsonObject()

        /* Map */
        if (map instanceof Map) {
            if (options.excludeNulls) {
                if (map.size() == 0) {
                    return
                }
            }

            map.each {Map.Entry it ->
                println "encodeMapType:  iterlevel $iterLevel:> given map field param, adding each item pogo: $it  to JsonObject "
                if (supportedStandardTypes.contains (it.value.getClass())) {
                    json.put ((String) it.key, it.value)
                } else {
                    def jItem
                    if (!jsonApiEncoded) {
                        jItem = this.toJson(it.value)
                    }
                    else {
                        jItem = this.toJsonApi(it.value, includedArray)
                    }
                    if (jItem)
                        json.put ((String)it.key, jItem)
                }
            }

            return json
        }
    }

    @CompileStatic
    private Closure classImplementsConverterType (Class<?> clazz ) {

        //eg. is Temporal assignable from LocalDateTime

        def entry = options.converters.find {Map.Entry rec ->
            Class key = rec.key
            key.isAssignableFrom(clazz)
        }
        entry?.value
    }
}

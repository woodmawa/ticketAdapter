/*
 * Copyright 2018 Will woodman.
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
package com.softwood.alarmsAndEvents

import com.softwood.application.Application
import com.softwood.application.ConfigurableProjectApplication
import groovy.json.JsonGenerator
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

import javax.inject.Inject
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

//import grails.events.annotation.*


class Alarm implements Serializable {

    //frig for dagger IoC not working
    @Inject
    ConfigurableProjectApplication app = Application.application

    //we want Alarm to be a flavour of event so use delegation
    @Delegate
    final Event event

    Alarm(event) {
        // Register codec for alarm message
        this.event = event
    }

    Alarm() {
        event = new Event()
    }

    //could use @Publisher ('<name>') defaults same as method
    //@Publisher('cpeAlarm')
    def generateAlarm() {
        println "alarm: publish alarm on 'cpeAlarm'"

        def vertx = app.vertx

        assert vertx
        def eventBus = vertx.eventBus()

        eventBus.publish("cpeAlarm", this)  //vertx platform wraps with a message
        this
    }

    /**
     *     catch property missing on map constructor call, and delegate to the embedded ci
     */
    def propertyMissing(String name) {
        getProperty(name)
    }

    def propertyMissing(String name, value) {
        setProperty(name, value)
    }

    /**
     * intercept regular property accesses and delegate to embedded ci
     */
    void setProperty(String name, value) {
        //println "invoked set property for $name with value $value "
        if (!metaClass.hasProperty(this, name)) {
            event?."$name" = value
        } else
            metaClass.setProperty(this, name, value)
    }

    def getProperty(String name) {
        if (!metaClass.hasProperty(this, name)) {
            event?."$name"
        } else
            this.metaClass.getProperty(this, name)
    }

    /**
     * better cleaner implementation using groovy's JsonGenerator to control the format
     * @return Alarm as JsonObject
     */
    JsonObject toJson() {
        def generator = new JsonGenerator.Options()
                .excludeNulls()
                .excludeFieldsByType(Class)
                .excludeFieldsByName("app")
                .excludeFieldsByName("event")
                .addConverter(LocalDateTime) { LocalDateTime t, String key ->
                    t.toString()
                }
                .build()

        String result = generator.toJson(this)
        new JsonObject(result)

    }


    JsonObject toJsonOld() {
        JsonObject json = new JsonObject()

        Map props = this.properties
        props.each { key, value ->
            if (value instanceof ConcurrentLinkedQueue)
                return
            else if (value instanceof Class)
                return
            else if (value instanceof LocalDateTime) {
                json.put(key, value.toString())
            } else if (value == null)
                return
            else if (key == "app")
                return
            else if (key == "event")
                return
            else if (key == "eventCharacteristics") {
                JsonObject details = new JsonObject()
                value.each { details.put(it.key, it.value) }
                json.put(key, details)
            } else {
                println "adding $key and value : $value to json"

                json.put(key, value)
            }
        }

        //todo encode other values later
        json

    }

    String toString() {
        "Alarm (id:$event.id, type:$event.type, name:$event.name, ciReference:$event.ciReference)"
    }

}

//default codec for custom Alarm type to regsiter with vertx eventBus
class AlarmMessageCodec implements MessageCodec<Alarm, Alarm> {

    String name

    String name() {
        // Each codec must have a unique name.
        // This is used to identify a codec when sending a message and for unregistering codecs.
        return this.getClass().getSimpleName();
    }

    @Override
    void encodeToWire(Buffer buffer, Alarm alarm) {
        // Easiest ways is using JSON object
        JsonObject jsonToEncode = new JsonObject()
        jsonToEncode.put("id", alarm.id)
        jsonToEncode.put("type", alarm.type)
        jsonToEncode.put("name", alarm.name)
        jsonToEncode.put("eventCharacteristics", alarm.eventCharacteristics)
        jsonToEncode.put("createdDate", alarm.createdDate.toString())

        // Encode object to string
        String jsonToStr = jsonToEncode.encode()

        // Length of JSON: is NOT characters count
        int length = jsonToStr.getBytes().length

        // Write data into given buffer
        buffer.appendInt(length)
        buffer.appendString(jsonToStr)

    }

    @Override
    Alarm decodeFromWire(int pos, Buffer buffer) {
        // My custom message starting from this *position* of buffer
        int _pos = position

        // Length of JSON
        int length = buffer.getInt(_pos)

        // Get JSON string by it`s length
        // Jump 4 because getInt() == 4 bytes
        String jsonStr = buffer.getString(_pos += 4, _pos += length)
        JsonObject contentJson = new JsonObject(jsonStr)

        // Get fields
        int id = contentJson.getInteger("id")
        String type = contentJson.getString("type")
        String name = contentJson.getString("name")
        Map eventCharacteristics = contentJson.getMap("eventCharacteristics")
        String createdDateString = contentJson.getString("createdDate")
        LocalDateTime createdDate = LocalDateTime.parse(createdDateString)

        // We can finally create custom message object
        return new Alarm(id: id, type: type, name: name, eventCharacteristics: eventCharacteristics, createdDate: createdDate)
    }

    @Override
    Alarm transform(Alarm alarm) {
        return alarm
    }

    byte systemCodecID() {
        // Always -1
        return -1
    }

}
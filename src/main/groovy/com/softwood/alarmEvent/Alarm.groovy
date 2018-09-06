package com.softwood.alarmEvent

import com.softwood.Application.ConfigurableProjectApplication
import com.softwood.bus.HackEventBus
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.JsonObject

import javax.inject.Inject
import java.time.LocalDateTime

//import grails.events.annotation.*


class Alarm implements Serializable{

    @Inject ConfigurableProjectApplication app

    //we want Alarm to be a flavour of event so use delegation
    @Delegate final Event event

    Alarm (event) {
        this.event = event
    }

    //frig for dagger IoC not working
    Alarm (ConfigurableProjectApplication app, event) {
        this (event)

        // Register codec for alarm message
        app.vertx.eventBus().registerDefaultCodec(Alarm.class, new AlarmMessageCodec())


        this.app = app
    }

    //could use @Publisher ('<name>') defaults same as method
    //@Publisher('cpeAlarm')
    def generateAlarm () {
        println "alarm: publish alarm on 'cpeAlarm'"

        def vertx = app.vertx

        assert vertx
        def eventBus = vertx.eventBus()

        eventBus.publish("cpeAlarm", this)
        //eventBus.publish("cpeAlarm", this)  //  use vertx event bus
        //publish ("cpeAlarm", this)  //manually publish event on 'cpeAlarm' topic
        //HackEventBus.publish ("cpeAlarm", this) //stopped for now using vertx eventBus
        this
    }

    String toString () {
        "Alarm (id:$event.id, type:$event.type, name:$event.name, emitter:$event.ciReference)"
    }

}

//default codec for custom Alarm type to regsiter with vertx eventBus
class AlarmMessageCodec implements MessageCodec<Alarm, Alarm> {

    String name

    String name() {
        // Each codec must have a unique name.
        // This is used to identify a codec when sending a message and for unregistering codecs.
        return this.getClass().getSimpleName();    }

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
        String jsonStr = buffer.getString(_pos+=4, _pos+=length)
        JsonObject contentJson = new JsonObject(jsonStr)

        // Get fields
        int id = contentJson.getInteger("id")
        String type = contentJson.getString("type")
        String name = contentJson.getString("name")
        Map eventCharacteristics = contentJson.getMap ("eventCharacteristics")
        String createdDateString = contentJson.getString ("createdDate")
        LocalDateTime createdDate = LocalDateTime.parse(createdDateString)

        // We can finally create custom message object
        return new Alarm (id:id, type:type, name:name, eventCharacteristics: eventCharacteristics, createdDate:createdDate)
    }

    @Override
    Alarm transform(Alarm alarm) {
        return alarm
    }

    byte systemCodecID () {
        // Always -1
        return -1
    }

}
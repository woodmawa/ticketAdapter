package com.softwood.bus

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class HackEventBus {

    static Map subscribers = new ConcurrentHashMap()

    static def subscribe (topic, handler) {
        ConcurrentLinkedQueue handlers = subscribers.get(topic)
        if (!handlers)
            handlers = new ConcurrentLinkedQueue()
        handlers << handler

        subscribers.put(topic, handlers)
    }

    static def publish (topic, event) {
        //handlers is array of closures
        ConcurrentLinkedQueue handlers = subscribers.get(topic)
        if (handlers) {
            handlers.each {it.call (event, topic)}
        }
    }
}

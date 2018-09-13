package com.softwood.cmdb.cmdbApi

import com.softwood.application.Application
import groovy.json.JsonGenerator
import io.vertx.core.json.JsonObject

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

class CmdbDbServices {

    private static def inventory = Application.application.binding.inventory
    private static def customers = Application.application.binding.customers

    static def db = [customers:customers, inventory:inventory]

    def ciList (String type, Closure filter=null) {
        if (type == null)
            type == "all"

        def inventoryList

        if (!filter) {
            if (type == "all")
                inventoryList = inventory.collect().toList()
            else
             inventoryList = inventory.findAll{it?.type.toLowerCase() == type.toLowerCase()}.toList()
        } else {

        }
        inventoryList
    }



}

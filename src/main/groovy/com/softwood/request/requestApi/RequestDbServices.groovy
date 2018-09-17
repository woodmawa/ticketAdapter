package com.softwood.request.requestApi

import com.softwood.application.Application

import java.util.concurrent.ConcurrentHashMap

class RequestDbServices {

    private static def requests = Application.application.binding.inventory
    private static def customers = Application.application.binding.customers

    static def requestsDb = [customers:customers, requests:requests]

    RequestDbServices () {
        if (requests == null) {
            Application.application.binding.requests = new ConcurrentHashMap<>()

        }
    }

    static RequestDbServices getDb () {
        requestsDb
    }


    def reqList (String type, Closure filter=null) {
        if (type == null)
            type == "all"

        def requestList

        if (!filter) {
            if (type == "all")
                requestList = inventory.collect().toList()
            else
             requestList = inventory.findAll{it?.type.toLowerCase() == type.toLowerCase()}.toList()
        } else {

        }
        requestList
    }



}

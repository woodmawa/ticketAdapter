package com.softwood.request.requestApi

import com.softwood.application.Application
import com.softwood.cmdb.Customer

import java.security.InvalidParameterException
import java.util.concurrent.ConcurrentHashMap

class RequestDbServices {

    private static def requests = Application.application.binding.requests
    private static def customers = Application.application.binding.customers

    static def requestsDb = [customers: customers, requests: requests]

    RequestDbServices() {
        if (requests == null) {
            Application.application.binding.requests = new ConcurrentHashMap<>()

        }
    }

    static RequestDbServices getDb() {
        requestsDb.requests
    }

    def requestListSize() {
        requests.size()
    }

    def requestList(Closure filter = null) {

        def requestList

        if (!filter) {
            requestList = requests.collect().toList()
        } else {

        }
        requestList
    }

    def requestByCustomerList(Customer cust, Closure filter = null) {

        def requestList

        if (!filter) {
            requestList = requests.findAll { it.customer.name == cust.name }.toList()
        } else {

        }
        requestList
    }

    def getRequestById(id) {

        def result

        if (!id) {
            result = requests.collect().toList()
        } else {
            if (id instanceof Long)
                result = requests.find { it.id == id }
            else if (id instanceof String) {
                Long num = Long.parseLong(id)
                if (num)
                    result = requests.find { it.id.toString() == "$id".toString() }
                else
                    throw new InvalidParameterException(message: "id should a number, found $id instead")
            } else
                throw new InvalidParameterException(message: "id should be Long or a String")
        }
        result
    }
}

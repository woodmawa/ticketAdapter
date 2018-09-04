package com.softwood.incident.adapters.simulators

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern;

public class SnowSimulatorIMDB {

    ConcurrentHashMap snowImdb = new ConcurrentHashMap()
    JsonObject defaultTicket
    Long startRecordFrom = Long.decode ("0x4d2")  //convert hex to long
    int numberEnding = 1

    SnowSimulatorIMDB () {
        String  defaultBaseTicketString = """
{
  "result": {
    "upon_approval": "proceed",
    "location": "",
    "expected_start": "",
    "reopen_count": "0",
    "close_notes": "",
    "additional_assignee_list": "",
    "impact": "2",
    "urgency": "3",
    "correlation_id": "",
    "sys_tags": "",
    "sys_domain": {
      "link": "https://instance.service-now.com/api/now/table/sys_user_group/global",
      "value": "global"
    },
    "description": "tried my printer many times and cant get any response, lights etc ",
    "group_list": "",
    "priority": "3",
    "delivery_plan": "",
    "sys_mod_count": "0",
    "work_notes_list": "",
    "business_service": "",
    "follow_up": "",
    "closed_at": "",
    "sla_due": "",
    "delivery_task": "",
    "sys_updated_on": "${LocalDateTime.now()}",
    "parent": "",
    "work_end": "",
    "number": "INC0010001",
    "closed_by": "",
    "work_start": "",
    "calendar_stc": "",
    "category": "inqury",  
    "business_duration": "",
    "incident_state": "1",
    "activity_due": "",
    "correlation_display": "",
    "company": "unknown",
    "active": "true",
    "due_date": "",
    "assignment_group": {
      "link": "https://instance.service-now.com/api/now/table/sys_user_group/287ebd7da9fe198100f92cc8d1d2154e",
      "value": "287ebd7da9fe198100f92cc8d1d2154e"
    },
    "caller_id": "",
    "knowledge": "false",
    "made_sla": "true",
    "comments_and_work_notes": "",
    "parent_incident": "",
    "state": "1",
    "user_input": "",
    "sys_created_on": "${LocalDateTime.now()}",
    "approval_set": "",
    "reassignment_count": "0",
    "rfc": "",
    "child_incidents": "0",
    "opened_at": "${LocalDateTime.now()}",
    "short_description": "My printer is not working",
    "order": "",
    "sys_updated_by": "admin",
    "resolved_by": "",
    "notify": "1",
    "upon_reject": "cancel",
    "approval_history": "",
    "problem_id": "",
    "work_notes": "",
    "calendar_duration": "",
    "close_code": "",
    "sys_id": "4d2",  //internal ticket id 
    "approval": "not requested",
    "caused_by": "",
    "severity": "3",
    "sys_created_by": "admin",
    "resolved_at": "",
    "assigned_to": "",
    "business_stc": "",
    "wf_activity": "",
    "sys_domain_path": "/",
    "cmdb_ci": "",
    "opened_by": {
      "link": "https://instance.service-now.com/api/now/table/sys_user/6816f79cc0a8016401c5a33be04be441",
      "value": "6816f79cc0a8016401c5a33be04be441"
    },
    "subcategory": "",
    "rejection_goto": "",
    "sys_class_name": "incident",
    "watch_list": "",
    "time_worked": "",
    "contact_type": "phone",
    "escalation": "0",
    "comments": ""
  }
}
"""

        defaultTicket  = new JsonObject(defaultBaseTicketString)

        //put first ticket into IMDB
        snowImdb << ["4d2": defaultTicket]
    }

    JsonObject getTicket (sysIdkey) {
        println "getTicket: get row id [$sysIdkey] of [${count()}] "
        snowImdb.get (sysIdkey)
    }

    JsonObject getLatestTicket () {

        def ticketArray = snowImdb.collect{it.value}.asList()
        def latestTicket = ticketArray[-1]
        println "getLatestTicket: get Latest ticket IMDB row id [${count()}] "

        ticketArray[-1]
    }

    JsonObject findTicketByNumber (ticketNum) {
        snowImdb.each {key, JsonObject record ->
            Pattern regexp = ~/"number" : "${ticketNum}"/
            if (record.encodePrettily().contains (regexp)) {
                return snowImdb.get (key)
            }
        }
    }

    JsonObject createTicket (JsonObject bodyContent) {
        Map params = bodyContent.mapTo (HashMap)
        println "createIicket post body as map $params"

        JsonObject baseCopy = defaultTicket.copy()  //get copy and then set new values
        //get into slurper
        def slurped = new JsonSlurper().parseText(baseCopy.encode())
        def builder = new JsonBuilder(slurped)

        //public records updates
        builder.content.result.short_description = params.short_description ?: ""
        builder.content.result.description = params.description ?: ""
        builder.content.result.category =  params.category ?: ""
        builder.content.result.comments =  params.comments ?: ""
        builder.content.result.impact =  params.impact ?: ""
        builder.content.result.priority =  params.priority ?: ""
        builder.content.result.urgency =  params.urgency ?: ""
        builder.content.result.severity =  params.severity ?: ""
        builder.content.result.location =  params.location ?: ""
        builder.content.result.company =  params.company ?: ""
        builder.content.result.caller_id =  params.caller_id ?: ""
        builder.content.result.cmdb_ci =  params.cmdb_ci ?: ""
        builder.content.result.comments =  params.comments ?: ""

        //internal simulated 'system' records updated
        //set up incrementing number for each record
        def number = "INC001000${++numberEnding}"
        builder.content.result.number = number

        builder.content.result.sys_updated_on =  LocalDateTime.now().toString()
        builder.content.result.sys_created_on = LocalDateTime.now().toString()
        builder.content.result.opened_at =  LocalDateTime.now().toString()
        def sys_id = Long.toHexString(++startRecordFrom)
        builder.content.result.sys_id =  sys_id

        JsonObject newRecord = new JsonObject(builder.toPrettyString())
        snowImdb.put (sys_id,  newRecord)

        println "adding new record to IMDB row id [$sys_id] of [${count()}] " // as ${builder.toPrettyString()}"

        newRecord

    }

    /* in reality this deosnt appear to be supported officually in the API
     * see https://developer.servicenow.com/app.do#!/rest_api_doc?v=istanbul&id=r_TableAPI-GETid
     *
     * probably need to chunk this in the vertx handler
     */
    JsonArray listTickets () {
        JsonArray results = new JsonArray ()

        snowImdb.each (key, record) {
            results.add(record)
        }


    }

    // current number of records in IMDB
    long count () {
        snowImdb.size()
    }
}

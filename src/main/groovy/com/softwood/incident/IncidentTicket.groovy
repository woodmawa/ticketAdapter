package com.softwood.incident

import com.softwood.utils.UuidUtil
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

import java.time.Instant
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

class IncidentTicket implements PublicTicketTrait {
    final UUID id = UuidUtil.timeBasedUuid

    String closureCode
    boolean majorIncident
    String diagnosticResult
    HelpDeskAgent assignee
    ConcurrentLinkedQueue<WorkNote> workNotes = new ConcurrentLinkedQueue()
    ConcurrentLinkedQueue<MaintainerTicket> maintainerTickets = new ConcurrentLinkedQueue()

    void setResoledDateTime (LocalDateTime time) {
        resolvedDateTime = time
    }

    void setClosedDateTime (LocalDateTime time) {
        closedDateTime = time
    }

    //cant remove work notes - just add to them
    void addWorkNote (text) {
        workNotes << new WorkNote (text:text)
    }

    void assign (HelpDeskAgent agent) {
        assignee = agent
    }

    JsonObject asJson() {
        JsonObject json = new JsonObject()

        Map props = this.properties
        props.each {key, value ->
            if (value instanceof ConcurrentLinkedQueue)
                return
            else if (key == "id")
                return
            else if (value instanceof Class )
                return
            else if (value instanceof LocalDateTime)
                json.put (key, value.toString() )
            else if (value == null )
                return
            else if (key == "relatedCi") {
                JsonArray relCi = new JsonArray()
                value.each {relCi.add (it)}
                json.put (key, relCi)
            } else {
                println "adding $key and value : $value to json"
                json.put (key, value)
            }
        }

        //todo encode other values later
        json
    }
}

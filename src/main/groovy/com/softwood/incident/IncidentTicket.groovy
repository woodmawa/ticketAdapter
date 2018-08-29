package com.softwood.incident

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

class IncidentTicket implements PublicTicketTrait {
    String closureCode
    boolean majorIncident
    String diagnosticResult
    HelpDeskAgent assignee
    ConcurrentLinkedQueue<WorkNote> workNotes = new ConcurrentLinkedQueue()
    ConcurrentLinkedQueue<MaintainerTicket> maintainerTickets = new ConcurrentLinkedQueue()

    @Override
    void setResoledDateTime (LocalDateTime time) {
        resolvedDateTime = time
    }

    @Override
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
}

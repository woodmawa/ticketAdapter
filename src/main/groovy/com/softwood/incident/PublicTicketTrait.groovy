package com.softwood.incident

import com.softwood.cmdb.ConfigurationItem
import com.softwood.cmdb.Customer
import com.softwood.cmdb.Site
import com.softwood.utils.UuidUtil

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

trait PublicTicketTrait {
    final UUID id = UuidUtil.timeBasedUuid
    final String title
    String description
    String status
    String impact
    String severity
    String urgency
    String priority
    String ticketIdentifier
    String clientTicketIdentifier
    final String requester
    String originator
    String category
    Customer customer   //final?
    Optional<Site> site = new Optional()   //final?
    String item //free format string
    Optional<ConfigurationItem> ci = new Optional()
    Optional<ConcurrentLinkedQueue<ConfigurationItem>> relatedCi = new Optional()
    LocalDateTime reportedDateTime
    LocalDateTime resolvedDateTime
    LocalDateTime closedDateTime
    //todo add comments handling to public api

    void SetResolvedDateTime (LocalDateTime time) {
        throw new UnsupportedOperationException("resolvedDateTime is a readonly attribute")
    }

    void SetClosedDateTime (LocalDateTime time) {
        throw new UnsupportedOperationException("closedDateTime is a readonly attribute")
    }

    void SetTicketIdentifier (String  name) {
        throw new UnsupportedOperationException("ticketIdentifer is a readonly attribute")
    }
}

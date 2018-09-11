package com.softwood.incident

import com.softwood.cmdb.ConfigurationItem
import com.softwood.cmdb.Customer
import com.softwood.cmdb.Site
import com.softwood.utils.UuidUtil

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

trait PublicTicketTrait {
    String title
    String description
    String status
    String impact
    String severity
    String urgency
    String priority
    String ticketIdentifier
    String clientTicketIdentifier
    String requester
    String originator
    String category
    String customerName
    String siteName
    String sitePostalCode
    String item //free format string
    String ciName
    String[] relatedCi = [""]
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

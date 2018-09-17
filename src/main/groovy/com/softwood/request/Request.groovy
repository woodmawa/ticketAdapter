package com.softwood.request

import com.softwood.cmdb.Customer

import java.time.LocalDateTime

class Request {
    String requestIdentifier
    Customer customer
    LocalDateTime createdDate
    String contactDetails
    String priorty = "normal"
}

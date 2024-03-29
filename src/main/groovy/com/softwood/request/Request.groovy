package com.softwood.request

import com.softwood.cmdb.Customer

import java.time.LocalDateTime

class Request {
    Long id
    String requestIdentifier  //business string
    String title = ""
    Customer customer
    String status
    LocalDateTime createdDate = LocalDateTime.now()
    LocalDateTime requiredDate
    LocalDateTime authorisedDate
    String contactDetails
    String priority = "normal"
    BillOfMaterials bom = new BillOfMaterials()

    String toString() {
        "Request (requestIdentifier: $requestIdentifier, status: $status)"
    }
}

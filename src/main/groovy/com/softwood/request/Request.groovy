package com.softwood.request

import com.softwood.cmdb.Customer

import java.time.LocalDateTime

class Request {
    String requestIdentifier
    Customer customer
    String status
    LocalDateTime createdDate = LocalDateTime.now()
    LocalDateTime requiredDate
    LocalDateTime authorisedDate
    String contactDetails
    String priorty = "normal"
    BillOfMaterials bom  = new BillOfMaterials()


}

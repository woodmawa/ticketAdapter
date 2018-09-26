package com.softwood.request

import com.softwood.cmdb.Customer
import com.softwood.utils.JsonUtils
import com.softwood.utils.UuidUtil
import io.vertx.core.json.JsonObject

import java.time.LocalDateTime

class Request {
    Long id
    String requestIdentifier  //business string
    Customer customer
    String status
    LocalDateTime createdDate = LocalDateTime.now()
    LocalDateTime requiredDate
    LocalDateTime authorisedDate
    String contactDetails
    String priorty = "normal"
    BillOfMaterials bom  = new BillOfMaterials()

    String toString() {
        "Request (requestIdentifier: $requestIdentifier, status: $status)"
    }
}

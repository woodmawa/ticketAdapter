package com.softwood.request

import com.softwood.cmdb.Customer
import com.softwood.utils.JsonUtils
import com.softwood.utils.UuidUtil
import io.vertx.core.json.JsonObject

import java.time.LocalDateTime

class Request {
    UUID id = UuidUtil.timeBasedUuid
    String requestIdentifier  //business string
    Customer customer
    String status
    LocalDateTime createdDate = LocalDateTime.now()
    LocalDateTime requiredDate
    LocalDateTime authorisedDate
    String contactDetails
    String priorty = "normal"
    BillOfMaterials bom  = new BillOfMaterials()

    JsonObject toJson () {
        JsonUtils.Options options = new JsonUtils.Options()
        assert options != null
        assert options.respondsTo ("excludeNulls")
        options.excludeNulls()
        options.excludeClass(true)
        JsonUtils generator = options.build()
        generator.toJson (this)

    }

    String toString() {
        "Request (requestIdentifier: $requestIdentifier, status: $status)"
    }
}

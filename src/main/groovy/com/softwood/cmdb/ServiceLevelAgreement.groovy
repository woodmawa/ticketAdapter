package com.softwood.cmdb

import com.softwood.utils.UuidUtil

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

class ServiceLevelAgreement {
    UUID id = UuidUtil.getTimeBasedUuid ()
    String name
    Optional<Customer> customer
    LocalDateTime createdDateTime = LocalDateTime.now()

}

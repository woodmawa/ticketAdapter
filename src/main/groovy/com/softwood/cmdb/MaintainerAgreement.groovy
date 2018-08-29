package com.softwood.cmdb

import com.softwood.utils.UuidUtil

import java.time.LocalDateTime

class MaintainerAgreement {
    UUID id = UuidUtil.getTimeBasedUuid ()
    String name
    Optional<Maintainer> maintainer
    LocalDateTime createdDateTime = LocalDateTime.now()
    
}

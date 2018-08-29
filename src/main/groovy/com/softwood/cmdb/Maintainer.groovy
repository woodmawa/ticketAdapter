package com.softwood.cmdb

import com.softwood.utils.UuidUtil
import groovy.transform.MapConstructor

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

//ensure role type is correctly set
@MapConstructor (post = {it.role = RoleType.MAINTAINER})
class Maintainer extends OrgRoleInstance {
    UUID id = UuidUtil.timeBasedUuid
    LocalDateTime createdDateTime = LocalDateTime.now()
    ConcurrentLinkedQueue<MaintainerAgreement> mags = new ConcurrentLinkedQueue()

    void addMag (MaintainerAgreement mag) {
        if (!mags.contains(mag))
            mags << mag
    }

    void removeMag (MaintainerAgreement mag) {
        mags.remove(mag)
    }

}

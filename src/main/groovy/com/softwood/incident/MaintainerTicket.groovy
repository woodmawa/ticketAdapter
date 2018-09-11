package com.softwood.incident

import com.softwood.cmdb.Maintainer
import com.softwood.cmdb.MaintainerAgreement
import com.softwood.utils.UuidUtil

class MaintainerTicket {
    final UUID id = UuidUtil.timeBasedUuid

    String ticketIdentifier
    MaintainerAgreement mag
    Maintainer maintainer
    String title
    String description
    //todo etc
}

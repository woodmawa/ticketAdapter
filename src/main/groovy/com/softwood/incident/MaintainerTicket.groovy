package com.softwood.incident

import com.softwood.cmdb.Maintainer
import com.softwood.cmdb.MaintainerAgreement

class MaintainerTicket {
    String ticketIdentifier
    MaintainerAgreement mag
    Maintainer maintainer
    String title
    String description
    //todo etc
}

/*
 * Copyright 2018 author : Will woodman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.softwood.incident

import com.softwood.cmdb.ConfigurationItem
import com.softwood.cmdb.Customer
import com.softwood.cmdb.Site
import com.softwood.utils.UuidUtil

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

trait PublicTicketTrait {
    String title
    String description
    String status
    String impact
    String severity
    String urgency
    String priority
    String ticketIdentifier
    String clientTicketIdentifier
    String requester
    String originator
    String category
    String customerName
    String siteName
    String sitePostalCode
    String item //free format string
    String ciName
    String cmdb_ci
    String[] relatedCi = [""]
    LocalDateTime reportedDateTime
    LocalDateTime resolvedDateTime
    LocalDateTime closedDateTime
    //todo add comments handling to public api

    void SetResolvedDateTime (LocalDateTime time) {
        throw new UnsupportedOperationException("resolvedDateTime is a readonly attribute")
    }

    void SetClosedDateTime (LocalDateTime time) {
        throw new UnsupportedOperationException("closedDateTime is a readonly attribute")
    }

    void SetTicketIdentifier (String  name) {
        throw new UnsupportedOperationException("ticketIdentifer is a readonly attribute")
    }
}

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

import com.softwood.utils.UuidUtil
import groovy.json.JsonGenerator
import io.vertx.core.json.JsonObject

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentLinkedQueue

class IncidentTicket implements PublicTicketTrait {
    final UUID id = UuidUtil.timeBasedUuid

    String closureCode
    boolean majorIncident
    String diagnosticResult
    HelpDeskAgent assignee
    ConcurrentLinkedQueue<WorkNote> workNotes = new ConcurrentLinkedQueue()
    ConcurrentLinkedQueue<MaintainerTicket> maintainerTickets = new ConcurrentLinkedQueue()

    void setResoledDateTime(LocalDateTime time) {
        resolvedDateTime = time
    }

    void setClosedDateTime(LocalDateTime time) {
        closedDateTime = time
    }

    //cant remove work notes - just add to them
    void addWorkNote(text) {
        workNotes << new WorkNote(text: text)
    }

    void assign(HelpDeskAgent agent) {
        assignee = agent
    }

    /**
     * better cleaner implementation using groovy's JsonGenerator to control the format
     * @return Alarm as JsonObject
     */
    JsonObject toJson() {
        def generator = new JsonGenerator.Options()
                .excludeNulls()
                .excludeFieldsByType(Class)
                .excludeFieldsByType(Closure)
                .addConverter(ConcurrentLinkedQueue) { ConcurrentLinkedQueue queue, String key -> queue.toArray() }
                .addConverter(LocalDateTime) { LocalDateTime t, String key -> t.toString() }
                .addConverter(UUID) { UUID uuid, String key -> uuid.toString() }
                .build()

        String result = generator.toJson(this)
        new JsonObject(result)

    }


}

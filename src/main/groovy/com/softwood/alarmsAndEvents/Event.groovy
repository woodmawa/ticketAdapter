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
package com.softwood.alarmsAndEvents

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class Event implements Serializable {
    int id
    String name
    String type
    def ciReference
    LocalDateTime createdDate = LocalDateTime.now()
    Map eventCharacteristics = new ConcurrentHashMap()
}

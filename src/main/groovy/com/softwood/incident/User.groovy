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

import java.util.concurrent.ConcurrentLinkedQueue

abstract class User {

    static ConcurrentLinkedQueue userList = new ConcurrentLinkedQueue()

    static User findByEmail (String email) {
        userList.find {it.email == email}
    }

    User () {
        userList << this
    }

    UUID id = UuidUtil.timeBasedUuid

    String firstName
    String lastName
    String systemName  //should really be unique - not assuring this here
    String email

    String getName () {
        firstName+lastName
    }

    //if presented a compound name
    String setName (String nameString) {
        def names = nameString.split(", ")
        firstName = names[0]
        lastName = names[1]
    }
}

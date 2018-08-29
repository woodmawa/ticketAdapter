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

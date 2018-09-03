package com.softwood.alarmEvent

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class Event implements Serializable{
    int id
    String name
    String type
    def ciReference
    LocalDateTime createdDate = LocalDateTime.now()
    Map eventCharacteristics = new ConcurrentHashMap()
}

package com.softwood.alarmEvent

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class Event implements Serializable{
    def id
    String name
    String type
    def objId
    LocalDateTime createdDate = LocalDateTime.now()
    Map eventCharacteristics = new ConcurrentHashMap()
}

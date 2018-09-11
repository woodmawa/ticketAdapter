package com.softwood.cmdb

import java.util.concurrent.ConcurrentHashMap

/**
 * general ci relationship
 */
class Relationship {
    String name
    def fromCi
    def toCi

    String toString () {
        "Relationship (name:$name, fromCi: $fromCi, toCi: $toCi)"
    }
}

package com.softwood.request

class ProductOffering {
    UUID id = UUID.randomUUID()
    String name
    String type
    String hierarchy

    String toString () {
        "ProductOffering (name:$name, type:$type, [id:$id]"
    }
}

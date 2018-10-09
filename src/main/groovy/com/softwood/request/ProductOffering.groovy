package com.softwood.request

import com.softwood.utils.XXUuidUtil

class ProductOffering {
    UUID id = XXUuidUtil.timeBasedUuid
    String name
    String type
    String hierarchy

    String toString () {
        "ProductOffering (name:$name, type:$type, [id:$id]"
    }
}

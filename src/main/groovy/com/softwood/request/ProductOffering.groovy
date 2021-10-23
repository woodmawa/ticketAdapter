package com.softwood.request

import com.softwood.utils.UuidUtil

class ProductOffering {
    UUID id = UuidUtil.timeBasedUuid
    String name
    String type
    String hierarchy

    String toString() {
        "ProductOffering (name:$name, type:$type, [id:$id]"
    }
}

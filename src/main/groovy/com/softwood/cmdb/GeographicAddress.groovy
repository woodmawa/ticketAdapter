package com.softwood.cmdb

/**
 * not used in model yet - tmf aligned
 *
 */
class GeographicAddress {
    String id
    String streetNbr
    String streetName
    String streetType
    String postCode
    String locality
    String city
    String stateOrProvince
    String country // should be an object ref

}

class GeographicLocation {
    String id
    String name
    String geometryType
    String accuracy
    Point Geometry
    String spatialReference

}

class Point {
    String x
    String y
    String z
}

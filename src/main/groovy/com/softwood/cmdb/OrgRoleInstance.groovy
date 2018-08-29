package com.softwood.cmdb


enum RoleType {
    CUSTOMER, SUPPLIER, MAINTAINER, MANUFACTURER
}
abstract class OrgRoleInstance {
    String name
    RoleType role
}

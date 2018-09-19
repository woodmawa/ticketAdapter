package com.softwood.request

import com.softwood.cmdb.ConfigurationItem

import java.time.LocalDateTime

class LineItem {
    int lineNumber
    ProductOffering portfolioProduct
    ConfigurationItem productInstance       //optional if provide
    int quantity
    String status
    LocalDateTime shippingDate

}

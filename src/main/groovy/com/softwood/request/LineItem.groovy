package com.softwood.request

import com.softwood.cmdb.ConfigurationItem

import java.time.LocalDateTime

class LineItem {
    int lineNumber
    ProductOffering offering
    ConfigurationItem productInstance       //optional if provide
    int quantity
    String status
    LocalDateTime shippingDate

    void addProductOffering(ProductOffering offering, ConfigurationItem productInstance) {
        assert offering.name == productInstance.offering.name
        this.offering = offering
        this.productInstance = productInstance
    }

    void addNonAssetingProductOffering(ProductOffering offering) {
        this.offering = offering
    }
}

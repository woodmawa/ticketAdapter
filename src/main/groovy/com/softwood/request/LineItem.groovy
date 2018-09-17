package com.softwood.request

import com.softwood.cmdb.ConfigurationItem

class LineItem {
    int lineNumber
    ProductOffering portfolioProduct
    ConfigurationItem productInstance       //optional if provide
    int quantity
}

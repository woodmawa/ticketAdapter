package com.softwood.request

import com.softwood.cmdb.ConfigurationItem
import com.softwood.cmdb.Site

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class BillOfMaterials {
    //site is expected index into the map,  with array of lineItems as value type
    LinkedHashMap basket = new ConcurrentHashMap<Site, ConcurrentLinkedQueue<LineItem> >()

    void clear () {
        basket.clear()
    }

    def addSite (Site site) {
        assert site
        if (basket.containsKey(site))
            throw new UnsupportedOperationException ("site key '$site', already exists in this basket")
        basket.put (site, new ConcurrentLinkedQueue<>() )
    }

    void addToBasket (Collection<LineItem> siteLines, ProductOffering po) {

    }

    void addToBasket (Collection<LineItem> siteLines, ConfigurationItem ci) {

    }

    Collection getSiteItemsList (Site site) {
        if (basket.containsKey(site)) {
            basket.get(site)

        }
    }
}

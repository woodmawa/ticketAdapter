package com.softwood.request

import com.softwood.cmdb.ConfigurationItem
import com.softwood.cmdb.Site

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class BillOfMaterials {
    //site is expected index into the map,  with array of lineItems as value type
    private LinkedHashMap basket = new ConcurrentHashMap<Site, ConcurrentLinkedQueue<LineItem> >()
    private int lineNumberGenerator = 0
    private Request request

    void clear () {
        basket.clear()
        lineNumberGenerator = 0
    }

    def addSite (Site site) {
        assert site
        if (basket.containsKey(site))
            throw new UnsupportedOperationException ("site key '$site', already exists in this basket")
        basket.put (site, new ConcurrentLinkedQueue<LineItem>() )
    }

    void addToBasket (Site site, ProductOffering po, quantity=1) {
        LineItem line = new LineItem (offering:po)
        line.status = "preOrder"
        line.lineNumber = ++lineNumberGenerator
        line.quantity = quantity
        Queue lines = basket[site]
        lines << line
    }

    void addToBasket (Site, ConfigurationItem ci) {
        LineItem line = new LineItem (offering:ci.offering, productInstance: ci)
        line.status = "preOrder"
        line.lineNumber = ++lineNumberGenerator
        line.quantity = 1
        Queue lines = basket[site]
        lines << line
    }

    //return Collection of lines in this basket for site
    Collection getSiteItemsList (Site site) {
        if (basket.containsKey(site)) {
            basket.get(site)

        }
    }

    Collection getAllItemsList () {
        def allLines = []
        basket.each { key, value ->
            allLines << value.toList()
        }

        allLines
    }

    /**
     * each entry starts with site, then itemLines
     * @return
     */
    Collection getAllItemsBySiteSortedList () {
        def allLines = []
        basket.each {key, value ->

            allLines << site
            allLines << value.toList()
            allLines.sort  {a,b -> a[0] <=> b[0]}
        }
        allLines.collect {it[1]}
    }

    String toString () {
        if (request) {
            "BoM for request id: $request.id"
        }
        else
            "anonymous request BoM"
    }
}

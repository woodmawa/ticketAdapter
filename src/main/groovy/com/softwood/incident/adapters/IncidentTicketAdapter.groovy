package com.softwood.incident.adapters

import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpRequest

/**
 * public Api for a ticket adapter
 */
interface IncidentTicketAdapter{
//todo - work out basic standard methods

    void configureHttpClient()

    String getName()
    void setName (String name)

    HttpRequest apiGet (String uri)
    HttpRequest apiPost (String uri, String bodyString)
    void apiSend (HttpRequest<Buffer> request, Closure handler)

    HttpRequest<Buffer> apiAddQueryparams (String uri, queryParams )


    boolean hasError ()
    void clearError ()


}

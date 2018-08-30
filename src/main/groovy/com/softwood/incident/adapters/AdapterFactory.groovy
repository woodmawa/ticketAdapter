package com.softwood.incident.adapters

class AdapterFactory {

    static IncidentSystemAdapter newAdapter () {
        //todo write the factory method to return the write
        //instance based on config
    }
}

//todo - work out basic standard methods
interface IncidentSystemAdapter {
    String name
    def send (message )

}

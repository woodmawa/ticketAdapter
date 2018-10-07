package com.softwood.request.requestApi

def f = new File('./createRequestTemplate')

def refData = [
        title : "autoGen title",
        status  : "Open",
        priority  : "High",
        requestIdentifier     : 'next new request',
        customer: [name: "HSBC", id : "95939b2d-c977-11e8-b3e1-052d9ee4fef0"],
        //bom : '''{"myBom" :"stuff "}''',
        //counter : 10
]



def engine = new groovy.text.GStringTemplateEngine()
def postTemplate = engine.createTemplate(f).make(refData)
println postTemplate.toString()
println "size : " + postTemplate.toString().size()
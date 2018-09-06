package com.softwood.application.bootstrap

/**
 *   for all files in directory run the scripts and
 *   auto configure in memory model from config files
 *
 *   psuedo code like
 *   for each config file in this directory
 *   read sequence order and setup the files in correct order
 *   set up async dataflow tasks to excecute each and chain
 *   dependent tasks on the predessor - may be able to do this in gradle DAG later
 *
 *   structure of tasks
 *   [seq #, [scriptA, scriptB...], status]
 *
 **/

import groovy.io.FileType

def list = []

// get list of *.conf files in run sorted order and run each one
def dir = new File(".")  //start from current directory
dir.eachFileRecurse (FileType.FILES) { file ->

    def fn = file.name
    def ext = fn.substring (fn.lastIndexOf (".") + 1)  //get file extension
    def runOrderText
    def runScriptOrder
    String line
    if (ext =~ /^mconf$/ ) {
        def lines = file.readLines()
        def result = lines.findResult {
             /*Pattern pat = ~"""
                    (?ix)               # case insensitive, extended format
                    \\s+                # some whitespace
                    def                 # match on def key word
                    \\s+                # some whitespace
                    runConfigInOrder    #variable we want 
                    \\s+                # some whitespace
                    =                   # assignment 
                    \\s+                # some whitespace
                    (d+)                # some digits in group to make productAttributesAndAssignment number
                     """*/
            def pattern = ~/^\s*+def\s*runConfigInOrder\s*=\s*(\d+)/
            def m = (it =~ pattern)
            if (m.matches()) { it}
        }

        line = result ?: ""

        if (line) {
            runOrderText = line.substring (line.lastIndexOf("=") + 1)
            runScriptOrder = runOrderText.toInteger()
            binding.$runScriptOrder << [runScriptOrder, file]
        }
    }
}


def runInSequence = $runScriptOrder.sort {a,b ->
    a[0] <=> b[0]}

//run in sequence order synchronously at the mo
//expact the conf files to add to vfPortfolio expando
println " bootstrap : run configs in order "
runInSequence.each {

    println " --- running : ${it[1].name} [runOrder : ${it[0]}]..."
    evaluate(it[1])
}

runScriptOrder = runInSequence //store as sorted list in the binding


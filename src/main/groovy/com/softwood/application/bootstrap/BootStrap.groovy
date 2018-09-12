/*
 * Copyright 2018 author : Will woodman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.softwood.application.bootstrap

import com.softwood.application.Application
import groovy.io.FileType

import java.util.concurrent.ConcurrentLinkedQueue

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

class BootStrap {

    def runOrderText
    def runScriptOrder
    GroovyShell shell
    Binding binding

    BootStrap (binding) {
        this.binding = binding
    }


    def init() {

        def list = []
        binding.$runScriptOrder = new ConcurrentLinkedQueue<>()

        // get list of *.conf files in run sorted order and run each one
        def dir = new File(".")  //start from current directory
        dir.eachFileRecurse (FileType.FILES) { file ->

            def fn = file.name
            def ext = fn.substring (fn.lastIndexOf (".") + 1)  //get file extension
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

        shell = new GroovyShell (binding)
        def runInSequence = binding.$runScriptOrder.sort {a,b ->
            a[0] <=> b[0]}


        //run in sequence order synchronously at the mo
        //expact the conf files to add to vfPortfolio expando
        println " bootstrap : run configs in order "
        runInSequence.each {

            println " --- running : ${it[1].name} [runOrder : ${it[0]}]..."
            shell.evaluate(it[1])
        }


    }

    def destroy() {
    }
}

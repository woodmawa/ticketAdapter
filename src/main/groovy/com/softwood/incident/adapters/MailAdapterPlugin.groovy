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
package com.softwood.incident.adapters

import com.softwood.application.Application
import com.softwood.application.ConfigurableProjectApplication
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.Email
import org.apache.commons.mail.SimpleEmail
import groovy.text.*


class MailAdapterPlugin /*implements IncidentSystemAdapter*/ {
    String name
    Email email

    def mailConf = Application.application.binding.config.ticketAdapter.mail

    //factory interface realisation
    def send (message) {
        sendMail ("new incident", message, "will.woodman@outlook.com")
    }

    Closure defaultConf = {

        def userName = System.getProperty("mailUserName").toString()
        def password = System.getProperty ("mailPassword").toString()
        if (userName == null || password == null ) {
            throw ExceptionInInitializerError.newInstance("mail userName and mail password must be set in the environment ")
        }

        email.setHostName(mailConf.'server')
        email.setSmtpPort( mailConf.'port')
        //todo need to hide this - read from env variables of something
        email.setAuthenticator(new DefaultAuthenticator(userName, password))
        email.setSSLOnConnect(mailConf.'sslEnabled')
        email.setFrom(mailConf.'from')
        email.setSubject(mailConf.'defaultSubject')
    }

    MailAdapterPlugin (Closure config = null) {

        email = new SimpleEmail()

        Closure configuration = (config ?: defaultConf).clone()
        configuration.delegate = this

        configure (configuration)
    }


    private def configure (config) {
        //invoke methodClosure
        config()
    }

    /**
     * if you get errors on like this
     * sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
     * then it means the mail servers certificate chain is not complete.  to fix this run the
     * \scripts\SaveRemoteServerCerts script after editing for your mail server/port, this will
     * add the required certs to your local jssecacerts and the sample send code should then run
     * @param subject
     * @param message
     * @param recipients
     * @return
     */
    def sendMail (String subject, String message, recipients) {
        if (!subject)
            email.setSubject("groovy TestMail")
        else
            email.setSubject(subject)

        String[] recipientsList
        if (recipients instanceof String)
            recipientsList = [recipients]
        else
            recipientsList = recipients

        email.setMsg("incident details :-)\n $message")
        if (recipientsList) {
            for (recipient in recipientsList)
                email.addTo(recipient)
        } else
            email.addTo("will.woodman@outlook.com")

        println "sending test message "
        email.send()  //todo make this async later
        email.toList = [] //clear recipients list for next message
    }

    String generateFormattedMailBody (incidentData, formatTemplate=null) {

        // SimpleTemplateEngine.
        def simple = new SimpleTemplateEngine()

        //have to use single quotes as we dont want string interpolation done here  -
        // its done through the template engine
        // todo would be better to pick up default tamplate through env variables config
        def incidentTemplate  = (formatTemplate ?: '''
Default template: Action: create ticket 
[customer]:       $customer
[site]:             $site
[title] :           $title
[description] :   $description
[item]:             $item
[requester] :     $requester
[priority] :        $priority
[severity] :        $severity
[urgency] :         $urgency
[reported] :        $now
''')
        //todo could get all the objects properites and build at runtime - rather than user
        //having to provide a map
        def binding = new HashMap(incidentData)
        binding << [now: Date.newInstance(), name: 'Will Woodman']
        def output = simple.createTemplate(incidentTemplate).make(binding).toString()
    }
}

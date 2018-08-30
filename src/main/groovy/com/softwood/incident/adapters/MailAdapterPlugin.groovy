package com.softwood.incident.adapters

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.Email
import org.apache.commons.mail.SimpleEmail
import groovy.text.*


class MailAdapterPlugin implements IncidentSystemAdapter {
    String name
    Email email

    //factory interface realisation
    def send (message) {
        sendMail ("new incident", message, "will.woodman@outlook.com")
    }

    Closure defaultConf = {
        email.setHostName("mail.btinternet.com")
        email.setSmtpPort( 465)
        email.setAuthenticator(new DefaultAuthenticator("will.woodman", "polomint1xex49is"));
        email.setSSLOnConnect(true)
        email.setFrom("will.woodman@btinternet.com")
        email.setSubject("groovy TestMail")
    }

    MailAdapterPlugin (Closure config = null) {

        email = new SimpleEmail()

        Closure configuration = (config ?: defaultConf).clone()
        configuration.delegate = this

        configure (configuration)
    }


    private def configure (config) {
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

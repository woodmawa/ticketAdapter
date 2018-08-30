package com.softwood.scripts

/*
 * Copyright 2006 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 *
 * Author Will Woodman 30-08-2018
 * Originally from:
 * http://blogs.sun.com/andreas/resource/InstallCert.java
 * Use:
 * converted to groovy script form.  offered as a global function in the script binding
 * takes a [host}:[port], the certificate store password to use and will output the
 * newly  generated jssecacerts, by default this will be written to env:java.home/lib/security
 * if a jssecacerts exists java will use this instead of the cacerts as delivered file
 * Example:
 *% enableRemoteServerCerts  mail.btinternet.com:465 [default with pwd 'changeit']
 */

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Class used to add the server's certificate to the KeyStore
 * with your trusted certificates.
 */

//use function to grab any remote server certificates e.g. from a mail server and
//add these to the local jssecacerts file
enableRemoteServerCerts ("mail.btinternet.com:465", "changeit")

def enableRemoteServerCerts (hostAndPort, certStorePwd="changeit") {
    String host
    int port
    char[] passphrase;
    String[] c = hostAndPort.split(":")
    host = c[0]
    port = (c.length == 1) ? 443 : Integer.parseInt(c[1])
    passphrase = certStorePwd


    KeyStore ks = ManageKeystore.loadKeystore()
    assert ks

    X509Certificate[] chain = AssessCertificateChain.trySLSHandshake (ks, host, port)

    System.out.println("\nServer sent " + chain.length + " certificate(s):\n");
    MessageDigest sha1 = MessageDigest.getInstance("SHA1");
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    for (int i = 0; i < chain.length; i++) {
        X509Certificate cert = chain[i];
        System.out.println (" " + (i + 1) + " Subject " + cert.getSubjectDN())
        System.out.println("   Issuer  " + cert.getIssuerDN())
        sha1.update(cert.getEncoded())
        System.out.println("   sha1    " + HexUtil.toHexString(sha1.digest()))
        md5.update(cert.getEncoded())
        System.out.println("   md5     " + HexUtil.toHexString(md5.digest()))
        System.out.println()
    }

    println "Enter certificate to add to trusted keystore, or 'a' for all,  or 'q' to quit: [1]"
    String line = System.in.newReader().readLine()
    line.trim()
    if (line.toLowerCase() == "q") {
        println("KeyStore not changed")
        return
    } else if (line.toLowerCase() == "a") {
        //write all certs to jssecacerts
        ManageKeystore.addRemoteCertificatesToKeyStore (ks, host, chain)
    } else {

        int lineNum = Integer.parseInt(line)
        if (![1..chain.length].contains(lineNum)) {
            println("KeyStore not changed")
            return
        }

        //write single selected cert to jssecacerts
        ManageKeystore.addSingleCertificateToKeyStore(ks, "$host-${lineNum}", chain[lineNum-1])
    }

    return


}

class AssessCertificateChain {
/**
 * get a secure socket to remote host
 * @param host
 * @param port
 * @return
 */
    static X509Certificate[] trySLSHandshake (KeyStore ks, String host, int port) {
        SSLContext context = SSLContext.getInstance("TLS")
        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(ks)
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0]
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager)
        TrustManager[] tmList = [tm] as TrustManager[]
        context.init(null, tmList, null)
        SSLSocketFactory factory = context.getSocketFactory()
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setSoTimeout(10000);

        try {
            println("Starting SSL handshake...")
            socket.startHandshake()
            socket.close()
            println("No errors, certificate is already trusted");
        } catch (SSLException e) {
            e.printStackTrace(System.out)
        }

        X509Certificate[] chain = tm.chain
        if (chain == null) {
            System.out.println("Could not obtain server certificate chain")
            return
        }
        chain

    }


}

/**
 * support classes
 * @param bytes
 * @return
 */

class ManageKeystore {

    static String passphrase = "changeit"

    static KeyStore loadKeystore (String pwd=null) {
        File stdCertsFile, overrideCertsFile, file
        char SEP = File.separatorChar

        def passphrase = pwd ?: "changeit"

        String secDir = System.getProperty("java.home") +SEP + "lib" + SEP + "security"
        overrideCertsFile = new File (secDir, "jssecacerts")
        stdCertsFile = new File (secDir, "cacerts")

        file = overrideCertsFile.isFile() ?: stdCertsFile
        assert file.exists()

        println("Loading KeyStore $file ...")
        InputStream inpStream = new FileInputStream(file)
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType())
        ks.load(inpStream, passphrase.toCharArray())
        inpStream.close()
        ks
    }

    static def addRemoteCertificatesToKeyStore (KeyStore ks, String remoteHost, X509Certificate[] chain) {
        X509Certificate cert
        String alias
        int row = 0
        //iterate
        for (c in chain ){
            cert = c
            alias = remoteHost + "-" + (++row)
            println "> adding cert on row $row with alias $alias "
            addSingleCertificateToKeyStore (ks, alias, cert)
        }

    }

    /**
     * as per preferred JSSE model we write the override certs to jssecacerts.
     * as we loaded cacerts in memory and then added new entries, the composite
     * is now written to jssecacerts.  If both files are present java security will use
     * the jssecacerts as master and ignore the as shipped cacerts file
     * @param ks
     * @param hostAlias
     * @param cert
     * @return
     */
    static def addSingleCertificateToKeyStore (KeyStore ks, String hostAlias, X509Certificate cert) {
        ks.setCertificateEntry(hostAlias, cert)

        char SEP = File.separatorChar

        String secDir = System.getProperty("java.home") +SEP + "lib" + SEP + "security"
        File file = new File (secDir,"jssecacerts" )

        OutputStream out = new FileOutputStream(file) as OutputStream
        ks.store(out, passphrase.toCharArray())
        out.close()

        System.out.println(cert)
        System.out.println()
        System.out.println ("Added certificate to keystore 'jssecacerts' using alias '$hostAlias'")


    }
}


class HexUtil {
    static char[] HEXDIGITS = "0123456789abcdef".toCharArray()

    static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3)
        for (int b : bytes) {
            b &= 0xff
            sb.append(HEXDIGITS[b >> 4])
            sb.append(HEXDIGITS[b & 15])
            sb.append(' ')
        }
        return sb.toString()
    }
}

class SavingTrustManager implements X509TrustManager {

    private final X509TrustManager tm
    private X509Certificate[] chain

    SavingTrustManager(X509TrustManager tm) {
        this.tm = tm
    }


    public X509Certificate[] getAcceptedIssuers() {

        /**
         * This change has been done due to the following resolution advised for Java 1.7+
         http://infposs.blogspot.kr/2013/06/installcert-and-java-7.html
         **/

        return new X509Certificate[0]
        //throw new UnsupportedOperationException();
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        throw new UnsupportedOperationException()
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        this.chain = chain;
        tm.checkServerTrusted(chain, authType)
    }
}
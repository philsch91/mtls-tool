package com.schunker.mtls.net;

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import java.security.cert.Certificate;

import java.io.IOException;

public class ClientHttpsURLConnection extends HttpsURLConnection {

    /* HttpsURLConnection */

    protected ClientHttpsURLConnection(URL url) {
        super(url);
        //TODO Auto-generated constructor stub
    }

    public boolean getConnected() {
        return this.connected;
    }

    @Override
    public String getCipherSuite() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Certificate[] getLocalCertificates() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
        // TODO Auto-generated method stub
        return null;
    }

    /* HttpURLConnection */

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean usingProxy() {
        // TODO Auto-generated method stub
        return false;
    }

    /* URLConnection */

    @Override
    public void connect() throws IOException {
        // TODO Auto-generated method stub
    }
    
}

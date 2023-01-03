package com.schunker.mtls;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import java.lang.reflect.Field;

import com.schunker.java.*;


public class App {
    public static void main(String[] args) {
        App app = new App();
        app.init();
    }

    public void init() {
        Menu menu = new Menu();
        menu.setTitle("mTLS Tool");
        menu.insert("Test mTLS connection");
        menu.insert("Edit Java keystore");
        int inputOption = menu.show();

        if (inputOption == 1) {
            this.testConnection();
        } else if (inputOption == 2) {
            this.editKeystore();
        }
        
    }

    private void testConnection() {
        TextBox urlTextBox = new TextBox("URL", TextBoxInputType.TEXT);
        String urlString = urlTextBox.show();
        if (!urlString.startsWith("https://")) {
            System.out.println("URL must start with 'https://'");
            return;
        }
        System.out.println("URL: " + urlString);

        TextBox keystorePathTextBox = new TextBox("Keystore path", TextBoxInputType.TEXT);
        String keystorePath = keystorePathTextBox.show();
        System.out.println("Keystore path: " + keystorePath);

        TextBox keystorePasswordTextBox = new TextBox("Keystore password", TextBoxInputType.TEXT);
        String keystorePassword = keystorePasswordTextBox.show();
        System.out.println("Keystore password: " + keystorePassword);

        SSLTrustManagerHelper trustManagerHelper = new SSLTrustManagerHelper(keystorePath, keystorePassword);
        SSLContext sslContext = null;

        try {
            sslContext = trustManagerHelper.getSSLContext();
        } catch (Exception ex) {
            System.err.println(ex);
            System.exit(1);
        }

        URL url = null;
        
        try {
            url = new URL(urlString);
        } catch (MalformedURLException ex) {
            System.err.println("Malformed URL: " + urlString);
            System.exit(2);
        }
        
        Proxy proxy = null;

        if (System.getenv("HTTPS_PROXY") != null && System.getenv("PROXY_PORT") != null) {
            String proxyHostname = System.getenv("HTTPS_PROXY");
            int proxyPort = Integer.parseInt(System.getenv("PROXY_PORT"));
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostname, proxyPort));
        } else {
            proxy = Proxy.NO_PROXY;
        }

        HttpsURLConnection connection = null;

        try {
            connection = (HttpsURLConnection) url.openConnection(proxy);
        } catch (Exception ex) {
            System.err.println("Exception for opening connection");
            System.exit(3);
        }
        
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setConnectTimeout(10 * 1000);
        this.setDefaultRequestParameters(connection);

        int responseCode = 0;

        try {
            responseCode = connection.getResponseCode();    
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(4);
        }
        
        System.out.println("Response code: " + Integer.toString(responseCode));

        Field connectedField = null;
        boolean isConnectionConnected = false;

        try {
            connectedField = connection.getClass().getField("connected");
            connectedField.setAccessible(true);
            isConnectionConnected = connectedField.getBoolean(connection);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(5);
        }

        String content = null;

        if (!isConnectionConnected) {
            try {
                content = connection.getContent().toString();    
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(6);
            }
        }
        System.out.println("Content: " + content);
    }

    private void editKeystore() {
        System.out.println("Option not yet available");
    }
    
    private void setDefaultRequestParameters(HttpURLConnection connection) {
        System.out.println("setDefaultRequestProperties");
		// The no-store request directive allows a client to request that caches
		// refrain from storing the request and corresponding response
		// even if the origin server's response could be stored.
		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control#no-store_2
		connection.setRequestProperty("Cache-Control", "no-store");
		return;
    }
}

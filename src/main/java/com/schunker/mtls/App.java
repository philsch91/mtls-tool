package com.schunker.mtls;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.net.URLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;

import java.io.IOException;

import java.lang.reflect.Field;

import com.schunker.java.*;
import com.schunker.mtls.net.HttpsURLConnectionWrapper;

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
        System.out.println("Keystore path: [" + keystorePath + "]");

        TextBox keystorePasswordTextBox = new TextBox("Keystore password", TextBoxInputType.TEXT);
        String keystorePassword = keystorePasswordTextBox.show();
        System.out.println("Keystore password: [" + keystorePassword + "]");

        TextBox truststorePathTextBox = new TextBox("Truststore path", TextBoxInputType.TEXT);
        String truststorePath = truststorePathTextBox.show();
        System.out.println("Truststore path: [" + truststorePath + "]");

        TextBox truststorePasswordTextBox = new TextBox("Truststore password", TextBoxInputType.TEXT);
        String truststorePassword = truststorePasswordTextBox.show();
        System.out.println("Truststore password: [" + truststorePassword + "]");

        SSLTrustManagerHelper trustManagerHelper = new SSLTrustManagerHelper(keystorePath, keystorePassword);

        if (!truststorePath.isEmpty()) {
            trustManagerHelper.setTrustStorePath(truststorePath);
        }
        if (!truststorePassword.isEmpty()) {
            trustManagerHelper.setTrustStorePassword(truststorePassword);
        }

        SSLContext sslContext = null;

        try {
            sslContext = trustManagerHelper.getSSLContext();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        URL url = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException ex) {
            System.err.println("Malformed URL: " + urlString);
            System.err.println(ex.getMessage());
            ex.printStackTrace();
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

        //HttpsURLConnection urlConnection = null;
        HttpsURLConnectionWrapper urlConnectionWrapper = null;

        try {
            //urlConnection = (HttpsURLConnection) url.openConnection(proxy);
            //urlConnection.setRequestMethod("GET");
            urlConnectionWrapper = new HttpsURLConnectionWrapper(url, proxy);
            urlConnectionWrapper.urlConnection.setRequestMethod("GET");
        } catch (Exception ex) {
            System.err.println("Exception for opening (creating) connection");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(3);
        }

        urlConnectionWrapper.urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
        urlConnectionWrapper.urlConnection.setConnectTimeout(10 * 1000);
        urlConnectionWrapper.setDefaultRequestParameters();

        /*
        List<Field> fields = ReflectionHelper.getDeclaredFields(urlConnection.getClass());
        for (Field field : fields) {
            System.out.println("URLConnection field: " + field.toString());
        } */

        //boolean isConnected = this.isConnectionConnected(urlConnection);
        //System.out.println("HttpsURLConnection.connected: " + isConnected);

        try {
            urlConnectionWrapper.urlConnection.connect();
        } catch (SSLHandshakeException sslex) {
            System.err.println("Exception for connecting via mTLS");
            System.err.println(sslex.getMessage());
            sslex.printStackTrace();
            System.exit(4);
        } catch (Exception ex) {
            System.err.println("Exception for connecting connection");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(4);
        }

        //isConnected = this.isConnectionConnected(urlConnection);
        //System.out.println("HttpsURLConnection.connected: " + isConnected);

        int responseCode = 0;

        try {
            responseCode = urlConnectionWrapper.urlConnection.getResponseCode();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(5);
        }

        System.out.println("Response code: " + Integer.toString(responseCode));

        String content = null;

        try {
            //content = urlConnectionWrapper.urlConnection.getContent().toString();
            content = urlConnectionWrapper.getResponseContent();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(6);
        }

        System.out.println("Content: " + content);

        /*
        String responseMessage = null;

        try {
            responseMessage = urlConnection.getResponseMessage();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(7);
        }

        System.out.println("Response message: " + responseMessage);
        */

        String responseBody = urlConnectionWrapper.getResponseBody();
        System.out.println("Response body: " + responseBody);
    }

    private void editKeystore() {
        System.out.println("Option not yet available");
    }

    private boolean isConnectionConnected(URLConnection urlConnection) {
        System.out.println("isConnectionConnected");
        Field connectedField = null;
        boolean isConnected = false;

        try {
            //connectedField = urlConnection.getClass().getDeclaredField("connected");
            connectedField = ReflectionHelper.getDeclaredField(urlConnection.getClass(), "connected");
            connectedField.setAccessible(true);
            isConnected = connectedField.getBoolean(urlConnection);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Exception message: " + ex.getMessage());
            //System.exit(1);
        }
        return isConnected;
    }
}

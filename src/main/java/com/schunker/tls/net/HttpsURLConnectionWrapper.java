package com.schunker.tls.net;

import java.net.URL;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpsURLConnectionWrapper {

    public HttpsURLConnection urlConnection;

    public HttpsURLConnectionWrapper(URL url) throws IOException {
        this(url, Proxy.NO_PROXY);
    }

    public HttpsURLConnectionWrapper(URL url, Proxy proxy) throws IOException {
        this.urlConnection = (HttpsURLConnection) url.openConnection(proxy);
    }

    public void setDefaultRequestParameters() {
        this.setDefaultRequestParameters(this.urlConnection);
    }

    public void setDefaultRequestParameters(HttpURLConnection connection) {
        System.out.println("setDefaultRequestProperties");
		// The no-store request directive allows a client to request that caches
		// refrain from storing the request and corresponding response
		// even if the origin server's response could be stored.
		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control#no-store_2
		connection.setRequestProperty("Cache-Control", "no-store");
		return;
    }

    public boolean isConnected() {
        return this.isConnected(this.urlConnection);
    }

    public boolean isConnected(URLConnection connection) {
        try {
            connection.setDoOutput(connection.getDoInput());    // throws IllegalStateException if connected
            return false;
        } catch (IllegalStateException ex) {
            return true;
        }
    }

    public String getResponseContent() throws IOException {
        int responseCode = this.urlConnection.getResponseCode();
        if (responseCode >= 400) {
            return null;
        }
        return this.urlConnection.getContent().toString();
    }

    public String getResponseBody() {
        BufferedReader reader = null;
        StringBuffer buffer = null;
        String responseBody = "";

        try {
            int responseCode = this.urlConnection.getResponseCode();
            if (responseCode >= 100 && responseCode <= 399) {
                reader = new BufferedReader(new InputStreamReader(this.urlConnection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(this.urlConnection.getErrorStream()));
            }

            buffer = new StringBuffer();
            String responseBodyLine = "";
            while ((responseBodyLine = reader.readLine()) != null) {
                //responseBody += responseBodyLine;
                buffer.append(responseBodyLine);
            }
            if (buffer.length() != 0) {
                responseBody = buffer.toString();
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            //System.exit(7);
        } finally {
            if (this.urlConnection != null) {
                this.urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }

        return responseBody;
    }
}

package com.schunker.mtls;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class WireLogSSLSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory delegate;

    public WireLogSSLSocketFactory(SSLSocketFactory sf) {
        this.delegate = sf;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return this.delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return this.delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return new WireLogSocket((SSLSocket)this.delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return new WireLogSocket((SSLSocket)this.delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException, UnknownHostException {
        return new WireLogSocket((SSLSocket)this.delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return new WireLogSocket((SSLSocket)this.delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        return new WireLogSocket((SSLSocket)this.delegate.createSocket(address, port, localAddress, localPort));
    }

    private static class WireLogSocket extends SSLSocket {

        private SSLSocket delegate;

        public WireLogSocket(SSLSocket socket) {
            this.delegate = socket;
        }

        public OutputStream getOutputStream() throws IOException {
            return new LoggingOutputStream(this.delegate.getOutputStream());
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return this.delegate.getSupportedCipherSuites();
        }

        @Override
        public String[] getEnabledCipherSuites() {
            return this.delegate.getEnabledCipherSuites();
        }

        @Override
        public void setEnabledCipherSuites(String[] suites) {
            this.delegate.setEnabledCipherSuites(suites);
        }

        @Override
        public String[] getSupportedProtocols() {
            return this.delegate.getSupportedProtocols();
        }

        @Override
        public String[] getEnabledProtocols() {
            return this.delegate.getEnabledProtocols();
        }

        @Override
        public void setEnabledProtocols(String[] protocols) {
            this.delegate.setEnabledProtocols(protocols);
        }

        @Override
        public SSLSession getSession() {
            return this.delegate.getSession();
        }

        @Override
        public void addHandshakeCompletedListener(HandshakeCompletedListener listener) {
            this.delegate.addHandshakeCompletedListener(listener);
        }

        @Override
        public void removeHandshakeCompletedListener(HandshakeCompletedListener listener) {
            this.delegate.removeHandshakeCompletedListener(listener);
        }

        @Override
        public void startHandshake() throws IOException {
            this.delegate.startHandshake();
        }

        @Override
        public void setUseClientMode(boolean mode) {
            this.delegate.setUseClientMode(mode);
        }

        @Override
        public boolean getUseClientMode() {
            return this.delegate.getUseClientMode();
        }

        @Override
        public void setNeedClientAuth(boolean need) {
            this.delegate.setNeedClientAuth(need);
        }

        @Override
        public boolean getNeedClientAuth() {
            return this.delegate.getNeedClientAuth();
        }

        @Override
        public void setWantClientAuth(boolean want) {
            this.delegate.setWantClientAuth(want);
        }

        @Override
        public boolean getWantClientAuth() {
            return this.delegate.getWantClientAuth();
        }

        @Override
        public void setEnableSessionCreation(boolean flag) {
            this.delegate.setEnableSessionCreation(flag);
        }

        @Override
        public boolean getEnableSessionCreation() {
            return this.delegate.getEnableSessionCreation();
        }

        private static  class LoggingOutputStream extends FilterOutputStream {
            private static final Logger logger = Logger.getLogger(WireLogSocket.LoggingOutputStream.class.toString());
            private static final String CHARSET = "ISO-8859-1";
            private StringBuffer sb = new StringBuffer();

            public LoggingOutputStream(OutputStream out) {
                super(out);
            }

            public void write(byte[] b, int off, int len) throws IOException {
                sb.append(new String(b, off, len, CHARSET));
                logger.info("\n" + sb.toString());
                out.write(b, off, len);
            }

            public void write(int b) throws IOException {
                sb.append(b);
                logger.info("\n" + sb.toString());
                out.write(b);
            }

            public void close() throws IOException {
                logger.info("\n" + sb.toString());
                super.close();
            }
        }
    }
}

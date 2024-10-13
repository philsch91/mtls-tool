package com.schunker.tls;

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

    private static final Logger logger = Logger.getLogger(WireLogSSLSocketFactory.class.toString());
    private static final String CHARSET = "ISO-8859-1";
    private StringBuffer sb = new StringBuffer();

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
        //sb.append("createSocket(Socket s, String host, int port, boolean autoClose)");
        sb.append(String.format("createSocket(%s, %s, %d, %b) ", s, host, port, autoClose));
        logger.info("\n" + sb.toString());

        if (autoClose) {
            autoClose = !autoClose;
        }

        sb.append(String.format("s.isConnected(): %b ", s.isConnected()));
        logger.info("\n" + sb.toString());

        Socket socket = (SSLSocket)this.delegate.createSocket(s, host, port, autoClose);

        // java.net.SocketException: Socket is not connected // solved
        // java.io.IOException: Socket Closed
        socket = new WireLogSSLSocket((SSLSocket)socket);

        sb.append(String.format("socket: %s, socket.isConnected(): %b ", socket, socket.isConnected()));
        logger.info("\n" + sb.toString());

        return socket;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        sb.append("createSocket(Socket s, int port)");
        logger.info("\n" + sb.toString());
        return new WireLogSSLSocket((SSLSocket)this.delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException, UnknownHostException {
        sb.append("createSocket(String host, int port, InetAddress localHost, int localPort)");
        logger.info("\n" + sb.toString());
        return new WireLogSSLSocket((SSLSocket)this.delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        sb.append("createSocket(InetAddress host, int port)");
        logger.info("\n" + sb.toString());
        return new WireLogSSLSocket((SSLSocket)this.delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        sb.append("createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)");
        logger.info("\n" + sb.toString());
        return new WireLogSSLSocket((SSLSocket)this.delegate.createSocket(address, port, localAddress, localPort));
    }

    private static class WireLogSSLSocket extends SSLSocket {

        private SSLSocket delegate;

        public WireLogSSLSocket(SSLSocket socket) {
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

        @Override
        public boolean isConnected() {
            return this.delegate.isConnected();
        }

        @Override
        public String toString() {
            return this.delegate.toString();
        }

        private static class LoggingOutputStream extends FilterOutputStream {
            private static final Logger logger = Logger.getLogger(WireLogSSLSocket.LoggingOutputStream.class.toString());
            private static final String CHARSET = "ISO-8859-1";
            private StringBuffer sb = new StringBuffer();

            public LoggingOutputStream(OutputStream out) {
                super(out);
            }

            public void write(byte[] b, int off, int len) throws IOException {
                sb.append(new String(b, off, len, CHARSET));
                logger.info("\n" + sb.toString());
                this.out.write(b, off, len);
            }

            public void write(int b) throws IOException {
                sb.append(b);
                logger.info("\n" + sb.toString());
                this.out.write(b);
            }

            public void close() throws IOException {
                logger.info("\n" + sb.toString());
                super.close();
            }
        }
    }
}

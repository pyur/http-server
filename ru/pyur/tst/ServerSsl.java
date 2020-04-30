package ru.pyur.tst;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.pyur.tst.HttpHeader.HTTP_VERSION_1_0;

public class ServerSsl {

    private ExecutorService service;

    private int port;

//    private HttpSession http_session;

//    private WebsocketSession ws_session;



    public ServerSsl(int port) {
        this.port = port;
    }



    public void run() {
        service = Executors.newFixedThreadPool(16);

        System.out.println("Server started.");

        try {
            //SSLServerSocket server = new SSLServerSocket(port);
            //ServerSocketFactory ssocketFactory = SSLServerSocketFactory.getDefault();
            ServerSocketFactory ssocketFactory = createSSLFactory(new File("c.vtof.ru.key.pem"), new File("c.vtof.ru.fullchain.pem"), "");
            ServerSocket server_socket = ssocketFactory.createServerSocket(port);

            for(;;) {
                Socket socket = server_socket.accept();  // thread locking

                service.execute(new TransportSsl(socket, cb_protocol_server_event));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        service.shutdown();
    }




    // -------------------------------- SSL specific -------------------------------- //

    // https://stackoverflow.com/questions/2138940/import-pem-into-java-key-store

    public static SSLServerSocketFactory createSSLFactory(File privateKeyPem, File certificatePem, String password) throws Exception {
        final SSLContext context = SSLContext.getInstance("TLS");
        final KeyStore keystore = createKeyStore(privateKeyPem, certificatePem, password);
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keystore, password.toCharArray());
        final KeyManager[] km = kmf.getKeyManagers();
        context.init(km, null, null);
        return context.getServerSocketFactory();
    }



    public static KeyStore createKeyStore(File privateKeyPem, File certificatePem, final String password)
            throws Exception, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        final X509Certificate[] cert = createCertificates(certificatePem);
        final KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(null);
        // Import private key
        final PrivateKey key = createPrivateKey(privateKeyPem);
        keystore.setKeyEntry(privateKeyPem.getName(), key, password.toCharArray(), cert);
        return keystore;
    }

    private static PrivateKey createPrivateKey(File privateKeyPem) throws Exception {
        final BufferedReader r = new BufferedReader(new FileReader(privateKeyPem));
        String s = r.readLine();
        if (s == null || !s.contains("BEGIN PRIVATE KEY")) {
            r.close();
            throw new IllegalArgumentException("No PRIVATE KEY found");
        }
        final StringBuilder b = new StringBuilder();
        s = "";
        while (s != null) {
            if (s.contains("END PRIVATE KEY")) {
                break;
            }
            b.append(s);
            s = r.readLine();
        }
        r.close();
        final String hexString = b.toString();
        final byte[] bytes = DatatypeConverter.parseBase64Binary(hexString);
        return generatePrivateKeyFromDER(bytes);
    }

    private static X509Certificate[] createCertificates(File certificatePem) throws Exception {
        final List<X509Certificate> result = new ArrayList<X509Certificate>();
        final BufferedReader r = new BufferedReader(new FileReader(certificatePem));
        String s = r.readLine();
        if (s == null || !s.contains("BEGIN CERTIFICATE")) {
            r.close();
            throw new IllegalArgumentException("No CERTIFICATE found");
        }
        StringBuilder b = new StringBuilder();
        while (s != null) {
            if (s.contains("END CERTIFICATE")) {
                String hexString = b.toString();
                final byte[] bytes = DatatypeConverter.parseBase64Binary(hexString);
                X509Certificate cert = generateCertificateFromDER(bytes);
                result.add(cert);
                b = new StringBuilder();
            } else {
                if (!s.startsWith("----")) {
                    b.append(s);
                }
            }
            s = r.readLine();
        }
        r.close();

        return result.toArray(new X509Certificate[result.size()]);
    }

    private static RSAPrivateKey generatePrivateKeyFromDER(byte[] keyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        final KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) factory.generatePrivate(spec);
    }

    private static X509Certificate generateCertificateFromDER(byte[] certBytes) throws CertificateException {
        final CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(certBytes));
    }




    // -------------------------------- Protocol callback -------------------------------- //

    private ProtocolDispatcher.CallbackProtocolServerEvent cb_protocol_server_event = new ProtocolDispatcher.CallbackProtocolServerEvent() {
//        @Override
//        public int httpHeaderReceived(HttpRequest http_request) {
//            http_session = new HttpSession();
//            http_session.setRequest(http_request);
//
//            return 1;
//        }


//        @Override
//        public DispatchedData dispatchRequest(HttpRequest http_request byte[] payload) {
//            return http_session.dispatch(payload);
//        }


        @Override
        public byte[] http(HttpRequest http_request, InputStream is, OutputStream os) {
            HttpSession http_session = new HttpSession();
            http_session.setRequest(http_request);

            // todo: receive payload
            byte[] payload = new byte[0];
            try {
                payload = ProtocolDispatcher.receivePayload(is, http_request);
            } catch (Exception e) { e.printStackTrace(); }

            DispatchedData feedback = http_session.dispatch(payload);

            // ---- compose response ---- //
            HttpResponse response = new HttpResponse();
            response.setConnectionClose();
            response.setVersion(HTTP_VERSION_1_0);

            if (feedback != null) {
                response.setCode(feedback.code);

                response.addOptions(feedback.options);

                //todo "Server: string"

                if (feedback.payload != null) {
                    response.appendPayload(feedback.payload);
                }
            }

            return response.stringify();
        }



//        @Override
//        public int websocketHeaderReceived(HttpRequest http_request) {
//            ws_session = new WebsocketSession();
//            int result = ws_session.validate(http_request);
//            //WsDispatcher ws_dispatcher = ws_session.getDispatcher();
//            ws_session.setRequest(http_request);
//
//            return 1;  // ws_dispatcher
//        }


//        @Override
//        public void dispatchStreams(InputStream is, OutputStream os) {
//
//            try {
//                ws_session.dispatch(is, os);
//            } catch (Exception e) { e.printStackTrace(); }
//
//        }


        @Override
        public void websocket(HttpRequest http_request, InputStream is, OutputStream os) {
            WebsocketSession ws_session = new WebsocketSession();
            int result = ws_session.validate(http_request);
            if (result == -1) {
//todo            Http_SendReferenceWsResponseFailed();
                return;
            }

            // maybe move it to 'WebsocketSession'
            String ws_key;
            try {
                ws_key = http_request.getOption("Sec-WebSocket-Key");
            } catch (Exception e) { e.printStackTrace(); return; }

            byte[] response = ProtocolDispatcher.Http_SendReferenceWsResponseOk(ws_key);

            try {
                os.write(response);
                os.flush();
            } catch (Exception e) { e.printStackTrace(); }
            // end-move


            ws_session.setRequest(http_request);

            try {
                ws_session.dispatch(is, os);
            } catch (Exception e) { e.printStackTrace(); }

        }

    };




}
package ru.pyur.tst;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class TransportSsl extends Transport implements Runnable {

    private Socket socket;

    private ProtocolDispatcher protocolDispatcher;

    private OutputStream os;

    //private Session session;

    private WebsocketSession ws_session;



    public TransportSsl() {}


    //public TransportTcp(Socket client, Transport.TransportCallback tc, ProtocolDispatcher.CallbackServer pd_cs, ProtocolDispatcher.CallbackHttpPayload pd_chp) {
    //public TransportTcp(Socket client, Session session) {
    public TransportSsl(Socket socket) {
        Session session = new Session();

        this.socket = socket;
        protocolDispatcher = new ProtocolDispatcher(transport_callback);
        protocolDispatcher.setStateServerSession(session.getProtocolCallback());

        protocolDispatcher.setWebsocketServerCallback(ws_instancer);

        tr_callback = session.getTransportCallback();
    }



    public void createClient(String host, TransportCallback tc, ProtocolDispatcher.CallbackHttpClient pd_chc, ProtocolDispatcher.CallbackHttpPayload pd_chp) {
        tr_callback = tc;

        protocolDispatcher = new ProtocolDispatcher(transport_callback);
        protocolDispatcher.setStateHttpClient(pd_chc, pd_chp);

        try {
            socket = new Socket(host, 80);  // todo SSL
        } catch (Exception e) { e.printStackTrace(); return; }

        try {
            listen();
        } catch (Exception e) { e.printStackTrace(); }
    }



    @Override
    public void run() {
        try {
            listen();
        } catch (Exception e) { e.printStackTrace(); }

    }



    public void listen() throws Exception {

        InputStream is = socket.getInputStream();
        //HeaderInputStream hhis = new HeaderInputStream(is);
        os = socket.getOutputStream();

        if (tr_callback != null) {
            byte[] bytes = tr_callback.onConnected();
            if (bytes != null)  Send(bytes);
        }


        // ---- 1. receive header ---- //

        HttpHeader header = protocolDispatcher.processHeader_v2(is);


        // ---- 2. receive payload ---- //

        protocolDispatcher.processData_v2(is, header, os);



        is.close();
        os.close();

        System.out.println("closing socket.");
        socket.close();
    }




    private Callback transport_callback = new Callback() {
        @Override
        public int send(byte[] bytes) {
            return Send(bytes);
        }
    };



    public int Send(byte[] bytes) {
        //System.out.println("---- Send ------------------------------------------------------");
        //System.out.println(new String(bytes));
        //System.out.println("----------------------------------------------------------------");

        try {
            os.write(bytes);
            os.flush();
        } catch (Exception e) { e.printStackTrace(); return -1; }

        return 0;
    }




    // ----

    private ProtocolDispatcher.CallbackWebsocketServer ws_instancer = new ProtocolDispatcher.CallbackWebsocketServer() {
        @Override
        public int headerReceived(HttpRequest http_request) {
            ws_session = new WebsocketSession();
            int result = ws_session.validate(http_request);
            //WsDispatcher ws_dispatcher = ws_session.getDispatcher();

            return 1;  // ws_dispatcher
        }


        @Override
        public void dispatchStreams(InputStream is, OutputStream os) {
            wis = new WebsocketInputStream(is);
            wos = new WebsocketOutputStream(os);
            WsDispatcher();
        }

    };



    WebsocketInputStream wis;
    WebsocketOutputStream wos;


    private void WsDispatcher() {

        wis.read();  // etc
        //todo: stopped here

    }



}
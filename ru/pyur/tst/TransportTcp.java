package ru.pyur.tst;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class TransportTcp extends Transport implements Runnable {

    private Socket client;

    private ProtocolDispatcher protocolDispatcher;

    private OutputStream os;

    //private Session session;



    public TransportTcp() {}


    //public TransportTcp(Socket client, Transport.TransportCallback tc, ProtocolDispatcher.CallbackServer pd_cs, ProtocolDispatcher.CallbackHttpPayload pd_chp) {
    //public TransportTcp(Socket client, Session session) {
    public TransportTcp(Socket client) {
        Session session = new Session();

        this.client = client;
        protocolDispatcher = new ProtocolDispatcher(transport_callback);
        protocolDispatcher.setStateServerSession(session.getProtocolCallback());

        tr_callback = session.getTransportCallback();
    }



    public void createClient(String host, Transport.TransportCallback tc, ProtocolDispatcher.CallbackHttpClient pd_chc, ProtocolDispatcher.CallbackHttpPayload pd_chp) {
        tr_callback = tc;

        protocolDispatcher = new ProtocolDispatcher(transport_callback);
        protocolDispatcher.setStateHttpClient(pd_chc, pd_chp);

        try {
            client = new Socket(host, 80);
        } catch (Exception e) { e.printStackTrace(); return; }

        listen();
    }



    @Override
    public void run() {
        listen();
    }



    public void listen() {

        try {
            InputStream is = client.getInputStream();
            HeaderInputStream hhis = new HeaderInputStream(is);
            os = client.getOutputStream();


            if (tr_callback != null) {
                byte[] bytes = tr_callback.onConnected();
                if (bytes != null)  Send(bytes);
            }


            // ---- 1. receive header ---- //

            byte[] raw_header = new byte[8192];

            int header_size = hhis.read(raw_header);

            System.out.println("header size: " + header_size);
            //System.out.println("[" + new String(raw_header) + "]");

            protocolDispatcher.parseHeader(raw_header);


            // ---- 2. receive payload ---- //

            protocolDispatcher.processData(is);


            System.out.println("Client disconnected.");

            is.close();
            os.close();

            client.close();
        } catch (Exception e) { e.printStackTrace(); }

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


}
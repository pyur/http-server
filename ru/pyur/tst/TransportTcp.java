package ru.pyur.tst;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class TransportTcp extends Transport implements Runnable {

    private Socket client;

    private ProtocolDispatcher protocolDispatcher;

    private OutputStream os;


    public TransportTcp() {}


    //public TransportTcp(Socket client, Transport.TransportCallback tc, ProtocolDispatcher.CallbackServer pd_cs, ProtocolDispatcher.CallbackHttpPayload pd_chp) {
    public TransportTcp(Socket client, Session session) {
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
            //DataOutputStream out = new DataOutputStream(client.getOutputStream());
            //DataInputStream in = new DataInputStream(client.getInputStream());
            //ByteArrayOutputStream os = new ByteArrayOutputStream();
            //OutputStream os = new ByteArrayOutputStream();
            InputStream is = client.getInputStream();
            os = client.getOutputStream();


            byte[] buf = new byte[8192];  // in C 16384
            int received_size;

            if (tr_callback != null) {
                byte[] bytes = tr_callback.onConnected();
                if (bytes != null)  Send(bytes);
            }


            for (;;) {
                received_size = is.read(buf);

                if (received_size == -1) {
                    // connection failed
                    break;
                }

                else if (received_size == 0) {
                    // remote host closed connection ?
                    System.out.println("received_size is 0");
                    //break;
                    continue;
                }

                System.out.println("received " + received_size + " bytes.");
                //System.out.println(new String(buf));


                protocolDispatcher.append(buf, received_size);


                int result = protocolDispatcher.parseStream();

                if (result < 0) {
                    System.out.println("unexpected protocol error");
                    // set: unexpected protocol error
                    break;
                }

                else if (result > 0) {
                    System.out.println("Force close stream. Protocol signalled, that all payload is received.");
                    //return_code = SESSION_LISTEN_OK;
                    break;
                }

            }  // for

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
        System.out.println("---- Send ------------------------------------------------------");
        System.out.println(new String(bytes));
        System.out.println("----------------------------------------------------------------");

        try {
            os.write(bytes);
            os.flush();
        } catch (Exception e) { e.printStackTrace(); return -1; }

        return 0;
    }



}
package ru.pyur.tst;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class TransportSsl extends Transport implements Runnable {

    private Socket socket;

    private ProtocolDispatcher protocol_dispatcher;

    private OutputStream output_stream;

    private HttpSession http_session;

    private WebsocketSession ws_session;



    //public TransportSsl() {}


    public TransportSsl(Socket socket) {
        this.socket = socket;
        protocol_dispatcher = new ProtocolDispatcher(callback_transport_control);
        protocol_dispatcher.setStateServer(cb_protocol_server_event);

//r        protocol_dispatcher.setWebsocketServerCallback(cb_protocol_server_event);

//r        callback_transport_events = session.getTransportCallback();
    }



    public void createClient(String host, CallbackTransportEvents tc, ProtocolDispatcher.CallbackProtocolHttpClient cb_protocol_http_client) {
        callback_transport_events = tc;

        protocol_dispatcher = new ProtocolDispatcher(callback_transport_control);
        protocol_dispatcher.setStateHttpClient(cb_protocol_http_client);

        try {
            socket = new Socket(host, 80);  // todo SSL
        } catch (Exception e) { e.printStackTrace(); return; }

        try {
            listen();
        } catch (Exception e) { e.printStackTrace(); }
    }




    // -------------------------------- Starting thread -------------------------------- //

    @Override
    public void run() {
        try {
            listen();
        } catch (Exception e) { e.printStackTrace(); }
    }



    public void listen() throws Exception {

        InputStream is = socket.getInputStream();
        output_stream = socket.getOutputStream();

        if (callback_transport_events != null) {
            byte[] bytes = callback_transport_events.onConnected();
            if (bytes != null)  Send(bytes);
        }


        // ---- 1. receive header ---- //

        HttpHeader header = protocol_dispatcher.processHeader_v2(is);


        // ---- 2. receive payload ---- //

        protocol_dispatcher.processData_v2(is, header);



        // ---- finalizing connection ---- //

        //if (callback_transport_events != null) { callback_transport_events.onClosing(); }

        is.close();
        output_stream.close();

        System.out.println("closing socket.");
        socket.close();
    }




    // --------------------------------------------------------------------------- //
    // -------------------------------- Callbacks -------------------------------- //
    // --------------------------------------------------------------------------- //

    private CallbackTransportControl callback_transport_control = new CallbackTransportControl() {
        @Override
        public int send(byte[] bytes) {
            return Send(bytes);
        }

        @Override
        public OutputStream getOutputStream() {
            return output_stream;
        }
    };



    public int Send(byte[] bytes) {
        //System.out.println("---- Send ------------------------------------------------------");
        //System.out.println(new String(bytes));
        //System.out.println("----------------------------------------------------------------");

        try {
            output_stream.write(bytes);
            output_stream.flush();
        } catch (Exception e) { e.printStackTrace(); return -1; }

        return 0;
    }




    // -------------------------------- Protocol callback -------------------------------- //
    // todo: it's not 'transport's job. move somewhere else

    private ProtocolDispatcher.CallbackProtocolServerEvent cb_protocol_server_event = new ProtocolDispatcher.CallbackProtocolServerEvent() {
        @Override
        public int httpHeaderReceived(HttpRequest http_request) {
            http_session = new HttpSession();
            http_session.setRequest(http_request);

            return 1;
        }


        @Override
        public DispatchedData dispatchRequest(byte[] payload) {
            return http_session.dispatch(payload);
        }



        @Override
        public int websocketHeaderReceived(HttpRequest http_request) {
            ws_session = new WebsocketSession();
            int result = ws_session.validate(http_request);
            //WsDispatcher ws_dispatcher = ws_session.getDispatcher();
            ws_session.setRequest(http_request);

            return 1;  // ws_dispatcher
        }


        @Override
        public void dispatchStreams(InputStream is, OutputStream os) {

            try {
                ws_session.dispatch(is, os);
            } catch (Exception e) { e.printStackTrace(); }

        }

    };



}
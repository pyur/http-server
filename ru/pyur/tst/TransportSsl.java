package ru.pyur.tst;

import ru.pyur.tst.sample_host.SampleHost;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class TransportSsl implements Runnable {

    private Socket socket;

//x    private ProtocolDispatcher protocol_dispatcher;

//x    private OutputStream output_stream;



    //public TransportSsl() {}


//x    public TransportSsl(Socket socket, ProtocolDispatcher.CallbackProtocolServerEvent cb_protocol_server_event) {
    public TransportSsl(Socket socket) {
        this.socket = socket;
//x        protocol_dispatcher = new ProtocolDispatcher();
//?        protocol_dispatcher.setStateServer(cb_protocol_server_event);

//r        protocol_dispatcher.setWebsocketServerCallback(cb_protocol_server_event);

//r        callback_transport_events = session.getTransportCallback();
//todo        callback_transport_events = ?;
    }


/*
    public void createClient(String host, CallbackTransportEvents tc, ProtocolDispatcher.CallbackProtocolHttpClient cb_protocol_http_client) {
        callback_transport_events = tc;

//        protocol_dispatcher = new ProtocolDispatcher(callback_transport_control);
        protocol_dispatcher = new ProtocolDispatcher();
        protocol_dispatcher.setStateHttpClient(cb_protocol_http_client);

        try {
            socket = new Socket(host, 80);  // todo SSL
        } catch (Exception e) { e.printStackTrace(); return; }

        try {
            listen();
        } catch (Exception e) { e.printStackTrace(); }
    }
*/



    // -------------------------------- Starting thread -------------------------------- //

    @Override
    public void run() {
        try {
            listen();
        } catch (Exception e) { e.printStackTrace(); }
    }



    public void listen() throws Exception {

        InputStream input_stream = socket.getInputStream();
        OutputStream output_stream = socket.getOutputStream();

//        if (callback_transport_events != null) {
////            byte[] bytes = callback_transport_events.onConnected();
////            if (bytes != null)  Send(bytes);
//            callback_transport_events.onConnected(output_stream);
//        }


        // ---- 1. receive header ---- //

//x        HttpHeader header = protocol_dispatcher.processHeader_v2(is);

//x        final int MAX_LINE = 2048;
        NewlineReader nr = new NewlineReader(input_stream);

        HttpRequest request_header;

        request_header = new HttpRequest();

//x        byte[] header_line = new byte[MAX_LINE];
//x        int line_size;

//x        line_size = nr.read(header_line);  // maybe replace with "Reader"
        byte[] header_line = nr.read();
//x        if (line_size == -1)  throw new Exception("input stream unexpectedly ends while header receive.");

        System.out.println("---- Request ---------------------------------------------------");
        System.out.println(new String(header_line));

        request_header.setFirstLine(new String(header_line));

        // -------- feed options -------- //
        for(;;) {
//x            header_line = new byte[MAX_LINE];
//x            line_size = nr.read(header_line);  // maybe replace with "Reader"
            header_line = nr.read();
            //System.out.println("line_size: " + line_size);

//x            if (line_size == 0)  break;
            if (header_line.length == 0)  break;
//x            if (line_size == -1)  throw new Exception("input stream unexpectedly ends while header options receive.");

            System.out.println(new String(header_line));
            request_header.addOption(new String(header_line));
        }
        System.out.println("----------------------------------------------------------------");



        // ---- 2. receive payload ---- //

//x        protocol_dispatcher.processData_v2(header, is, output_stream);
        // ---- dispatch host ---- //

        String host_name = "";
        Host host = null;

        //try {
            host_name = request_header.getOption("Host");
            // 'host_name' might be 'null'
        //} catch (Exception e) { }

        //if (host_name.isEmpty()) {
        //    host = new EmptyHost();
        //}

        //else if (host.equals("dbadmin.vtof.ru")) {
        //    host = new DbAdminHost();
        //}

        //else {
        //    host = new OnlyIpHost();
        //}

        host = new SampleHost();

        if (host != null) {
            host.init(request_header, input_stream, output_stream);
            host.dispatch();
        }



        // ---- finalizing connection ---- //

        //if (callback_transport_events != null) { callback_transport_events.onClosing(); }

        input_stream.close();
        output_stream.close();

        System.out.println("closing socket.");
        socket.close();
    }




    // --------------------------------------------------------------------------- //
    // -------------------------------- Callbacks -------------------------------- //
    // --------------------------------------------------------------------------- //

//    private CallbackTransportControl callback_transport_control = new CallbackTransportControl() {
//        @Override
//        public int send(byte[] bytes) {
//            return Send(bytes);
//        }

//        @Override
//        public OutputStream getOutputStream() {
//            return output_stream;
//        }
//    };


/*
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
*/

}
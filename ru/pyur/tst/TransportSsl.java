package ru.pyur.tst;

import ru.pyur.tst.sample_host.SampleHost;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class TransportSsl implements Runnable {

    private Socket socket;



    public TransportSsl(Socket socket) {
        this.socket = socket;
    }




    // ---------------------------- Starting client thread ---------------------------- //

    @Override
    public void run() {
        System.out.println("client thread started.");

        try {
            listen();
        } catch (Exception e) { e.printStackTrace(); }

        System.out.println("client thread finished.");
    }



    public void listen() throws Exception {

        InputStream input_stream = socket.getInputStream();
        OutputStream output_stream = socket.getOutputStream();


        // ---- 1. receive header ---- //

        NewlineReader newline_reader = new NewlineReader(input_stream);

        HttpRequest request_header = new HttpRequest();

        byte[] header_line = newline_reader.read();

        System.out.println("---- Request ---------------------------------------------------");
        System.out.println(new String(header_line));

        request_header.setFirstLine(new String(header_line));

        // -------- feed options -------- //
        for(;;) {
            header_line = newline_reader.read();

            if (header_line.length == 0)  break;

            System.out.println(new String(header_line));
            request_header.addOption(new String(header_line));
        }
        System.out.println("----------------------------------------------------------------");



        // ---- 2. receive payload ---- //

        // ---- dispatch host ---- //

        String host_name = "";
        Host host = null;

        host_name = request_header.getOption("Host");
        // 'host_name' might be 'null'

        //if (host_name == null) {
        //    host = new EmptyHost();
        //}

        //else if (host_name.isEmpty()) {
        //    host = new EmptyHost();
        //}

        //else if (host.equals("dbadmin.vtof.ru")) {
        //    host = new DbAdminHost();
        //}

        //else {
        //    host = new OnlyIpHost();
        //}

        host = new SampleHost();  // sample


        if (host != null) {
            host.init(request_header, input_stream, output_stream);
            host.dispatch();
        }



        // ---- finalizing connection ---- //

        input_stream.close();
        output_stream.close();

        System.out.println("closing socket.");
        socket.close();
    }


}
package ru.pyur.tst.client;

import ru.pyur.tst.*;
import ru.pyur.tst.util.PStr;
import ru.pyur.tst.util.Util;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static ru.pyur.tst.HttpHeader.HTTP_VERSION_1_1;
import static ru.pyur.tst.HttpRequest.HTTP_METHOD_GET;


public abstract class HttpClient {

    private String host;
    private String path;
    private Url url;

    private InputStream input_stream;
    private OutputStream output_stream;


    private HttpRequest request_header;
    //private byte[] request_payload;

    private HttpResponse response_header;
    private byte[] payload;


    public void init(Url url) {
        this.url = url;
    }



//    public void setHost(String host) { this.host = host; }

//    public void setPath(String path) { this.path = path; }


    public byte[] getPayload() { return payload; };



    public void fetch() throws Exception {

        //Socket socket = new Socket(host, 80);

        SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket)factory.createSocket(url.getHost(), url.getPort());

        socket.startHandshake();

        input_stream = socket.getInputStream();
        output_stream = socket.getOutputStream();



        // -------- send request -------- //

        request_header = new HttpRequest();
        request_header.setMethod(HTTP_METHOD_GET);
        request_header.setLocation(url.getLocation());
        request_header.setVersion(HTTP_VERSION_1_1);

        request_header.addOption("Host", url.getHost());
        request_header.addOption("User-Agent", "ReferenceHttpClient/1.0 (Java)");
        request_header.addOption("Connection", "close");  // keep-alive

        System.out.println("---- Request ---------------------------------------------------");
        System.out.println(new String(request_header.stringify()));
        System.out.println("----------------------------------------------------------------");
        output_stream.write(request_header.stringify());



        // -------- receive answer -------- //

        final int MAX_LINE = 2048;
        NewlineInputStream nis = new NewlineInputStream(input_stream, MAX_LINE);  // maybe expand to 4096, because cookies max limit

        response_header = new HttpResponse();

        byte[] header_line = new byte[MAX_LINE];
        int line_size;

        line_size = nis.read(header_line);  // maybe replace with "Reader"
        if (line_size == -1)  throw new Exception("input stream unexpectedly ends while header receive.");

        System.out.println("---- Response --------------------------------------------------");
        System.out.println(new String(header_line, 0, line_size));

        response_header.setFirstLine(new String(header_line, 0, line_size));

        // -------- feed options -------- //
        for(;;) {
            header_line = new byte[MAX_LINE];
            line_size = nis.read(header_line);  // maybe replace with "Reader"
            //System.out.println("line_size: " + line_size);

            if (line_size == 0)  break;
            if (line_size == -1)  throw new Exception("input stream unexpectedly ends while header options receive.");

            System.out.println(new String(header_line, 0, line_size));
            response_header.addOption(new String(header_line, 0, line_size));
        }
        System.out.println("----------------------------------------------------------------");



        // ---------------- //

        boolean done = onHeader(response_header);


        if (response_header.hasOption("Set-Cookie")) {
            for (PStr opt : response_header.getOptions()) {
                if (opt.key.equals("Set-Cookie")) {  // todo: case insensitivity
                    onSetCookie(new Cookie(opt.value));
                }
            }
        }


        // -------- payload -------- //
        if (!done) {
            receivePayload();
            onPayload(payload);
        }


        System.out.println("closing client socket.");
        input_stream.close();
        output_stream.close();
        socket.close();
    }




    // ---- callables ----------------

    //public void onHeader(HttpResponse http_response) { }
    public boolean onHeader(HttpResponse rs) { return false; }

    public void onSetCookie(Cookie cookie) { }

    public void onPayload(byte[] payload) { }




    // --------------------------------------------------------------------------------

    private void receivePayload() throws Exception {

        String payload_length_s = response_header.getOption("Content-Length");
        if (payload_length_s != null) {
            int payload_length = Integer.parseInt(payload_length_s);

            int readed = 0;
            payload = new byte[payload_length];

            for(;;) {
                if (readed >= payload_length)  break;
                int readed1 = input_stream.read(payload, readed, payload_length - readed);
                if (readed1 == -1)  break;
                readed += readed1;
            }
        }


        else if (response_header.hasOption("Transfer-Encoding")) {
            String[] transfer_encoding = response_header.getOptionSplit("Transfer-Encoding");
            if (Util.inArray(transfer_encoding, "chunked")) {
                ChunkedPayloadInputStream payload_is = new ChunkedPayloadInputStream(input_stream);
                ByteArrayOutputStream os_payload = new ByteArrayOutputStream();

                byte[] payload_fragment = new byte[65536];

                for(;;) {
                    int readed = payload_is.read(payload_fragment);
                    if (readed == -1)  break;
                    os_payload.write(payload_fragment, 0, readed);
                }

                payload = os_payload.toByteArray();
            }
        }


        //else {
        //try read until stream ends
        //}

    }


}
package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;


public abstract class Host {

    protected HttpRequest request_header;
    protected byte[] payload;

    protected HttpResponse response_header;

    protected InputStream input_stream;
    protected OutputStream output_stream;



    public void init(HttpRequest request_header, InputStream input_stream, OutputStream output_stream) {
        this.request_header = request_header;
        this.input_stream = input_stream;
        this.output_stream = output_stream;

        response_header = new HttpResponse();
        response_header.setVersion(HttpHeader.HTTP_VERSION_1_1);
        response_header.setCode(200);
    }


    public abstract void dispatch();



    // --------------------------------------------------------------------------------

    public HttpRequest getRequestHeader() { return request_header; }

    public byte[] getPayload() { return payload; }

    public HttpResponse getResponseHeader() { return response_header; }

    public InputStream getInputStream() { return input_stream; }

    public OutputStream getOutputStream() { return output_stream; }

    public ArrayList<PStr> getQuery() { return request_header.getQuery(); }



    // ---- for control response from modules ----------------

    public void setCode(int code) { response_header.setCode(code); }

    public void addOption(String name, String value) { response_header.addOption(name, value); }

    public void setCookie(String name, String value, int expires, String path) { response_header.setCookie(name, value, expires, path); }




    // ---- Detect features ------------------------------------------------------------

    protected boolean isWebsocket() {

        //if (http_request.hasOption("Connection")) {
        //    //System.out.println("option \"Connection\" found.");
        //    String[] opts = http_request.getOptionSplit("Connection");
        //    if (Util.inArray(opts, "Upgrade")) {  // can be: "keep-alive, Upgrade"
        //        System.out.println("option \"Connection\" has \"Upgrade\".");
        //        //...
        //    }
        //}


//        if (request_header.hasOption("Upgrade")) {
        //String option_upgrage;

        //try {
        String option_upgrage = request_header.getOption("Upgrade");
        //} catch (Exception e) { e.printStackTrace(); return false; }
        if (option_upgrage == null)  return false;
//x        System.out.println("...Upgrade success.");

        if (!option_upgrage.equals("websocket")) { return false; }
//x        System.out.println("...equals websocket success.");


        // -------- check 'Sec-WebSocket-Version' -------- //
//        if (!request_header.hasOption("Sec-WebSocket-Version")) {
//            System.out.println("defaultHeaderDispatcher. error. option \"Sec-WebSocket-Version\" not found.");
//            return false;
//        }


        // -------- check 'Sec-WebSocket-Key' -------- //
//        if (request_header.hasOption("Sec-WebSocket-Key")) {
//            System.out.println("option \"Sec-WebSocket-Key\" present.");

//x                    state = HTTP_STATE_WS_SERVER;

            // set-up websocket server
            // callback: websocket server

//            return true;
//        }  // "Sec-WebSocket-Key"
//        }  // "websocket"
//        }  // "Upgrade: "


        return true;
    }




    protected boolean isCast() {

        if (request_header.hasOption("Icy-MetaData")) {
            return true;
        }

        return false;
    }




    // --------------------------------------------------------------------------------

    protected void receivePayload() throws Exception {
//x        byte[] payload = new byte[0];

        //if (request_header.hasOption("Content-Length")) {
        String payload_length_s = request_header.getOption("Content-Length");
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


        else if (request_header.hasOption("Transfer-Encoding")) {
            String[] transfer_encoding = request_header.getOptionSplit("Transfer-Encoding");
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

//x        return payload;
    }




    // ---- Responses ------------------------------------------------------------

    protected void response(byte[] contents) {
        response_header.appendPayload(contents);
        send(response_header.stringify());
    }



    protected void response400() {
        response_header.setCode(400);
        send(response_header.stringify());
    }



    protected void response404(String message) {
        response_header.setCode(404);
        response_header.appendPayload(message);
        send(response_header.stringify());
    }



    protected void responseSwitchingOk(String ws_key) {
        //System.out.println("responseSwitchingOk()");
        response_header.setCode(101);
        //response_header.appendPayload(contents);
        response_header.addOption("Upgrade", "websocket");
        response_header.addOption("Connection", "Upgrade");

        // ---- calc hash ---- //
        String ws_key_resp = ws_key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

        byte[] sha1 = null;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(ws_key_resp.getBytes());  // getBytes("UTF-8")
            sha1 = crypt.digest();
        } catch(NoSuchAlgorithmException e) { e.printStackTrace(); }

        String szKey64 = new String (Base64.getEncoder().encode(sha1));

        response_header.addOption("Sec-WebSocket-Accept", szKey64);

        send(response_header.stringify());
    }




    protected void send(byte[] bytes) {
        //System.out.println("---- HttpSession. Send -----------------------------------------");
        //System.out.println(new String(bytes));
        //System.out.println("----------------------------------------------------------------");

        try {
            output_stream.write(bytes);
            output_stream.flush();
        } catch (Exception e) { e.printStackTrace(); }
    }



}
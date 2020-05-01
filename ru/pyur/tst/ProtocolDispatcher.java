package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static ru.pyur.tst.HttpHeader.HTTP_VERSION_1_0;
import static ru.pyur.tst.HttpHeader.HTTP_VERSION_1_1;


public class ProtocolDispatcher {


    private int state;
    private final int HTTP_STATE_UNDEFINED = 0;
    private final int HTTP_STATE_SERVER = 1;
    private final int HTTP_STATE_HTTP_SERVER = 2;
    private final int HTTP_STATE_HTTP_CLIENT = 3;

    private final int HTTP_STATE_WS_SERVER = 4;  // websocket
    private final int HTTP_STATE_WS_CLIENT = 5;
    private final int HTTP_STATE_WS_CLOSED = 6;

    private final int HTTP_STATE_CAST_SERVER = 7;  // media broadcast
    private final int HTTP_STATE_CAST_CLIENT = 8;



//joined    private String ws_key;


    // ---------------- Callbacks ---------------- //

    private Transport.CallbackTransportControl callback_transport_control;



//x    private CallbackProtocolHeader callback_protocol_header;
//x
//x    public interface CallbackProtocolHeader {
//x        int dispatchRequest(HttpRequest http_request);
//x    }



    private CallbackProtocolHttpClient callback_protocol_http_client;

    public interface CallbackProtocolHttpClient {
        int dispatchResponse(HttpResponse http_response);
        int dispatchPayload(byte[] payload);
    }




    private CallbackProtocolServerEvent callback_protocol_server_event;

    public interface CallbackProtocolServerEvent {
        //int httpHeaderReceived(HttpRequest http_request);
        //DispatchedData dispatchRequest(byte[] payload);
        void http(HttpRequest http_request, InputStream is, OutputStream os);

        //int websocketHeaderReceived(HttpRequest http_request);
        //void dispatchStreams(InputStream is, OutputStream os);
        void websocket(HttpRequest http_request, InputStream is, OutputStream os);
    }




    public ProtocolDispatcher(Transport.CallbackTransportControl callback_transport_control) {
        this.callback_transport_control = callback_transport_control;
    }




    public void setStateServer(CallbackProtocolServerEvent cb_server_event) {
        state = HTTP_STATE_SERVER;

//x        callback_protocol_header = defaultHeaderDispatcher;
        callback_protocol_server_event = cb_server_event;
    }




    public void setStateHttpClient(CallbackProtocolHttpClient chc) {
        state = HTTP_STATE_HTTP_CLIENT;

        callback_protocol_http_client = (chc != null) ? chc : defaultHttpClientDispatcher;
    }




    // ---------------------------- Parse/dispatch header v2 ---------------------------- //

    public HttpHeader processHeader_v2(InputStream is) throws Exception {
        final int MAX_LINE = 2048;
        NewlineInputStream nis = new NewlineInputStream(is, MAX_LINE);  // maybe expand to 4096, because cookies max limit

        HttpHeader header;

        if (state == HTTP_STATE_SERVER) {
            header = new HttpRequest();
        } else {
            header = new HttpResponse();
        }

        byte[] header_line = new byte[MAX_LINE];
        int line_size;

        line_size = nis.read(header_line);  // maybe replace with "Reader"
        if (line_size == -1)  throw new Exception("input stream unexpectedly ends while header receive.");
        System.out.println(new String(header_line, 0, line_size));

        header.setFirstLine(new String(header_line, 0, line_size));

        // -------- feed options -------- //
        for(;;) {
            header_line = new byte[MAX_LINE];
            line_size = nis.read(header_line);  // maybe replace with "Reader"
            //System.out.println("line_size: " + line_size);

            if (line_size == 0)  break;
            if (line_size == -1)  throw new Exception("input stream unexpectedly ends while header options receive.");

            System.out.println(new String(header_line, 0, line_size));
            header.addOption(new String(header_line, 0, line_size));
        }


        // -------- Process header -------- //

        int result;

        // -- mode server. process request -- //
        // http server, websocket server, cast server
        if (state == HTTP_STATE_SERVER) {
            HttpRequest http_request = (HttpRequest)header;
//x            result = callback_protocol_header.dispatchRequest(http_request);
            try {
                dispatchRequest(http_request);
            } catch (Exception e) { e.printStackTrace(); return header; }
        }


        // -- if client - process response -- //
        else if (state == HTTP_STATE_HTTP_CLIENT) {
            HttpResponse http_response = (HttpResponse)header;
            result = callback_protocol_http_client.dispatchResponse(http_response);

            if (result < 0)   throw new Exception("processing in callback 'dispatchResponse' failed");  // failed
        }


        // -- if websocket client - process response -- //
        else if (state == HTTP_STATE_WS_CLIENT) {

            //todo  result = new HttpWsResponse(raw_header);

            // ---- prepare for data exchange ---- //
            //  http->message = Expandable_Create();
            //  http->ws_prev_fin = 1;

            //  if (http->cbWsReady)  http->cbWsReady(http->cbInstance, http);
        }


        return header;
    }




    // --------------------------------------------------------------------------------- //
    // ------------------------------- 3 Dispatch header ------------------------------- //
    // --------------------------------------------------------------------------------- //

    // ------------------------ default Server request dispatcher ------------------------ //

    private void dispatchRequest(HttpRequest http_request) throws Exception {

        // ---------------- WebSocket ---------------- //

        //if (http_request.hasOption("Connection")) {
        //    //System.out.println("option \"Connection\" found.");
        //    String[] opts = http_request.getOptionSplit("Connection");
        //    if (Util.inArray(opts, "Upgrade")) {  // can be: "keep-alive, Upgrade"
        //        System.out.println("option \"Connection\" has \"Upgrade\".");
        //        //...
        //    }
        //}


        if (http_request.hasOption("Upgrade")) {
            if (http_request.getOption("Upgrade").equals("websocket")) {

                // -------- check 'Sec-WebSocket-Version' -------- //
                if (!http_request.hasOption("Sec-WebSocket-Version")) {
                    System.out.println("defaultHeaderDispatcher. error. option \"Sec-WebSocket-Version\" not found.");
                }


                // -------- check 'Sec-WebSocket-Key' -------- //
                if (http_request.hasOption("Sec-WebSocket-Key")) {
                    System.out.println("option \"Sec-WebSocket-Key\" present.");

//joined                    ws_key = http_request.getOption("Sec-WebSocket-Key");

//joined                    // ---- validate auth ---- //
//joined                    int iResult = 0;
//joined                    if (callback_protocol_server_event != null)  iResult = callback_protocol_server_event.websocketHeaderReceived(http_request);
//joined                    else  iResult = -1;

//joined                    if (iResult < 0) {
                    //Http_SendReferenceWsResponseAuthFail(http);  // into called custom processor
//joined                        return -1;
//joined                    }

                    state = HTTP_STATE_WS_SERVER;

//joined                    Http_SendReferenceWsResponseOk();

                    // set-up websocket server
                    // callback: websocket server


                    return;
                }  // "Sec-WebSocket-Key"
            }  // "websocket"
        }  // "Upgrade: "



        // ---------------- Audio Cast ---------------- //

        if (http_request.hasOption("Icy-MetaData")) {

            //todo            int metadata = atoi(opt->value);
//todo            DebugIVerbose_(http, "found \"icy-metadata: #\". use mode \"audio cast\". ");  di(metadata);  dcd();

            state = HTTP_STATE_CAST_SERVER;

//todo            Http_SendStandardCastResponse(http);

            // set-up audio cast (after payload receive)
            // callback: audio cast (after payload receive)

//todo            if (http->cbCastReady)  http->cbCastReady(http->cbInstance, http);

            return;
        }  // "Icy-MetaData: "




        //System.out.println("no special options found, work as plain standard http server.");

        // ---------------- casual http request ---------------- //
        state = HTTP_STATE_HTTP_SERVER;

//joined        int result2 = 0;
//joined        if (callback_protocol_server_event != null)  result2 = callback_protocol_server_event.httpHeaderReceived(http_request);

    }




    // ---------------------------- Process data v2 ---------------------------- //

    public void processData_v2(HttpHeader header, InputStream is, OutputStream os) throws Exception {
        int result = 0;

        if (state == HTTP_STATE_HTTP_SERVER) {
//r            byte[] payload = receivePayload(is, header);

            HttpRequest http_request = (HttpRequest)header;

            if (callback_protocol_server_event != null) {
                //maybe here spawn session, call, and dispose
//r                DispatchedData feedback = callback_protocol_server_event.dispatchRequest(payload);
//r                byte[] osb = callback_protocol_server_event.http(http_request, is, os);
                callback_protocol_server_event.http(http_request, is, os);

//r                //Http_Send(osb);
            }

        }


        else if (state == HTTP_STATE_HTTP_CLIENT) {
            byte[] payload = receivePayload(is, header);

            HttpRequest http_request = (HttpRequest)header;

            if (callback_protocol_http_client != null)  result = callback_protocol_http_client.dispatchPayload(payload);  // todo: pass header

            if (result < 0)   throw new Exception("processing in callback 'dispatchRequest' failed");  // failed
        }


        // ---- WS SERVER, WS CLIENT - dispatch incoming messages, quit on 'close' message ---- //
        else if (state == HTTP_STATE_WS_CLIENT || state == HTTP_STATE_WS_SERVER) {
            //WebsocketInputStream wis = new WebsocketInputStream(is);
            //WebsocketOutputStream wos = new WebsocketOutputStream(os);
            //result = callback_ws.dataStream(wis, wos);
//r            OutputStream os = getOutputStream();

            // start websocket dispatcher
//r            if (callback_protocol_server_event != null)  result = callback_protocol_server_event.dispatchStreams(is, os);

            HttpRequest http_request = (HttpRequest)header;
            callback_protocol_server_event.websocket(http_request, is, os);

            //if (result < 0)   throw new Exception("processing in callback 'dispatchRequest' failed");  // failed
        }

    }




    public static byte[] receivePayload(InputStream is, HttpHeader header) throws Exception {
        byte[] payload = new byte[0];

        if (header.hasOption("Content-Length")) {
            String payload_length_s = header.getOption("Content-Length");
            int payload_length = Integer.parseInt(payload_length_s);

            int readed = 0;
            payload = new byte[payload_length];

            for(;;) {
                if (readed >= payload_length)  break;
                int readed1 = is.read(payload, readed, payload_length - readed);
                if (readed1 == -1)  break;
                readed += readed1;
            }
        }


        else if (header.hasOption("Transfer-Encoding")) {
            String[] transfer_encoding = header.getOptionSplit("Transfer-Encoding");
            if (Util.inArray(transfer_encoding, "chunked")) {
                ChunkedPayloadInputStream payload_is = new ChunkedPayloadInputStream(is);
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                byte[] payload_fragment = new byte[65536];

                for(;;) {
                    int readed = payload_is.read(payload_fragment);
                    if (readed == -1)  break;
                    os.write(payload_fragment, 0, readed);
                }

                payload = os.toByteArray();
            }
        }


        //else {
            //try read until stream ends
        //}

        return payload;
    }




    // -------------------- default Http Server payload dispatcher -------------------- //

/*
    private CallbackHttpPayload defaultHttpServerPayloadDispatcher = new CallbackHttpPayload() {
        @Override
        public int dispatchPayload(byte[] payload) {
            HttpResponse response = new HttpResponse();
            response.setConnectionClose();

//todo        HttpResponse_Server(rs, Http_CB_GetServerString(http));

            response.appendPayload("Hello sample Java server.");

            Http_Send(response.stringify());

            return 0;
        }
    };
*/




    // -------------------- default Http Client response dispatcher -------------------- //

    private CallbackProtocolHttpClient defaultHttpClientDispatcher = new CallbackProtocolHttpClient() {
        @Override
        public int dispatchResponse(HttpResponse http_response) {

            // ---------------- grab cookies ---------------- //

/* todo
        LPair lspOptions = rs->options;
        for (size_t idx = 0; lspOptions[idx]; idx++) {
            if (!strcmp(lspOptions[idx]->key, "Set-Cookie")) {
                DebugICy(http, "\"Set-Cookie\" present.");
                HttpResponse_ParseAddCookie(rs, lspOptions[idx]->value);
            }
        }
*/
        // wrap this loop in function:
        //LCookie co = rs->cookies = Parse_SetCookies(rs->options);


        // CallbackTransportControl for process custom `options`


        return 1;
        }



        @Override
        public int dispatchPayload(byte[] payload) {
            // remote host sent answer payload
            System.out.println(new String(payload));

            return 0;
        }
    };




    // -------------------- default Http Client payload dispatcher -------------------- //

//    private CallbackHttpPayload defaultHttpClientPayloadDispatcher = new CallbackHttpPayload() {
//    };




    // -------------------- default Websocket Client response dispatcher -------------------- //

//x    private int Http_ProcessWsResponse() {
//    private CallbackProtocolWsClient defaultWsClientDispatcher = new CallbackProtocolWsClient() {
//        @Override
//        public int dispatchResponse(HttpResponse http_response) {

/*

        http->response = HttpResponse_Parse(http->lsLines);
        if (!http->response)  return -1;

        // HTTP/1.1 101 Switching Protocols
        // Upgrade: websocket
        // Connection: Upgrade
        // Sec-WebSocket-Accept: hsBlbuDTkk24srzEOTBUlZAlC2g=


        // todo: check 'code' for '101'


        // ---------------- Setting up WS receiver ---------------- //

        HttpResponse rs = http->response;
        Pair opt;

        opt = PairList_FindByKey(rs->options, "Connection");
        if (!opt) {
            DebugIError(http, "option \"Connection\" not found.");  // missing
            return -1;
        }

        if (strcmp(opt->value, "Upgrade")) {
            DebugIError(http, "option \"Connection\" not \"Upgrade\".");
            return -1;
        }


        opt = PairList_FindByKey(rs->options, "Upgrade");
        if (!opt) {
            DebugIError(http, "option \"Upgrade\" not present.");
            return -1;
        }

        if (strcmp(opt->value, "websocket")) {
            DebugIError(http, "option \"Upgrade\" not \"websocket\".");
            return -1;
        }



        opt = PairList_FindByKey(rs->options, "Sec-WebSocket-Accept");
        if (!opt) {
            DebugIError(http, "option \"Sec-WebSocket-Accept\" not present.");  // missing
            return -1;
        }

        // todo: verify 'Sec-WebSocket-Accept'
        DebugWarn_("Sec-WebSocket-Accept: "); ds(opt->value);  dcd();  // incorrect



        // CallbackTransportControl for process custom `options`

*/

//        return 1;
//    }




    public static byte[] Http_SendReferenceWsResponseOk(String ws_key) {
        HttpResponse rs = new HttpResponse();

        // HTTP/1.1 101 Switching Protocols
        // Upgrade: websocket
        // Connection: Upgrade
        // Sec-WebSocket-Accept: hsBlbuDTkk24srzEOTBUlZAlC2g=

        rs.setVersion(HTTP_VERSION_1_1);
        rs.setCode(101);

        //HttpResponse_Server(rs, Http_CB_GetServerString(http));

        rs.addOption("Upgrade", "websocket");
        rs.addOption("Connection", "Upgrade");

        // ---- calc hash ---- //
        //Expandable exRsKey = Expandable_Create();
        //printf("http->ws_key: %s", http->ws_key);
        //Expandable_AppendString(exRsKey, http->ws_key);
        //Expandable_AppendString(exRsKey, "258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
        String ws_key_resp = ws_key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        //System.out.println("ws_key_resp: " + ws_key_resp);

        //Str szKeyMix = Expandable_ConvertToString(&exRsKey);

        //char sha1_hash[21];
        //DebugInfo("Http_SendStandardWsResponseOk(). calling sha1...");
        //SHA1_(sha1_hash, szKeyMix, strlen(szKeyMix));

        byte[] sha1 = null;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(ws_key_resp.getBytes());  // getBytes("UTF-8")
            sha1 = crypt.digest();
        } catch(NoSuchAlgorithmException e) { e.printStackTrace(); }
        //catch(UnsupportedEncodingException e) { e.printStackTrace(); }

        //DebugInfo("Http_SendStandardWsResponseOk(). calling String_Base64EncodeBin()...");
        //Str szKey64 = String_Base64EncodeBin((Blob)sha1_hash, 20);
        String szKey64 = new String (Base64.getEncoder().encode(sha1));

        rs.addOption("Sec-WebSocket-Accept", szKey64);
        //String_Destroy(szKey64);


        //DebugInfo("Http_SendStandardWsResponseOk(). calling HttpResponse_Stringify()...");
        //Str szResponse = HttpResponse_Stringify(rs);
        //Http_SendStr(http, szResponse);
//r        Http_Send(rs.stringify());
        return rs.stringify();
        //byte[] tmp = rs.stringify();
        //System.out.println(new String(tmp));
        //Http_Send(tmp);

        //DebugInfo("Http_SendStandardWsResponseOk(). done...");
    }




    // --------------------------------------------------------------------------------------

    private int Http_Send(byte[] bytes) {
        // ---- redirect call to transport ---- //
        if (callback_transport_control != null)  return callback_transport_control.send(bytes);
        return 0;
    }


//x    private OutputStream getOutputStream() {
//x        if (callback_transport_control != null)  return callback_transport_control.getOutputStream();
//x        return null;
//x    }


}
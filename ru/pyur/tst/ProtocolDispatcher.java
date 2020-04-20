package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import static ru.pyur.tst.HttpHeader.HTTP_VERSION_1_0;
import static ru.pyur.tst.HttpHeader.HTTP_VERSION_1_1;


public class ProtocolDispatcher {

//r    private ByteArrayOutputStream stream;
//r    private ByteArrayOutputStream request_payload;


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



//    private final int RECV_HEADER_LIMIT = 8192;  // in C 16384


//    private HttpRequest http_request;
//    private HttpResponse http_response;


    private String ws_key;


    private Transport.Callback transport_callback;
    private Callback protocol_callback;

    public interface Callback {
        int http(byte[] bytes);
        int ws(byte[] bytes);
        int cast(byte[] bytes);
    }



    private CallbackServer callback_server;

    public interface CallbackServer {
        int requestReceived(HttpRequest http_request);
    }


    private CallbackHttpPayload callback_http_payload;

    public interface CallbackHttpPayload {
        int payloadReceived(byte[] payload);
    }


    private CallbackHttpClient callback_http_client;

    public interface CallbackHttpClient {
        int responseReceived(HttpResponse http_response);
    }


    private CallbackSession callback_session;

    public interface CallbackSession {
        //byte[] onReceived(HttpRequest http_request, byte[] payload);
        DispatchedData onReceived(HttpRequest http_request, byte[] payload);
    }



    private CallbackWebsocketServer callback_websocket_server;

    public interface CallbackWebsocketServer {
        int headerReceived(HttpRequest http_request);
        void dispatchStreams(InputStream is, OutputStream os);
    }





    public ProtocolDispatcher(Transport.Callback transport_callback) {
        this.transport_callback = transport_callback;
    }



    //public void setStateServer(CallbackServer cs, CallbackHttpPayload cp) {
    //    state = HTTP_STATE_SERVER;
    //
    //    callback_server = (cs != null) ? cs : defaultDispatchServer;
    //    callback_http_payload = (cp != null) ? cp : defaultDispatchHttpServerPayload;
    //}



    public void setStateServerSession(CallbackSession css) {
        state = HTTP_STATE_SERVER;

        callback_server = defaultDispatchServer;
        callback_http_payload = null;  // defaultDispatchHttpServerPayload
        callback_session = css;
    }


    public void setWebsocketServerCallback(CallbackWebsocketServer cb_wss) {
        callback_websocket_server = cb_wss;
    }



    public void setStateHttpClient(CallbackHttpClient chc, CallbackHttpPayload cp) {
        state = HTTP_STATE_HTTP_CLIENT;

        callback_http_client = (chc != null) ? chc : defaultDispatchHttpClient;
        callback_http_payload = (cp != null) ? cp : defaultDispatchHttpClientPayload;
    }




    // ---------------------------- 1.0. Parse/dispatch header v2 ---------------------------- //

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

        header.setFirstLine(new String(header_line));

        // -------- feed options -------- //
        for(;;) {
            header_line = new byte[MAX_LINE];
            line_size = nis.read(header_line);  // maybe replace with "Reader"
            //System.out.println("line_size: " + line_size);

            if (line_size == 0)  break;
            if (line_size == -1)  throw new Exception("line_size == -1");

            System.out.println(new String(header_line));
            header.addOption(new String(header_line));
        }


        // -------- Process header -------- //

        //todo: custom callback
        dispatchHeader_v2(header);

        return header;
    }




    public void dispatchHeader_v2(HttpHeader header) throws Exception {
        int result;

        // -- mode server. process request -- //
        // http server, websocket server, cast server
        if (state == HTTP_STATE_SERVER) {
            HttpRequest http_request = (HttpRequest)header;
            result = callback_server.requestReceived(http_request);
        }


        // -- if client - process response -- //
        else if (state == HTTP_STATE_HTTP_CLIENT) {
            HttpResponse http_response = (HttpResponse)header;
            result = callback_http_client.responseReceived(http_response);

            if (result < 0)   throw new Exception("processing in callback 'responseReceived' failed");  // failed
        }


        // -- if websocket client - process response -- //
        else if (state == HTTP_STATE_WS_CLIENT) {

            //todo  result = new HttpWsResponse(raw_header);

            // ---- prepare for data exchange ---- //
            //  http->message = Expandable_Create();
            //  http->ws_prev_fin = 1;

            //  if (http->cbWsReady)  http->cbWsReady(http->cbInstance, http);
        }


    }




    // ---------------------------- 1.0. Process data v2 ---------------------------- //

    public void processData_v2(InputStream is, HttpHeader header, OutputStream os) throws Exception {
        int result = 0;

        if (state == HTTP_STATE_HTTP_SERVER) {
            byte[] payload = receivePayload(is, header);

            HttpRequest http_request = (HttpRequest)header;

            //result = callback_server.requestReceivedWithPayload(http_request);
            //todo: default, with default response

////            if (result < 0)   throw new Exception("processing in callback 'requestReceived' failed");  // failed

            if (callback_http_payload != null)  result = callback_http_payload.payloadReceived(payload);

            if (callback_session != null) {
                //maybe here spawn session, call, and dispose
                DispatchedData feedback = callback_session.onReceived(http_request, payload);

                HttpResponse response = new HttpResponse();
                response.setConnectionClose();
                response.setVersion(HTTP_VERSION_1_0);
                response.setCode(200);


                if (feedback != null) {
                    response.addOptions(feedback.options);

                    //todo HttpResponse_Server(rs, Http_CB_GetServerString(http));

                    if (feedback.payload != null) {
                        response.appendPayload(feedback.payload);
                    }
                }
                //else {
                //...
                //}

                Http_Send(response.stringify());
            }

        }


        else if (state == HTTP_STATE_HTTP_CLIENT) {
            byte[] payload = receivePayload(is, header);

            HttpRequest http_request = (HttpRequest)header;

            result = callback_http_payload.payloadReceived(payload);  // todo: pass header

            if (result < 0)   throw new Exception("processing in callback 'requestReceived' failed");  // failed
        }


        // ---- WS SERVER, WS CLIENT - dispatch incoming messages, quit on 'close' message ---- //
        else if (state == HTTP_STATE_WS_CLIENT || state == HTTP_STATE_WS_SERVER) {
            //WebsocketInputStream wis = new WebsocketInputStream(is);
            //WebsocketOutputStream wos = new WebsocketOutputStream(os);
            //result = callback_ws.dataStream(wis, wos);

            // start websocket dispatcher
            if (callback_websocket_server != null)  callback_websocket_server.dispatchStreams(is, os);

            //if (result < 0)   throw new Exception("processing in callback 'requestReceived' failed");  // failed
        }

    }




    private byte[] receivePayload(InputStream is, HttpHeader header) throws Exception {
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




    // ----------------------------------------------------------------------------------- //
    // -------------------------------- 1.1. Parse stream -------------------------------- //
    // ----------------------------------------------------------------------------------- //

/*
    public int parseStream() {
        int result;


        // ---------------- 1. header not received ---------------- //




        // ---------------- 2. header already received ---------------- //

        // todo: switch/case

        // ---- HTTP SERVER - receive payload, dispatch and answer ---- //
        if (state == HTTP_STATE_HTTP_SERVER) {
            result = receiveHttpPayload();  // receive POST data, etc...
            if (result < 0) return -1;  // failed
            if (result == 0) return 0;  // in progress

            result = 0;
            if (callback_http_payload != null)  result = callback_http_payload.payloadReceived(request_payload.toByteArray());

            if (callback_session != null) {
                DispatchedData feedback = callback_session.onReceived(http_request, request_payload.toByteArray());

                HttpResponse response = new HttpResponse();
                response.setConnectionClose();

                if (feedback != null) {
                    if (feedback.options.size() > 0) {
                        for (PStr option : feedback.options) {
                            response.addOption(option);
                        }
                    }

                    //todo HttpResponse_Server(rs, Http_CB_GetServerString(http));

                    if (feedback.payload != null) {
                        response.appendPayload(feedback.payload);
                    }
                }
                //else {
                    //...
                //}

                Http_Send(response.stringify());
            }

            if (result < 0)  return -1;  // failed

            return 1;  // signal to transport to close connection
        }


        // ---- HTTP CLIENT - receive payload, and quit ---- //
        else if (state == HTTP_STATE_HTTP_CLIENT) {
            result = receiveHttpPayload();

            if (result < 0)  return -1;  // failed
            if (result == 0)  return 0;  // in progress

            result = callback_http_payload.payloadReceived(request_payload.toByteArray());

            if (result < 0)  return -1;  // failed

            return 1 + 0;  // all payload received
        }


        // ---- WS SERVER, WS CLIENT - dispatch incoming messages, quit on 'close' message ---- //
        else if (state == HTTP_STATE_WS_CLIENT || state == HTTP_STATE_WS_SERVER) {
            result = 0;//Websocket_ProcessStream(http);

            if (result < 0)  return -1;  // stream parse error
            if (result == 0)  return 0;
            if (result > 0) {
                //callback Ws_Closed
                return 1;  // 'close' message received
            }

            return 0;  // keep connection alive
        }


        // ---- WS CLOSED - ... ---- //
        else if (state == HTTP_STATE_WS_CLOSED) {
            System.err.println("Error. ws closed received");
            //DebugDump(http->stream->data, http->stream->size);

            return 0;  // keep connection alive
        }



        // ---- CAST SERVER - dispatch incoming messages, quit on 'close' message ---- //
        else if (state == HTTP_STATE_CAST_SERVER) {
            //iResult = Http_ReceivePayload(http);
            //if (iResult < 0)  return -1;
            //if (iResult == 0)  return 0;

            // iResult = Cast_ProcessStream(http);
            // if (iResult < 0)  return -1;  // stream parse error
            // if (iResult == 0)  return 0;

            //  Http_SendStandardCastResponse(http);

            //  Http_CB_CastReady(http);

            //return 1;  // 'close' message received
            return 0;  // keep connection alive
        }


        // ---- CAST CLIENT - dispatch incoming media stream, quit on ? ---- //
        else if (state == HTTP_STATE_WS_CLIENT) {
            result = 0;//Cast_ProcessStream(http);

            if (result < 0)  return -1;  // stream parse error
            if (result == 0)  return 0;

            //callback Cast_Closed
            return 1;  // ?'close' message received
        }


        return 0;
    }
*/


    // --------------------------------------------------------------------------------- //
    // ------------------------------- 3 Dispatch header ------------------------------- //
    // --------------------------------------------------------------------------------- //

    // ------------------------ default Server request analyzer ------------------------ //

    private CallbackServer defaultDispatchServer = new CallbackServer() {
        @Override
        public int requestReceived(HttpRequest http_request) {

            // ---------------- determining what client wants ---------------- //

            // ---------------- WebSocket ---------------- //

            //if (http_request.hasOption("Connection")) {
            //    //System.out.println("option \"Connection\" found.");
            //    String[] opts = http_request.getOptionSplit("Connection");
            //    if (Util.inArray(opts, "Upgrade")) {  // can be: "keep-alive, Upgrade"
            //        System.out.println("option \"Connection\" has \"Upgrade\".");
            //        //...
            //    }
            //}

            //----

            if (http_request.hasOption("Upgrade")) {
                if (http_request.getOption("Upgrade").equals("websocket")) {

                    // -------- check 'Sec-WebSocket-Version' -------- //
                    if (!http_request.hasOption("Sec-WebSocket-Version")) {
                        System.out.println("defaultDispatchServer. error. option \"Sec-WebSocket-Version\" not found.");
                    }


                    // -------- check 'Sec-WebSocket-Key' -------- //
                    if (http_request.hasOption("Sec-WebSocket-Key")) {
                        System.out.println("option \"Sec-WebSocket-Key\" present.");

                        ws_key = http_request.getOption("Sec-WebSocket-Key");

                        // ---- validate auth ---- //
                        int iResult;
    //todo                    if (http->cbWsProcessHeader)  iResult = http->cbWsProcessHeader(http->cbInstance, http);
                        if (callback_websocket_server != null)  iResult = callback_websocket_server.headerReceived(http_request);
                        else  iResult = -1;

                        if (iResult < 0) {
                            //Http_SendReferenceWsResponseAuthFail(http);  // into called custom processor
                            return -1;
                        }

                        state = HTTP_STATE_WS_SERVER;

                        Http_SendReferenceWsResponseOk();

                        // set-up websocket server
                        // callback: websocket server

                        // ---- prepare for data exchange ---- //
    //todo                    http->message = Expandable_Create();
    //todo                    http->ws_prev_fin = 1;

    //todo                    if (http->cbWsReady)  http->cbWsReady(http->cbInstance, http);

                        return 1;
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

                return 1;
            }  // "Icy-MetaData: "




            //System.out.println("no special options found, work as plain standard http server.");

            // ---------------- casual http request ---------------- //
            state = HTTP_STATE_HTTP_SERVER;


            return 1;
        }
    };




    // -------------------- default Http Server payload dispatcher -------------------- //

    private CallbackHttpPayload defaultDispatchHttpServerPayload = new CallbackHttpPayload() {
        @Override
        public int payloadReceived(byte[] payload) {
            HttpResponse response = new HttpResponse();
            response.setConnectionClose();

//todo        HttpResponse_Server(rs, Http_CB_GetServerString(http));

            response.appendPayload("Hello sample Java server.");

            Http_Send(response.stringify());

            return 0;
        }
    };




    // -------------------- default Http Client response dispatcher -------------------- //

    private CallbackHttpClient defaultDispatchHttpClient = new CallbackHttpClient() {
        @Override
        public int responseReceived(HttpResponse http_response) {

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


        // Callback for process custom `options`


        return 1;
        }
    };




    // -------------------- default Http Client payload dispatcher -------------------- //

    private CallbackHttpPayload defaultDispatchHttpClientPayload = new CallbackHttpPayload() {
        @Override
        public int payloadReceived(byte[] payload) {
            // remote host sent answer payload
            System.out.println(new String(payload));

            return 0;
        }
    };




    // -------------------- default Websocket Client response dispatcher -------------------- //

    private int Http_ProcessWsResponse() {
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



        // Callback for process custom `options`

*/

        return 1;
    }




    private void Http_SendReferenceWsResponseOk() {
//        DebugInfo("Http_SendStandardWsResponseOk()");
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
        Http_Send(rs.stringify());
        //byte[] tmp = rs.stringify();
        //System.out.println(new String(tmp));
        //Http_Send(tmp);

        //DebugInfo("Http_SendStandardWsResponseOk(). done...");
    }




    // --------------------------------------------------------------------------------------

    public int Http_Send(byte[] bytes) {
        // ---- redirect call to transport ---- //
        transport_callback.send(bytes);
        return 0;
    }


}
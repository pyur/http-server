package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;


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


    private HttpRequest http_request;
    private HttpResponse http_response;


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





    public ProtocolDispatcher(Transport.Callback transport_callback) {
        this.transport_callback = transport_callback;
//r        stream = new ByteArrayOutputStream();

//r        request_payload = new ByteArrayOutputStream();

//x        isHeaderReceived = false;
    }



    public void setStateServer(CallbackServer cs, CallbackHttpPayload cp) {
        state = HTTP_STATE_SERVER;

        callback_server = (cs != null) ? cs : defaultDispatchServer;
        callback_http_payload = (cp != null) ? cp : defaultDispatchHttpServerPayload;
    }



    public void setStateServerSession(CallbackSession css) {
        state = HTTP_STATE_SERVER;

        callback_server = defaultDispatchServer;
        callback_http_payload = null;
        callback_session = css;
    }



    public void setStateHttpClient(CallbackHttpClient chc, CallbackHttpPayload cp) {
        state = HTTP_STATE_HTTP_CLIENT;

        callback_http_client = (chc != null) ? chc : defaultDispatchHttpClient;
        callback_http_payload = (cp != null) ? cp : defaultDispatchHttpClientPayload;
    }



//    public void append(byte[] data, int size) {
//        try {
//            stream.write(data, 0, size);
//        } catch (Exception e) { e.printStackTrace(); }
//    }




    // --------------------------------------------------------------------------------- //
    // -------------------------------- 1. Parse stream -------------------------------- //
    // --------------------------------------------------------------------------------- //

    public void processData(InputStream is) throws Exception {
        int result;

        if (state == HTTP_STATE_HTTP_SERVER) {
            byte[] payload = new byte[0];

            if (http_request.hasOption("Content-Length")) {
                String payload_length_s = http_request.getOption("Content-Length");
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

            else if (http_request.hasOption("Transfer-Encoding")) {
                String[] transfer_encoding = http_request.getOptionSplit("Transfer-Encoding");
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
                // try to read until -1
            //}


            if (callback_http_payload != null)  result = callback_http_payload.payloadReceived(payload);

            if (callback_session != null) {
                DispatchedData feedback = callback_session.onReceived(http_request, payload);

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

            //return 1;
        }



        else if (state == HTTP_STATE_HTTP_CLIENT) {
            byte[] payload = new byte[0];
            System.out.println("processData(). HTTP_STATE_HTTP_CLIENT");

            if (http_response.hasOption("Content-Length")) {
                String payload_length_s = http_response.getOption("Content-Length");
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

            else if (http_response.hasOption("Transfer-Encoding")) {
                System.out.println("processData(). hasOption \"Transfer-Encoding\"");

                String[] transfer_encoding = http_response.getOptionSplit("Transfer-Encoding");

                if (Util.inArray(transfer_encoding, "chunked")) {
                    System.out.println("processData(). Transfer-Encoding has \"chunked\"");

                    ChunkedPayloadInputStream chunked_is = new ChunkedPayloadInputStream(is);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();

                    byte[] payload_fragment = new byte[65536];
//                    for(;;) {
//                        payload_fragment = new byte[65536];
//                        int readed2 = is.read(payload_fragment);
//                        System.out.println("---------------- raw (" + readed2 + ") ----------------");
//                        System.out.println(new String(payload_fragment));
//                        System.out.println("---------------- end ----------------");
//                        if (readed2 == -1)  break;
//                    }

                    for(;;) {
                        int readed = chunked_is.read(payload_fragment);
                        System.out.println("==== processData(). chunked_is.readed: " + readed);
                        if (readed == -1)  break;
                        os.write(payload_fragment, 0, readed);
                    }

                    payload = os.toByteArray();
                }
            }

            result = callback_http_payload.payloadReceived(payload);

            //return 1;
        }

    }


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
    // ----------------------------- 2 Parse stream header ----------------------------- //
    // --------------------------------------------------------------------------------- //

    public void parseHeader(byte[] raw_header) throws Exception {
        int result;

        // todo: switch/case

        // -- mode server. process request -- //
        // http server, websocket server, cast server
        if (state == HTTP_STATE_SERVER) {
            http_request = new HttpRequest();

            http_request.parse(raw_header);

            result = callback_server.requestReceived(http_request);

            if (result < 0)   throw new Exception("processing in callback 'requestReceived' failed");  // failed
        }


        // -- if client - process response -- //
        else if (state == HTTP_STATE_HTTP_CLIENT) {
            http_response = new HttpResponse();

            http_response.parse(raw_header);

            result = callback_http_client.responseReceived(http_response);

            if (result < 0)   throw new Exception("processing in callback 'responseReceived' failed");  // failed
        }


        // -- if websocket client - process response -- //
        else if (state == HTTP_STATE_WS_CLIENT) {

//todo                    result = new HttpWsResponse(raw_header);

            // ---- prepare for data exchange ---- //
            //  http->message = Expandable_Create();
            //  http->ws_prev_fin = 1;

            //  if (http->cbWsReady)  http->cbWsReady(http->cbInstance, http);
        }


        // -- if castclient - process response -- //
        else if (state == HTTP_STATE_CAST_CLIENT) {

//todo                    result = HttpCastResponse(raw_header);

            // prepare for stream receiving

            //  if (http->cbCastReady)  http->cbCastReady(http->cbInstance, http);
        }

    }





    // --------------------------------------------------------------------------------- //
    // ------------------------------- 3 Dispatch header ------------------------------- //
    // --------------------------------------------------------------------------------- //

    // ------------------------ default Server request analyzer ------------------------ //

    private CallbackServer defaultDispatchServer = new CallbackServer() {
        @Override
        public int requestReceived(HttpRequest http_request) {
            //System.out.println("Http_ProcessRequest()");


            // ---------------- determining what client wants ---------------- //

            // ---------------- WebSocket ---------------- //

            if (http_request.hasOption("Connection")) {
                System.out.println("option \"Connection\" found.");
                String[] opts = http_request.getOptionSplit("Connection");
                if (Util.inArray(opts, "Upgrade")) {  // can be: "keep-alive, Upgrade"
                    System.out.println("option \"Connection\" has \"Upgrade\".");
                    //...
                }
            }

            //----

            if (http_request.hasOption("Upgrade")) {
                if (http_request.getOption("Upgrade").equals("websocket")) {

                    // -------- check 'Sec-WebSocket-Version' -------- //
                    if (!http_request.hasOption("Sec-WebSocket-Version")) {
                        System.out.println("defaultDispatchServer. error. option \"Sec-WebSocket-Version\" not found.");
                    }


                    // -------- check 'Sec-WebSocket-Key' -------- //
                    if (http_request.hasOption("Sec-WebSocket-Key")) {
                        //System.out.println("option \"Sec-WebSocket-Key\" present.");

                        ws_key = http_request.getOption("Sec-WebSocket-Key");

                        // ---- validate auth ---- //
                        int iResult;
    //todo                    if (http->cbWsProcessHeader)  iResult = http->cbWsProcessHeader(http->cbInstance, http);
                        /*else*/ iResult = -1;
                        if (iResult < 0) {
                            //Http_SendStandardWsResponseAuthFail(http);  // into called custom processor
                            return -1;
                        }

                        state = HTTP_STATE_WS_SERVER;

    //todo                    Http_SendStandardWsResponseOk(http);
                        //Http_SendStandardWsResponseFail(http);  in case of not correct params

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

            // ---------------- prepare payload receiver ---------------- //

//r            PStr opt;

//r            opt = PStr.PairList_FindByKey(http_response.options, "Transfer-Encoding");  // chunked, compress, deflate, gzip, identity
//r            // Several values can be listed, separated by a comma
//r            if (opt != null) {
//r                //DebugIInfo(http, "used \"Transfer-Encoding\".");
//r                // lame compare. todo: explode value with ','
//r                if (opt.value.equals("chunked")) {
//r                    //ds(" chunked");
//r    //!                isChunked = true;
//r                }
//r            }


//r            opt = PStr.PairList_FindByKey(http_response.options, "Content-Length");
//r            if (opt != null) {
//r                //DebugICy(http, "\"Content-Length\" present.");
//r                content_length = Integer.parseInt(opt.value);
//r                http_response.content_length = content_length;
//r            }



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




    private int Http_ProcessCastResponse() {
        // ...
        return 0;
    }




    // -------------------------------------------------------------------------------------- //
    // -------------------------------- Receive http payload -------------------------------- //
    // -------------------------------------------------------------------------------------- //




    // --------------------------------------------------------------------------------------

    public int Http_Send(byte[] bytes) {
        // ---- redirect call to transport ---- //
        transport_callback.send(bytes);
        return 0;
    }




//    // --------------------------------------------------------------------------------------
//
//    public void onConnected() {
//        if (state == HTTP_STATE_HTTP_CLIENT) {
//
//        }
//    }


}
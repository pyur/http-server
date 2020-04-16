package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class ProtocolDispatcher {

    private ByteArrayOutputStream stream;
    private ByteArrayOutputStream payload;


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


    private boolean isHeaderReceived;

    private final int RECV_HEADER_LIMIT = 8192;  // in C 16384

    private byte[] raw_header;

    private HttpRequest http_request;
//    private HttpResponse http_response;

/*
    HTTP_METHOD_Unknown = 0,
    HTTP_METHOD_GET = 1,
    HTTP_METHOD_POST = 2,
    HTTP_METHOD_HEAD = 3,
    HTTP_METHOD_OPTIONS = 4,
    HTTP_METHOD_PUT = 5,
    HTTP_METHOD_DELETE = 6,
    HTTP_METHOD_PATCH = 7,
    HTTP_METHOD_CONNECT= 8,
    HTTP_METHOD_TRACE = 9,

    HTTP_VERSION_Unknown = 0,
    HTTP_VERSION_0_9 = 1,
    HTTP_VERSION_1_0 = 2,
    HTTP_VERSION_1_1 = 3,
*/


    private String ws_key;

    private boolean isChunked = false;
    private int content_length = 0;
    private int chunkSize = 0;

    private boolean isDeflated = false;
    private boolean isGzipped = false;



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
        stream = new ByteArrayOutputStream();
        //stream = new DataOutputStream();
        //stream = new FilterOutputStream();
        //stream = new BufferedOutputStream();

        payload = new ByteArrayOutputStream();

        isHeaderReceived = false;
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



    public void append(byte[] data, int size) {
        try {
            stream.write(data, 0, size);
        } catch (Exception e) { e.printStackTrace(); }
    }




    // --------------------------------------------------------------------------------- //
    // -------------------------------- 1. Parse stream -------------------------------- //
    // --------------------------------------------------------------------------------- //

    public int parseStream() {
        int result;


        // ---------------- 1. header not received ---------------- //

        if (!isHeaderReceived) {
            result = parseStreamHeader();

            if (result < 0)  return -1;  // failed
            else if (result == 0)  return 0;  // not found


            // todo: switch/case

            // -- mode server. process request -- //
            // http server, websocket server, cast server
            if (state == HTTP_STATE_SERVER) {
                http_request = new HttpRequest();
                try {
                    http_request.parse(raw_header);
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }

                result = callback_server.requestReceived(http_request);

                if (result < 0)  return -1;  // failed
                //if (result > 0) {
                    // todo: prepare for payload receiving
                //}

            }


            // -- if client - process response -- //
            else if (state == HTTP_STATE_HTTP_CLIENT) {
                HttpResponse http_response = new HttpResponse();
                try {
                    http_response.parse(raw_header);
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }

                result = callback_http_client.responseReceived(http_response);

                if (result < 0)  return -1;

                // todo: prepare for payload receiving
            }


            // -- if websocket client - process response -- //
            else if (state == HTTP_STATE_WS_CLIENT) {
                try {
//todo                    result = new HttpWsResponse(raw_header);
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }

                // ---- prepare for data exchange ---- //
              //  http->message = Expandable_Create();
              //  http->ws_prev_fin = 1;

              //  if (http->cbWsReady)  http->cbWsReady(http->cbInstance, http);
            }


            // -- if castclient - process response -- //
            else if (state == HTTP_STATE_CAST_CLIENT) {
                try {
//todo                    result = HttpCastResponse(raw_header);
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }

                // prepare for stream receiving

              //  if (http->cbCastReady)  http->cbCastReady(http->cbInstance, http);
            }


            isHeaderReceived = true;
        }



        // ---------------- 2. header already received ---------------- //

        // todo: switch/case

        // ---- HTTP SERVER - receive payload, dispatch and answer ---- //
        if (state == HTTP_STATE_HTTP_SERVER) {
            result = receiveHttpPayload();  // receive POST data, etc...
            if (result < 0) return -1;  // failed
            if (result == 0) return 0;  // in progress

            result = 0;
            if (callback_http_payload != null)  result = callback_http_payload.payloadReceived(payload.toByteArray());

            if (callback_session != null) {
                DispatchedData feedback = callback_session.onReceived(http_request, payload.toByteArray());

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

            result = callback_http_payload.payloadReceived(payload.toByteArray());

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




    // --------------------------------------------------------------------------------- //
    // ----------------------------- 2 Parse stream header ----------------------------- //
    // --------------------------------------------------------------------------------- //

    private int parseStreamHeader() {

        byte[] bytes = stream.toByteArray();

        int header_size = searchDoubleNewLine(bytes);

        if (header_size < 0) {
            // -- "\r\n\r\n" not found -- //
            if (bytes.length > RECV_HEADER_LIMIT) {
                System.out.println("ProtocolDispatcher. Search header within limit (" + RECV_HEADER_LIMIT + ") failed. Terminate.");
                return -2;  // failed
            }

            return 0;  // not found
        }


        // -- copy header from stream -- //

        raw_header = Arrays.copyOfRange(bytes, 0, header_size);

        //System.out.println("---- raw header ------------------------------------------------");
        //System.out.println(new String(raw_header));
        //System.out.println("----------------------------------------------------------------");


        // -- cut header from stream-- //

        stream = new ByteArrayOutputStream();
        int offset = header_size + 4;
        byte[] trail = Arrays.copyOfRange(bytes, offset, offset + bytes.length );  // 4 is for "\r\n\r\n"
        //System.out.println("beg: " + (header_size + 4) + ", end: " + stm.length);
        try {
            stream.write(trail);
        } catch (Exception e) { e.printStackTrace(); }


        return 1;  // header found
    }



    private int searchDoubleNewLine(byte[] stm) {
        int search_end = stm.length - 3;

        for (int i = 0; i < search_end; i++) {
            if (stm[i] != '\r')  continue;

            if (stm[i+1] == '\n' && stm[i+2] == '\r' && stm[i+3] == '\n')  return i;
        }

        return -1;  // not found
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

            PStr opt;

            // ---- GET ---- //
            //if (rq->method == 1) {
            //return 1;  // ??
            //  }

            // ---- POST ---- //
            //else if (rq->method == 2) {
            //  }

    // Host: www.googleapis.com
    // Connection: close
    // Content-Type: application/x-www-form-urlencoded
    // Content-Length: %d

    // Content-Type: application/json
    // Authorization: Bearer %s


            // ---------------- WebSocket ---------------- //

            //opt = PairList_FindByKey(rq->options, "Connection");
            //if (opt) {
            //  //DebugIMsg(http, "option \"Connection\" found.");
            //  if (!strcmp(opt->value, "Upgrade")) {  // can be: "keep-alive, Upgrade"
            //    //DebugIMsg(http, "option \"Connection\" is \"Upgrade\".");

            //----

            opt = PStr.PairList_FindByKey(http_request.options, "Upgrade");
            if (opt != null) {
                if (opt.value.equals("websocket")) {
                    //DebugIVerbose(http, "found \"Upgrade: websocket\". use mode \"websocket\".");

                    // -------- check 'Sec-WebSocket-Version' -------- //
                    //opt = Pair_ListFindByKey(rq->options, "Sec-WebSocket-Version");
                    //if (!opt) { DebugIInfo(session, "option \"Sec-WebSocket-Version\" not found."); }


                    // -------- check 'Sec-WebSocket-Key' -------- //
                    opt = PStr.PairList_FindByKey(http_request.options, "Sec-WebSocket-Key");
                    if (opt != null) {
                        //DebugIMsg(http, "option \"Sec-WebSocket-Key\" present.");
                        ws_key = opt.value;

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

            opt = PStr.PairList_FindByKey(http_request.options, "Icy-MetaData");  // "icy-metadata"
            if (opt != null) {
    //todo            int metadata = atoi(opt->value);
    //todo            DebugIVerbose_(http, "found \"icy-metadata: #\". use mode \"audio cast\". ");  di(metadata);  dcd();

                state = HTTP_STATE_CAST_SERVER;

    //todo            Http_SendStandardCastResponse(http);

                // set-up audio cast (after payload receive)
                // callback: audio cast (after payload receive)

    //todo            if (http->cbCastReady)  http->cbCastReady(http->cbInstance, http);

                return 1;
            }  // "Icy-MetaData: "




            //DebugIInfo(http, "no special options found, work as plain standard http server.");

            // ---------------- casual http request ---------------- //
            state = HTTP_STATE_HTTP_SERVER;

            // ---------------- prepare payload receiver ---------------- //
            // ---- determine format of payload ---- //
            opt = PStr.PairList_FindByKey(http_request.options, "Transfer-Encoding");  // chunked, compress, deflate, gzip, identity
            // Several values can be listed, separated by a comma
            if (opt != null) {
    //!            DebugIInfo(http, "used \"Transfer-Encoding\".");
                // lame compare. must explode value with ','
                //if (!strcmp(opt->value, "chunked")) {
                if (opt.value.equals("chunked")) {
    //!                ds(" chunked");
                    isChunked = true;
                }
            }  // "Transfer-Encoding: "


            //if (!isChunked) {
            //    // ? where setted-up 'Content-Length:' ?
            //    opt = PStr.PairList_FindByKey(http_request.options, "Content-Length");
            //
            //    // ...
            //}


            return 1;
        }
    };




    // ----------------------------------------------------------------------------

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




    // ----------------------------------------------------------------------------

    private CallbackHttpClient defaultDispatchHttpClient = new CallbackHttpClient() {
        @Override
        public int responseReceived(HttpResponse http_response) {

        // ---------------- prepare payload receiver ---------------- //

        PStr opt;

        opt = PStr.PairList_FindByKey(http_response.options, "Transfer-Encoding");  // chunked, compress, deflate, gzip, identity
        // Several values can be listed, separated by a comma
        if (opt != null) {
            //DebugIInfo(http, "used \"Transfer-Encoding\".");
            // lame compare. todo: explode value with ','
            if (opt.value.equals("chunked")) {
                //ds(" chunked");
                isChunked = true;
            }
        }


        opt = PStr.PairList_FindByKey(http_response.options, "Content-Length");
        if (opt != null) {
            //DebugICy(http, "\"Content-Length\" present.");
            content_length = Integer.parseInt(opt.value);
            http_response.content_length = content_length;
        }



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




    // ----------------------------------------------------------------------------

    private CallbackHttpPayload defaultDispatchHttpClientPayload = new CallbackHttpPayload() {
        @Override
        public int payloadReceived(byte[] payload) {
            // remote host sent answer payload
            System.out.println(new String(payload));

            return 0;
        }
    };




    // ----------------------------------------------------------------------------

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

    private int receiveHttpPayload() {

        if (!isChunked) {
            //size_t size_to_copy = http->stream->size;
            //if (size_to_copy > http->contentSize)  size_to_copy = http->contentSize;

            //r Expandable_Append(http->payload, http->stream->data, http->stream->size);
            try { payload.write(stream.toByteArray()); } catch (Exception e) { e.printStackTrace(); }

            //r Expandable_Truncate(http->stream);
            stream.reset();

//!            DebugCy_("compare: size (");  di(http->payload->size); ds(") >= ("); di(http->contentLength); ds(")");

            if (payload.size() >= content_length)  return 1;
        }


        // ---------------- chunked ---------------- //

        else {
            for(;;) {
                if (chunkSize == 0) {
                    //if (http->stream->size < 6)  return 0;
                    //r int size_size = Http_HasNewLine(http->stream->data, http->stream->size);
                    byte[] stream_bytes = stream.toByteArray();
                    int size_size = Http_HasNewLine(stream_bytes);
                    if (size_size < 0)  return 0;
                    else if (size_size > 8) {
                        System.err.println("HttpSession. stream chunk size exceed 8 chars.");
                        return -1;
                    }

                    //r Str szChunkSize = String_Substr(http->stream->data, size_size);
                    byte[] byChunkSize = Arrays.copyOfRange(stream_bytes, 0, size_size);
                    chunkSize = byte_HexToInt(byChunkSize);
                    //r String_Destroy(szChunkSize);
                    System.out.println("chunk size: " + chunkSize);
                    chunkSize += 2;  // for trailing "\r\n"

                    //if (!http->chunkSize) {
                    if (chunkSize == 2) {
                        //DebugWarn("chunk [0]. flushing.");
                        //return 1;
                        // or raise a flush flag, read remaining 2 bytes, and then flush.
                        System.out.println("chunk [0]. ready to flush.");
                    }

                    //r Expandable_Shift(http->stream, size_size + 2);  // 2 is for "\r\n"
                    stream.reset();
                    try {
                        stream.write(Arrays.copyOfRange(stream_bytes, size_size + 2, stream_bytes.length));
                    } catch (Exception e) { e.printStackTrace(); }
                }


//!                DebugCy_("stream->size: ");  di(http->stream->size);  ds(", chunk size: ");  di(http->chunkSize);

                //r if (http->stream->size >= http->chunkSize) {
                if (stream.size() >= chunkSize) {
                    //DebugWarn("equals or more. copy to payload.");
                    byte[] stream_bytes = stream.toByteArray();

                    //r Expandable_Append(http->payload, http->stream->data, http->chunkSize - 2);  // excluding trailing "\r\n"
                    try { payload.write(stream_bytes, 0, (chunkSize - 2) ); } catch (Exception e) { e.printStackTrace(); }

                    //r Expandable_Shift(http->stream, http->chunkSize);
                    stream.reset();
                    try {
                        stream.write(Arrays.copyOfRange(stream_bytes, chunkSize, stream_bytes.length));
                    } catch (Exception e) { e.printStackTrace(); }

                    if (chunkSize == 2) {
                        System.out.println("remainings readed. flushing.");
                        chunkSize = 0;  // redundant
                        return 1;
                    }

                    //DebugWarning("chunk move. size reset.");
                    chunkSize = 0;
                }

                else {
                    break;
                }

            }  // for

        }


        return 0;
    }



    private int Http_HasNewLine(byte[] bytes) { //Str stream, size_t size) {
        int p = 0;
        int search_end = p + bytes.length - 1;

        // ooooooox
        for (;;) {
            while (bytes[p] != '\r' && p < search_end)  p++;

            if (p >= search_end)  break;  // return -1;

            if (bytes[p+1] == '\n')  return p;

            p++;
        }

        return -1;
    }



    private int byte_HexToInt(byte[] p) {
        //Str p = string + strlen(string) - 1;
        int i = 0;  // p.length - 1;
        int nibble;
        int number = 0;
//        Bin val = (Bin)&number;
        //long val = 0;
        int val = 0;

        int limit = 4;
        while(i < p.length && (limit--) >= 0) {  // todo: noy sure about '>='

//            // -- first nibble -- //
            nibble = p[i];

            if (nibble >= '0' && nibble <= '9')  nibble = nibble - '0';
            else if (nibble >= 'a' && nibble <= 'f')  nibble = nibble - 'a' + 10;
            else if (nibble >= 'A' && nibble <= 'F')  nibble = nibble - 'A' + 10;
            else  break;
            //*val = nibble;
            val <<= 4;
            val |= nibble & 0xF;
            //p--;
            i++;

            // -- second nibble -- //
//            if (!*p)  break;
//            nibble = *p;
//
//            if (nibble >= '0' && nibble <= '9')  nibble = nibble - '0';
//            else if (nibble >= 'a' && nibble <= 'f')  nibble = nibble - 'a' + 10;
//            else if (nibble >= 'A' && nibble <= 'F')  nibble = nibble - 'A' + 10;
//            else  break;
//
//            *val |= (nibble & 0xF) << 4;  // is `& 0xF` necessary?
//            p--;
//
//            val++;
        }

        //number = _byteswap_ulong(number);

        return val;  // number;
    }




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
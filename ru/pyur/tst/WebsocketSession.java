package ru.pyur.tst;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class WebsocketSession {

//    private WebsocketSession session;

    private HttpRequest request_header;
//    private byte[] request_payload;
    private String host;  // option "Host" in http header

    private String prefix;  // a - api, i - image, e - embed, etc
    public String module;
    public String action;

    DummyModCallback ws_mod_cb;




    public WebsocketSession() {
//        session = this;
        module = "";
        action = "";
    }




    // --------------------------------------------------------------------------------

    public Transport.CallbackTransportEvents getTransportCallback() { return cb_transport; }

    private Transport.CallbackTransportEvents cb_transport = new Transport.CallbackTransportEvents() {
        @Override
        public byte[] onConnected() {
            System.out.println("onConnected()");
            return null;
        }

        @Override
        public void onFailed() {
            System.out.println("onFailed()");
        }
    };



//    private ProtocolDispatcher.CallbackProtocolHeader cb_server = new ProtocolDispatcher.CallbackProtocolHeader() {
//        @Override
//        public int dispatchRequest(HttpRequest http_request) {
//            System.out.println("----------- Request -----------");
//            System.out.println("[" + http_request.szMethod + "] [" + http_request.szLocation + "] [" + http_request.szVersion + "]");
//
//            for (PStr option : http_request.options) { System.out.println("[" + option.key + "] : [" + option.value + "]"); }
//            System.out.println("--------------------------------");
//
//            return 0;
//        }
//    };


/*
    public ProtocolDispatcher.CallbackSession getProtocolCallback() { return cb_session; }

    private ProtocolDispatcher.CallbackSession cb_session = new ProtocolDispatcher.CallbackSession() {
        @Override
        public DispatchedData onReceived(HttpRequest http_request, byte[] bytes) {
//            System.out.println("----------- Request -----------");
//            System.out.println("[" + http_request.szMethod + "] [" + http_request.szLocation + "] [" + http_request.szVersion + "]");
//
//            for (PStr option : http_request.options) { System.out.println("[" + option.key + "] : [" + option.value + "]"); }
//            System.out.println("--------------------------------");
//
//            System.out.println("----------- Payload ------------");
//            System.out.println(new String(bytes));
//            System.out.println("--------------------------------");

            request_header = http_request;
            request_payload = bytes;

            return getHtml();
        }
    };
*/

    public void setRequest(HttpRequest http_request) {
        request_header = http_request;
    }



    // --------------------------------------------------------------------------------

    public void dispatch(InputStream is, OutputStream os) throws Exception {

        // ---------------- getHtml Host ---------------- //

        //ArrayList<PStr> options = request_header.getOptions();
        //host = null;
        //if (request_header.hasOption("Host")) {
    //    host = request_header.getOption("Host");
        //}


        //if (host == null) {
            // default
        //}

        //else if (host == "mydomain.com") {
            // mydomain.com
        //}



        // ---------------- getHtml URI ---------------- //

        boolean isPrefixed = false;

        // only '/'
        if (request_header.lsPath.length < 3) {
            // root
        }

        // '/module/'
        // '/a/' api
        // '/e/' embed
        // '/i/' image
        // '/k/' kiosk
        else {
            //if (request_header.lsPath[1].equals("a")) { isPrefixed = true;  prefix = 1; }
            //else if (request_header.lsPath[1].equals("e")) { isPrefixed = true;  prefix = 2; }
            //else if (request_header.lsPath[1].equals("i")) { isPrefixed = true;  prefix = 3; }
            //else if (request_header.lsPath[1].equals("k")) { isPrefixed = true;  prefix = 4; }

            if (request_header.lsPath[1].length() == 1) {
                isPrefixed = true;
                prefix = request_header.lsPath[1];
            }
        }


        if (!isPrefixed) {
            // '/module/'
            if (request_header.lsPath.length == 3) {
                module = request_header.lsPath[1];
            }

            // '/module/action/'
            else if (request_header.lsPath.length == 4) {
                module = request_header.lsPath[1];
                action = request_header.lsPath[2];
            }

        }

        else {
            // '/?/module/'
            if (request_header.lsPath.length == 4) {
                module = request_header.lsPath[2];
            }

            // '/?/module/action/'
            else if (request_header.lsPath.length == 5) {
                module = request_header.lsPath[2];
                action = request_header.lsPath[3];
            }
        }


        // ---- getHtml 'module' ---- //

        //if (module.isEmpty())  module = "default";

        //WsDispatcher md = null;
        ModuleInfo info = null;

        //todo: attach dispatcher


        if (module.isEmpty()) {
            info = new ru.pyur.tst.websocket.Info(this);
            //ws_mod_cb = info.setGetWebsocketCallback();
            WebsocketModule wsm = info.getWs();
            // todo: info.getWs(is, os).getHtml();
            //info.setWebsocketSession(this);
            wsm.setStreams(is, os);
            wsm.dispatch();
        }

//        if (module.equals("ws")) {
//        }

        else if (module.equals("battleship")) {
            info = new ru.pyur.tst.battleship.Info(this);
            //ws_mod_cb = info.setGetWebsocketCallback();
            WebsocketModule wsm = info.getWs();
            // todo: info.getWs(is, os).getHtml();
            //info.setWebsocketSession(this);
            wsm.setStreams(is, os);
            wsm.dispatch();
        }

//        else if (module.equals("db")) {
//            md = new ru.pyur.tst.dbedit.Info(session).getHtml();
//        }
//
//        else if (module.equals("res")) {
//            md = new ru.pyur.tst.resources.Info(session).getHtml();
//        }

        //else if (module.equals("ext")) {
        //    new ru.pyur.tst.extsample.ExtMod();
        //}


//        if (md != null) {
//            byte[] contents = md.getContent();
//            byte[] compressed_contents = null;
//
//            ArrayList<PStr> response_options = md.getOptions();
//
//            return new DispatchedData(contents, response_options);
//        }




        //return null;
    }




    public ArrayList<PStr> getQuery() {
        return request_header.getQuery();
    }




    public int validate(HttpRequest http_request) {
        System.out.println("WebsocketSession. validate()");


        return 1;
    }


}
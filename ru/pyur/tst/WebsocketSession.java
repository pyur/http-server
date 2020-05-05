package ru.pyur.tst;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class WebsocketSession {

//    private WebsocketSession session;

    private HttpRequest request_header;

    public InputStream input_stream;
    public OutputStream output_stream;

    private DbManager db_manager;

    private Auth auth;

    private String host;  // option "Host" in http header

//    private String prefix;  // a - api, i - image, e - embed, etc
    public String module;
    public String action;

//    DummyModCallback ws_mod_cb;




//    public WebsocketSession() {
    public WebsocketSession(HttpRequest http_request, InputStream is, OutputStream os) {
//        session = this;
//        module = "";
//        action = "";
        request_header = http_request;
        input_stream = is;
        output_stream = os;
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

//todo    public DbManager getDbManager() { return db_manager; }

    public String getAction() { return action; }

    public InputStream getInputStream() { return input_stream; }

    public OutputStream getOutputStream() { return output_stream; }


//    public void setRequest(HttpRequest http_request) {
//        request_header = http_request;
//    }



    // --------------------------------------------------------------------------------

//    public void dispatch(InputStream is, OutputStream os) throws Exception {
    public void dispatch() throws Exception {

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

//        boolean isPrefixed = false;
        module = "";
        action = "";

        int path_length = request_header.lsPath.length;


        if (path_length < 2) {
            // response 400
            throw new Exception("path length less than 2");
        }


        // only '/'
//        if (request_header.lsPath.length < 3) {
            // root
//        }

        // '/module/'
        // '/a/' api
        // '/e/' embed
        // '/i/' image
        // '/k/' kiosk
//        else {
            //if (request_header.lsPath[1].equals("a")) { isPrefixed = true;  prefix = 1; }
            //else if (request_header.lsPath[1].equals("e")) { isPrefixed = true;  prefix = 2; }
            //else if (request_header.lsPath[1].equals("i")) { isPrefixed = true;  prefix = 3; }
            //else if (request_header.lsPath[1].equals("k")) { isPrefixed = true;  prefix = 4; }

//            if (request_header.lsPath[1].length() == 1) {
//                isPrefixed = true;
//                prefix = request_header.lsPath[1];
//            }
//        }


        // '/module/'
        if (path_length == 3) {
            module = request_header.lsPath[1];
        }

        // '/module/action/'
        else if (path_length == 4) {
            module = request_header.lsPath[1];
            action = request_header.lsPath[2];
        }

        else {
            // response 400
            throw new Exception("path length more than 4");
        }



        // ---- getHtml 'module' ---- //

        //if (module.isEmpty())  module = "default";

        //WsDispatcher md = null;
        ModuleInfo module_info = ModulesManager.getModuleInfo(module);

        if (module_info == null) {
//            response404("no such module \"" + module + "\".");
            return;
        }

//        module_info.setWebsocketSession(this);


        WebsocketDispatcher wsd = module_info.getWs(getAction());

        if (wsd == null) {
//            response404("module \"" + module + "\" lack html support");
            return;
        }

        wsd.setSession(this);


        //wsd.setStreams(is, os);
        wsd.dispatch();
    }




//    public ArrayList<PStr> getQuery() {
//        return request_header.getQuery();
//    }




    public int validate() {
        System.out.println("WebsocketSession. validate()");


        return 1;
    }


}
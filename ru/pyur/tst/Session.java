package ru.pyur.tst;


import java.io.FileInputStream;
import java.util.ArrayList;

public class Session {

    private Session session;

    private HttpRequest request_header;
    private byte[] request_payload;

    private String prefix;
    public String module;
    public String action;




    public Session() {
        session = this;
        module = "";
        action = "";
    }




    // --------------------------------------------------------------------------------

    public Transport.TransportCallback getTransportCallback() { return cb_transport; }

    private Transport.TransportCallback cb_transport = new Transport.TransportCallback() {
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



//    private ProtocolDispatcher.CallbackServer cb_server = new ProtocolDispatcher.CallbackServer() {
//        @Override
//        public int requestReceived(HttpRequest http_request) {
//            System.out.println("----------- Request -----------");
//            System.out.println("[" + http_request.szMethod + "] [" + http_request.szLocation + "] [" + http_request.szVersion + "]");
//
//            for (PStr option : http_request.options) { System.out.println("[" + option.key + "] : [" + option.value + "]"); }
//            System.out.println("--------------------------------");
//
//            return 0;
//        }
//    };



    public ProtocolDispatcher.CallbackSession getProtocolCallback() { return cb_session; }

    private ProtocolDispatcher.CallbackSession cb_session = new ProtocolDispatcher.CallbackSession() {
        @Override
        public byte[] onReceived(HttpRequest http_request, byte[] bytes) {
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

            return dispatch();
        }
    };




    // --------------------------------------------------------------------------------

    private byte[] dispatch() {

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

            // /favicon.ico
            if (request_header.lsPath.length == 2) {
                if (request_header.lsPath[1].equals("favicon.ico")) {
                    //System.out.println("must return favicon");
                    byte[] bytes = null;

                    try {
                        //System.out.println("user dir: " + System.getProperty("user.dir"));
                        FileInputStream fis = new FileInputStream("favicon.ico");
                        bytes = new byte[fis.available()];
                        int read_length = fis.read(bytes);
                    } catch (Exception e) { e.printStackTrace(); }

                    return bytes;
                }
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


        // ---- ---- //

        if (module.isEmpty())  module = "default";

        //Info mi = null;
        Module md = null;

        if (module.equals("elec")) {
            //mi = new ru.pyur.tst.elec.Info();
            md = new ru.pyur.tst.elec.Md_Elec(session);
        }

        else if (module.equals("water")) {
            //mi = new ru.pyur.tst.water.Info();
            md = new ru.pyur.tst.water.Md_Water(session);
        }

        else if (module.equals("db")) {
            md = new ru.pyur.tst.dbedit.Info(session).dispatch();
        }


        if (md != null) {
            md.prepare();
            return md.render().getBytes();
        }


        return null;
    }




    public ArrayList<PStr> getQuery() {
        return request_header.getQuery();
    }



}
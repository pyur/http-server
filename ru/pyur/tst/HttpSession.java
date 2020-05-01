package ru.pyur.tst;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static ru.pyur.tst.HttpHeader.HTTP_VERSION_1_0;


public class HttpSession {

    private HttpSession session;

    private HttpRequest request_header;

    private InputStream input_stream;
    private OutputStream output_stream;

    private String host;
    private String prefix;  // a - api, i - image, e - embed, etc
    public String module;
    public String action;
    private String last_argument;

    private final String doc_root = "files";



    public HttpSession() {
        session = this;
        //module = "";
        //action = "";
    }


    public HttpSession(HttpRequest http_request, InputStream is, OutputStream os) {
        session = this;
        request_header = http_request;
        input_stream = is;
        output_stream = os;
    }




    // --------------------------------------------------------------------------------

//    public Transport.CallbackTransportEvents getTransportCallback() { return cb_transport; }
//
//    private Transport.CallbackTransportEvents cb_transport = new Transport.CallbackTransportEvents() {
//        @Override
//        public byte[] onConnected() {
//            System.out.println("onConnected()");
//            return null;
//        }
//
//        @Override
//        public void onFailed() {
//            System.out.println("onFailed()");
//        }
//    };



//    public void setRequest(HttpRequest http_request) {
//        request_header = http_request;
//    }



    public ArrayList<PStr> getQuery() {
        return request_header.getQuery();
    }





    // --------------------------------------------------------------------------------

    //public DispatchedData dispatch(byte[] payload) {
    public void dispatch() throws Exception {

        // maybe dispatch path before receiving possibly huge payload
        byte[] payload = ProtocolDispatcher.receivePayload(input_stream, request_header);


        getHost();

        if (host.isEmpty()) {
            dispatchDefaultHost();
        }

        //else if (host.equals("mydomain.com")) {
        //    dispatchSpecificHost();
        //}

        else {
            dispatchDefaultHost();
        }

    }




    private void getHost() {  // throws Exception
        host = "";

        //ArrayList<PStr> options = request_header.getOptions();
        //if (request_header.hasOption("Host")) {
        try {
            host = request_header.getOption("Host");
        } catch (Exception e) { }
    }




    private void dispatchDefaultHost() throws Exception {

        try {
            dispatchPath();
        } catch (Exception e) {
            response400();
            return;
        }



        // ---- dispatch file ---- //

        if (!last_argument.isEmpty()) {
            //if (request_header.lsPath[1].equals("favicon.ico")) {
                //System.out.println("must return favicon");
            byte[] bytes = null;

            try {
                //System.out.println("user dir: " + System.getProperty("user.dir"));
                File file = new File(doc_root + request_header.getPath());
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    bytes = new byte[fis.available()];
                    fis.read(bytes);

                    ArrayList<PStr> opts = new ArrayList<>();
                    opts.add(new PStr("Content-Type", "image/x-icon"));
                    //Last-Modified: Wed, 21 Jan 2015 12:50:06 GMT
                    //ETag: "47e-50d28feb5fca8"
                    response200(bytes, opts);

//x                    bytes = new byte[131072];
//x                    int read_length;
//x                    while ((read_length = fis.read(bytes)) != -1) {
//x                        output_stream.write(bytes, 0, read_length);
//x                    }
//x                    output_stream.flush();
                }
                else {
                    System.out.println("file not exists: " + file.getPath());
                    response404("file not exists: " + file.getPath());
                }
            } catch (Exception e) {
                e.printStackTrace();
                response404("file i/o error.");
            }

            //return new DispatchedData(bytes);
            return;
        }


        // ---- dispatch prefix ---- //

        //if (module.isEmpty())  module = "default";

        if (prefix.isEmpty()) {
            processHtml();
        }

        else if (prefix.equals("a")) {
            processJson();
        }

        else if (prefix.equals("i")) {
            processImage();
        }

        else if (prefix.equals("e")) {
            processEmbed();
        }

        else if (prefix.equals("k")) {
            processKiosk();
        }

        else {
            // unknown prefix
            // throw error 400
        }


    }




    private void dispatchPath() throws Exception {
        prefix = "";
        module = "";
        action = "";
        last_argument = "";

        int path_length = request_header.lsPath.length;

        if (!request_header.lsPath[path_length - 1].isEmpty())  last_argument = request_header.lsPath[path_length - 1];


        if (path_length < 2) {
            // response 400
            throw new Exception("path length less than 2");
        }

//        else if (path_length == 2) {
            // only '/'. root
//            if (request_header.lsPath[1].isEmpty()) {
//                //processHtml();
//            }
//
//            // '/file'. root
//            else {
//                last_empty = false;
//            }
//        }


        // '/module/'
        // '/a/' api
        // '/e/' embed
        // '/i/' image
        // '/k/' kiosk
        else {  // request_header.lsPath.length > 2
            if (request_header.lsPath[1].length() == 1) {
                prefix = request_header.lsPath[1];

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


            else {
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

        }

    }



/*
    private void composeResponse() {
        // ---- compose response ---- //
        HttpResponse response = new HttpResponse();
        response.setConnectionClose();
        response.setVersion(HTTP_VERSION_1_0);

        if (feedback != null) {
            response.setCode(feedback.code);

            response.addOptions(feedback.options);

            //todo "Server: string"

            if (feedback.payload != null) {
                response.appendPayload(feedback.payload);
            }
        }

        return response.stringify();
    }
*/




    // -------------------------------- Html module -------------------------------- //

    private void processHtml() {
        HttpModule md = null;

        if (module.isEmpty()) {
            //todo: default page
        }

        else if (module.equals("elec")) {
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

        else if (module.equals("res")) {
            md = new ru.pyur.tst.resources.Info(session).dispatch();
        }

        else if (module.equals("ws")) {
            md = new ru.pyur.tst.websocket.Info(session).dispatch();
        }

        else if (module.equals("battleship")) {
            md = new ru.pyur.tst.battleship.Info(session).dispatch();
        }

        //else if (module.equals("ext")) {
        //    new ru.pyur.tst.extsample.ExtMod();
        //}


        if (md != null) {
            byte[] contents = md.getContents();
            byte[] compressed_contents = null;

            ArrayList<PStr> response_options = md.getOptions();
/*
            // compression
            //if (header has "Accept-Encoding: gzip, deflate, br") {

            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                GZIPOutputStream gos = new GZIPOutputStream(os);
                gos.write(contents);
                gos.close();
                compressed_contents = os.toByteArray();
            } catch (Exception e) { e.printStackTrace(); }

            if (compressed_contents != null) {
                contents = compressed_contents;
                response_options.add(new PStr("Content-Encoding", "gzip"));
            }
*/
            //return new DispatchedData(contents, response_options, 200);
            response200(contents, response_options);
        }

    }




    // -------------------------------- Json module -------------------------------- //

    private void processJson() {

    }




    // -------------------------------- Kiosk module -------------------------------- //

    private void processKiosk() {

    }




    // -------------------------------- Image module -------------------------------- //

    private void processImage() {

    }








    // -------------------------------- Embed module -------------------------------- //

    private void processEmbed() {

    }




    private void response200(byte[] contents, ArrayList<PStr> response_options) {
        HttpSessionResponse response = new HttpSessionResponse();
        response.appendPayload(contents);
        response.addOptions(response_options);

        send(response.stringify());
    }



    private void response400() {
        HttpSessionResponse response = new HttpSessionResponse(400);
        send(response.stringify());
    }



    private void response404(String message) {
        HttpSessionResponse response = new HttpSessionResponse(404);
        response.appendPayload(message);
        send(response.stringify());
    }




    private int send(byte[] bytes) {
        //System.out.println("---- HttpSession. Send -----------------------------------------");
        //System.out.println(new String(bytes));
        //System.out.println("----------------------------------------------------------------");

        try {
            output_stream.write(bytes);
            output_stream.flush();
        } catch (Exception e) { e.printStackTrace(); return -1; }

        return 0;
    }

}
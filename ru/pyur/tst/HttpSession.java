package ru.pyur.tst;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class HttpSession {

    private HttpSession session;

    private HttpRequest request_header;
    private byte[] payload;

    private HttpResponse response_header = new HttpResponse();
    //private byte[] response_payload;

    private InputStream input_stream;
    private OutputStream output_stream;

    private DbManager db_manager;

    private Auth auth;

    private String host;
    private String prefix;  // a - api, i - image, e - embed, etc
    private String module;
    private String action;
    private String last_argument;

    private final String doc_root = "files";



    // ---------------- Database ---------------- //





    public interface ControlSession {
        void setOption(String name, String value);
        void setCookie(String name, String value, int expires, String path);
    }

    private ControlSession co_session = new ControlSession() {
        @Override
        public void setOption(String name, String value) {
            response_header.addOption(name, value);
        }

        @Override
        public void setCookie(String name, String value, int expires, String path) {
            response_header.addCookie(name, value, expires, path);
        }
    };





//    public HttpSession() {
//        session = this;
//        //module = "";
//        //action = "";
//    }


    public HttpSession(HttpRequest http_request, InputStream is, OutputStream os) {
        session = this;
        request_header = http_request;
        input_stream = is;
        output_stream = os;

        response_header.setVersion(HttpHeader.HTTP_VERSION_1_1);
    }




    // --------------------------------------------------------------------------------

//    public void setRequest(HttpRequest http_request) {
//        request_header = http_request;
//    }


    public DbManager getDbManager() { return db_manager; }

    public ArrayList<PStr> getQuery() {
        return request_header.getQuery();
    }

    public String getModule() { return module; }

    public String getAction() { return action; }

    public byte[] getPayload() { return payload; }


    public void setModule(String module) { this.module = module; }

    public void setAction(String action) { this.action = action; }





    // ---- Entry point ------------------------------------------------------------

    public void dispatch() {

        try {
            // maybe getHtml path before receiving possibly huge payload
            payload = ProtocolDispatcher.receivePayload(input_stream, request_header);
        } catch (Exception e) { e.printStackTrace(); }

        db_manager = new DbManager();
        db_manager.connectDb();
        db_manager.connectConfigDb();

        host = "";

        //ArrayList<PStr> options = request_header.getOptions();
        //if (request_header.hasOption("Host")) {
        try {
            host = request_header.getOption("Host");
        } catch (Exception e) { }


        if (host.isEmpty()) {
            dispatchDefaultHost();
        }

        //else if (host.equals("mydomain.com")) {
        //    dispatchSpecificHost();
        //}

        else {
            dispatchDefaultHost();
        }

        db_manager.closeDb();
        db_manager.closeConfig();
    }




    //maybe create class Host, VirtualHost
    private void dispatchDefaultHost() {

        // -------- determine module, action -------- //
        try {
            dispatchPath();
        } catch (Exception e) {
            response400();
            return;
        }


        // -------- user authorization -------- //

        auth = new Auth(db_manager, co_session);
        try {
            auth.authByCookie(request_header);
        } catch (Exception e) {
            //response401();
            //return;
            // -- force redirect to auth form -- //
            //setModule("auth");
            //setAction("");
        }

        //modules = auth.getModules();



        // -------- getHtml file -------- //

        if (!last_argument.isEmpty()) {
            try {
                //System.out.println("user dir: " + System.getProperty("user.dir"));
                File file = new File(doc_root + request_header.getPath());
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    byte[] bytes = new byte[fis.available()];
                    fis.read(bytes);
                    fis.close();

                    ArrayList<PStr> opts = new ArrayList<>();
                    opts.add(new PStr("Content-Type", "image/x-icon"));
                    //Last-Modified: Wed, 21 Jan 2015 12:50:06 GMT
                    //ETag: "47e-50d28feb5fca8"
                    response200(bytes, opts);
                }
                else {
                    System.out.println("file not exists: " + file.getPath());
                    response404("file not exists: " + file.getPath());
                }
            } catch (Exception e) {
                e.printStackTrace();
                response404("file i/o error.");
            }

            return;
        }


        // ---- getHtml prefix ---- //

        if (prefix.isEmpty()) {
            processHtml();
        }

        else if (prefix.equals("a")) {
            processApi();
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
            response404("unknown prefix");
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




    private ModuleInfo getModuleInfo() {
        ModuleInfo module_info = null;

        if (module.isEmpty()) {
            //todo: default page
            module_info = new ru.pyur.tst.default_module.Info(session);
        }

        else if (module.equals("auth")) {
            module_info = new ru.pyur.tst.auth.Info(session);
        }

        else if (module.equals("elec")) {
            //mi = new ru.pyur.tst.elec.Info();
            //html_content = new ru.pyur.tst.elec.Md_Elec(session);  // rewrite
        }

        else if (module.equals("water")) {
            //mi = new ru.pyur.tst.water.Info();
            //html_content = new ru.pyur.tst.water.Md_Water(session);  // rewrite
        }

        else if (module.equals("db")) {
            module_info = new ru.pyur.tst.dbedit.Info(session);
        }

        else if (module.equals("res")) {
            module_info = new ru.pyur.tst.resources.Info(session);
        }

        else if (module.equals("ws")) {
            module_info = new ru.pyur.tst.websocket.Info(session);
        }

        else if (module.equals("battleship")) {
            module_info = new ru.pyur.tst.battleship.Info(session);
        }

        //else if (module.equals("ext")) {
        //    module_info = new ru.pyur.tst.extsample.ExtMod();
        //}

        return module_info;
    }




    // -------------------------------- Html module -------------------------------- //

    private void processHtml() {
        ModuleInfo module_info = getModuleInfo();

        if (module_info == null) {
            response404("no such module \"" + module + "\".");
            return;
        }


        HtmlContent html_content = module_info.getHtml();

        if (html_content == null) {
            response404("module \"" + module + "\" lack html support");
            return;
        }


        byte[] content = html_content.getContent();
        byte[] compressed_content = null;

        ArrayList<PStr> response_options = html_content.getOptions();
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
        response200(content, response_options);
        //response(content, code, response_options);
    }




    // -------------------------------- Api module -------------------------------- //

    private void processApi() {
        ModuleInfo module_info = getModuleInfo();

        if (module_info == null) {
            response404("no such module \"" + module + "\".");
            return;
        }


        ApiContent api_content = module_info.getApi();

        if (api_content == null) {
            response404("module \"" + module + "\" lack html support");
            return;
        }


        byte[] content = api_content.getContent();  // put payload in passed arguments
        byte[] compressed_content = null;

        ArrayList<PStr> response_options = api_content.getOptions();


        response200(content, response_options);
        //response(content, code, response_options);
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




    // ---- Responses ------------------------------------------------------------

    private void response200(byte[] contents, ArrayList<PStr> response_options) {
//x        HttpSessionResponse response = new HttpSessionResponse();
        response_header.setCode(200);
        response_header.appendPayload(contents);
        response_header.addOptions(response_options);

        send(response_header.stringify());
    }



    private void response400() {
//x        HttpSessionResponse response = new HttpSessionResponse(400);
        response_header.setCode(400);
        send(response_header.stringify());
    }



    private void response404(String message) {
//x        HttpSessionResponse response = new HttpSessionResponse(404);
        response_header.setCode(404);
        response_header.appendPayload(message);
        send(response_header.stringify());
    }




    private void send(byte[] bytes) {
        //System.out.println("---- HttpSession. Send -----------------------------------------");
        //System.out.println(new String(bytes));
        //System.out.println("----------------------------------------------------------------");

        try {
            output_stream.write(bytes);
            output_stream.flush();
        } catch (Exception e) { e.printStackTrace(); }
    }



}
package ru.pyur.tst;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;


public abstract class ModularHost extends Host {

    private Connection db_host;
    private Connection db_host_config;
    private Connection db_sess;
    private Connection db_user;
    private Connection db_adm_user;

    protected Auth auth;

    protected String prefix;  // a - api, i - image, e - embed, etc
    protected String module;
    protected String action;
    protected String last_argument;

    protected String doc_root;




    // ---- prototypes ------------------------------------------------------------

//    protected void processHtml() {};
//
//    protected void processApi() {};
//
//    protected void processImage() {};
//
//    protected void processEmbed() {};
//
//    protected void processKiosk() {};


//    protected void processWebsocket() {};
//
//    protected void processCast() {};


    // ----------------

    protected abstract String getHostDir();

    protected String getDocRoot() { return null; }

    protected ModuleInfo getModuleInfo(String module_name) { return null; }

    // ---- db ----
    protected Connection connectHostDb() { return null; }

    protected Connection connectHostConfig() { return null; }

    protected Connection connectSessDb() { return null; }

    protected Connection connectUserDb() { return null; }

    protected Connection connectAdmUserDb() { return null; }




    // --------------------------------------------------------------------------------

    //protected void setDocRoot(String path) { doc_root = path; }

//    public DbManager getDbManager() { return db_manager; }


    public String getModule() { return module; }

    public String getAction() { return action; }


    public Connection getDb() { return db_host; }

    public Connection getConfigDb() { return db_host_config; }

    public Connection getSessDb() { return db_sess; }

    public Connection getUserDb() { return db_user; }

    public Connection getAdmUserDb() { return db_adm_user; }




    // ---- Entry point ------------------------------------------------------------

    @Override
    public void dispatch() {

        // -------- determine module, action -------- //
        try {
            dispatchPath();
        } catch (Exception e) {
            response400();
            return;
        }



        if (isWebsocket()) {
            processWebsocket();
            return;
        }


        if (isCast()) {
            processCast();
            return;
        }



        // ---------------- getHtml file ---------------- //

        doc_root = getDocRoot();

        if (doc_root != null && !last_argument.isEmpty()) {
            try {
                //System.out.println("user dir: " + System.getProperty("user.dir"));
                if (!request_header.getPath().equals("/favicon.ico"))  throw new Exception("file name strict.");
                File file = new File(doc_root + request_header.getPath());
//todo                File file = new File(doc_root + "/../../txt");
                System.out.println("file: " + file.getAbsolutePath());
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    byte[] bytes = new byte[fis.available()];
                    fis.read(bytes);
                    fis.close();

                    addOption("Content-Type", "image/x-icon");
                    //Last-Modified: Wed, 21 Jan 2015 12:50:06 GMT
                    //ETag: "47e-50d28feb5fca8"
                    response(bytes);
                }
                else {
                    //System.out.println("file not exists: " + file.getPath());
                    response404("file not exists: " + file.getPath());
                }
            } catch (Exception e) {
                //e.printStackTrace();
                response404("file i/o error.");
            }

            return;
        }



        // -------- open database -------- //

        db_host = connectHostDb();
        db_host_config = connectHostConfig();
        db_sess = connectSessDb();
        db_user = connectUserDb();
        db_adm_user = connectAdmUserDb();



        // -------- user authorization -------- //

        auth = new Auth(db_sess, db_user, db_adm_user, response_header);
        try {
            auth.authByCookie(request_header);
        } catch (Exception e) {
            e.printStackTrace();
            //response401();
            //return;
            // -- force redirect to auth form -- //
            //setModule("auth");
            //setAction("");
        }
        //System.out.println("auth state: " + auth.state);

        //modules = auth.getModules();



        // -------- receive payload -------- //

        try {
            receivePayload();
        } catch (Exception e) { e.printStackTrace(); }



        // -------- prefix -------- //

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



        // ---- close database ---- //

        try {
            if (db_host != null)  db_host.close();
            if (db_host_config != null)  db_host_config.close();
            if (db_sess != null)  db_sess.close();
            if (db_user != null)  db_user.close();
            if (db_adm_user != null)  db_adm_user.close();
        } catch (Exception e) { e.printStackTrace(); }
    }




    // --------------------------------------------------------------------------------

    protected void dispatchPath() throws Exception {
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
                if (path_length == 4) {
                    module = request_header.lsPath[2];
                }

                // '/?/module/action/'
                else if (path_length == 5) {
                    module = request_header.lsPath[2];
                    action = request_header.lsPath[3];
                }
            }


            else {
                // '/module/'
                if (path_length == 3) {
                    module = request_header.lsPath[1];
                }

                // '/module/action/'
                else if (path_length == 4) {
                    module = request_header.lsPath[1];
                    action = request_header.lsPath[2];
                }
            }

        }

    }




    // -------------------------------- Html module -------------------------------- //

    private void processHtml() {
        ModuleInfo module_info = getModuleInfo(module);

        if (module_info == null) {
            response404("no such module \"" + module + "\".");
            return;
        }


        HtmlContent html_content = module_info.getHtml(getAction());

        if (html_content == null) {
            response404("module \"" + module + "\" lack html support. or no action \"" + getAction() + "\".");
            return;
        }

        html_content.init(this);


        byte[] content = html_content.makeContent();

/*
        // compression
        //if (header has "Accept-Encoding: gzip, deflate, br") {

        byte[] compressed_content = null;
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
        response(content);
    }




    // -------------------------------- Api module -------------------------------- //

    private void processApi() {
        ModuleInfo module_info = getModuleInfo(module);

        if (module_info == null) {
            response404("no such module \"" + module + "\".");
            return;
        }


        ApiContent api_content = module_info.getApi(getAction());

        if (api_content == null) {
            response404("module \"" + module + "\" lack api support.");
            return;
        }

        api_content.init(this);


        byte[] content = api_content.makeContent();

        // todo: compression

        response(content);
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






    // -------------------------------- Websocket module -------------------------------- //

    private void processWebsocket() {
        String ws_key;
        //try {
        ws_key = request_header.getOption("Sec-WebSocket-Key");
        //} catch (Exception e) {
        //    e.printStackTrace();
        if (ws_key == null) {
            response404("missing required websocket key");
            return;
        }



        // -------- get module_info -------- //

        ModuleInfo module_info = getModuleInfo(module);

        if (module_info == null) {
            response404("no such module \"" + module + "\".");
            return;
        }


        WebsocketDispatcher wsd = module_info.getWs(getAction());

        if (wsd == null) {
            response404("module \"" + module + "\" lack websocket support. or wrong action\"" + getAction() + "\"");
            return;
        }

        wsd.init(this);


        responseSwitchingOk(ws_key);

        // switch input and output streams to websocket protocol

        wsd.dispatch();
    }




    // -------------------------------- Cast module -------------------------------- //

    private void processCast() {

    }

}
package ru.pyur.tst.sample_host;

import ru.pyur.tst.*;
import ru.pyur.tst.db.DbManager;

import java.io.File;
import java.io.FileInputStream;


public class SampleHost extends ModularHost {


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



//x        System.out.println("probing for websocket");
        if (isWebsocket()) {
//x            System.out.println("...probing success.");
            processWebsocket();
            return;
        }
//x        System.out.println("...probing failed.");


        if (isCast()) {
            processCast();
            return;
        }



        // ---------------- getHtml file ---------------- //

        if (!last_argument.isEmpty()) {
            try {
                //System.out.println("user dir: " + System.getProperty("user.dir"));
                File file = new File(doc_root + request_header.getPath());
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    byte[] bytes = new byte[fis.available()];
                    fis.read(bytes);
                    fis.close();

                    //ArrayList<PStr> opts = new ArrayList<>();
                    //opts.add(new PStr("Content-Type", "image/x-icon"));
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

        db_manager = new DbManager();
        db_manager.connectDb();
        db_manager.connectConfigDb();



        // -------- user authorization -------- //

        auth = new Auth(db_manager, response_header);
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
            //payload = ProtocolDispatcher.receivePayload(input_stream, request_header);
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

        db_manager.closeDb();
        db_manager.closeConfig();
    }




    // -------------------------------- Html module -------------------------------- //

    private void processHtml() {
        ModuleInfo module_info = ModulesManager.getModuleInfo(module);

        if (module_info == null) {
            response404("no such module \"" + module + "\".");
            return;
        }

//        module_info.setHttpSession(session);


        HtmlContent html_content = module_info.getHtml(getAction());

        if (html_content == null) {
            response404("module \"" + module + "\" lack html support. or no action \"" + getAction() + "\"");
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
        ModuleInfo module_info = ModulesManager.getModuleInfo(module);

        if (module_info == null) {
            response404("no such module \"" + module + "\".");
            return;
        }

//        module_info.setHttpSession(session);


        ApiContent api_content = module_info.getApi(getAction());

        if (api_content == null) {
            response404("module \"" + module + "\" lack html support");
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

        ModuleInfo module_info = ModulesManager.getModuleInfo(module);

        if (module_info == null) {
            response404("no such module \"" + module + "\".");
            return;
        }

//        module_info.setWebsocketSession(this);


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
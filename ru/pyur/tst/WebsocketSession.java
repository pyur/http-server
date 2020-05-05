package ru.pyur.tst;

import ru.pyur.tst.db.DbManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class WebsocketSession {

    private HttpRequest request_header;
    private HttpResponse response_header;

    private InputStream input_stream;
    private OutputStream output_stream;

    private DbManager db_manager;

    private Auth auth;

    private String host;  // option "Host" in http header

    public String module;
    public String action;

    private String ws_key;





    public WebsocketSession(HttpRequest http_request, InputStream is, OutputStream os) {
        request_header = http_request;
        input_stream = is;
        output_stream = os;

        response_header = new HttpResponse();
        response_header.setVersion(HttpHeader.HTTP_VERSION_1_1);
        response_header.setCode(101);
    }




    // --------------------------------------------------------------------------------

    public DbManager getDbManager() { return db_manager; }

    public String getAction() { return action; }

    public InputStream getInputStream() { return input_stream; }

    public OutputStream getOutputStream() { return output_stream; }




    // ---- Entry point ------------------------------------------------------------

    public void dispatch() {  // throws Exception {

        db_manager = new DbManager();
        db_manager.connectDb();
        db_manager.connectConfigDb();


        host = "";

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




    private void dispatchDefaultHost() {

//        int result = validate();  // try/catch
//        if (result == -1) {
//            Http_SendReferenceWsResponseFailed();
//            return;
//        }


        //String ws_key;
        try {
            ws_key = request_header.getOption("Sec-WebSocket-Key");
        } catch (Exception e) {
            e.printStackTrace();
            response404("missing websocket key");
            return;
        }

//        byte[] response = ProtocolDispatcher.Http_SendReferenceWsResponseOk(ws_key);
//
//        try {
//            os.write(response);
//            os.flush();
//        } catch (Exception e) { e.printStackTrace(); }



        try {
            dispatchPath();
        } catch (Exception e) {
            response404(e.getMessage());
            return;
        }


        // -------- user authorization -------- //

        auth = new Auth(db_manager, response_header);
        try {
            auth.authByCookie(request_header);
        } catch (Exception e) {
            e.printStackTrace();
//todo            response401();
//            return;
            // -- force redirect to auth form -- //
            //setModule("auth");
            //setAction("");
        }
        //System.out.println("auth state: " + auth.state);

        //modules = auth.getModules();



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

        wsd.setSession(this);


        responseSwitchingOk();

        // switch input and output streams to websocket protocol

        wsd.dispatch();
    }




    private void dispatchPath() throws Exception {
        module = "";
        action = "";

        int path_length = request_header.lsPath.length;


        if (path_length < 2) {
            // response 400
            throw new Exception("path length less than 2");
        }


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


    }




//    public int validate() {
//        System.out.println("WebsocketSession. validate()");
//        return 1;
//    }




    // ---- Responses ------------------------------------------------------------

    private void responseSwitchingOk() {
        //response_header.appendPayload(contents);
        response_header.addOption("Upgrade", "websocket");
        response_header.addOption("Connection", "Upgrade");

        // ---- calc hash ---- //
        String ws_key_resp = ws_key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

        byte[] sha1 = null;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(ws_key_resp.getBytes());  // getBytes("UTF-8")
            sha1 = crypt.digest();
        } catch(NoSuchAlgorithmException e) { e.printStackTrace(); }

        String szKey64 = new String (Base64.getEncoder().encode(sha1));

        response_header.addOption("Sec-WebSocket-Accept", szKey64);

        send(response_header.stringify());
    }



    private void response400() {
        response_header.setCode(400);
        send(response_header.stringify());
    }



    private void response404(String message) {
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
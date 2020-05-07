package ru.pyur.tst;

import ru.pyur.tst.sample_host.SampleHost;

import java.io.InputStream;
import java.io.OutputStream;


public class HttpSession {  // todo: rename to HostDispatcher, HostsManager

//x    private HttpRequest request_header;
//x    private byte[] payload;

//x    private HttpResponse response_header;

//x    private InputStream input_stream;
//x    private OutputStream output_stream;

//x    private DbManager db_manager;

//x    private Auth auth;

//    private String host_name;
//x    private String prefix;  // a - api, i - image, e - embed, etc
//x    private String module;
//x    private String action;
//x    private String last_argument;

//x    private final String doc_root = "files";



    // ---------------- session controller ---------------- //

//    private ControlSession co_session = new ControlSession() {
//        @Override
//        public void setCode(int code) { response_header.setCode(code); }
//
//        @Override
//        public void addOption(String name, String value) {
//            response_header.addOption(name, value);
//        }
//
//        @Override
//        public void setCookie(String name, String value, int expires, String path) {
//            response_header.setCookie(name, value, expires, path);
//        }
//    };




//x    public HttpSession(HttpRequest http_request, InputStream is, OutputStream os) {
//x        request_header = http_request;
//x        input_stream = is;
//x        output_stream = os;
//x
//x        response_header = new HttpResponse();
//x        response_header.setVersion(HttpHeader.HTTP_VERSION_1_1);
//x        response_header.setCode(200);
//x    }




    // --------------------------------------------------------------------------------

//x    public DbManager getDbManager() { return db_manager; }

//x    public ArrayList<PStr> getQuery() { return request_header.getQuery(); }

//x    public HttpResponse getResponseHeader() { return response_header; }

//x    public String getModule() { return module; }

//x    public String getAction() { return action; }

//x    public byte[] getPayload() { return payload; }


//x    public void setModule(String module) { this.module = module; }

//x    public void setAction(String action) { this.action = action; }


    // ---- for control response from modules ----------------

//x    public void setCode(int code) { response_header.setCode(code); }

//x    public void addOption(String name, String value) { response_header.addOption(name, value); }

//x    public void setCookie(String name, String value, int expires, String path) { response_header.setCookie(name, value, expires, path); }




    // ---- Entry point ------------------------------------------------------------
/*
    public static void dispatch(HttpRequest request_header, InputStream input_stream, OutputStream output_stream) {

        String host_name = "";
        Host host = null;

        //ArrayList<PStr> options = request_header.getOptions();
        //if (request_header.hasOption("Host")) {
        try {
            host_name = request_header.getOption("Host");
        } catch (Exception e) { }


        //if (host_name.isEmpty()) {
        //    host = new EmptyHost();
        //}

        //else if (host.equals("dbadmin.vtof.ru")) {
        //    host = new DbAdminHost();
        //}

        //else {
        host = new SampleHost();
        //    host = new OnlyIpHost();
        //}


        if (host != null) {
            host.init(request_header, input_stream, output_stream);
            host.dispatch();
        }

    }
*/

}
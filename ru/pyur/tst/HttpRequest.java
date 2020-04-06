package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class HttpRequest extends HttpHeader {

    String szMethod;
    String szLocation;
    String szVersion;

    int method;
    int version;

    String szPath;  // reserved
    String[] lsPath;

    String szQuery;  // reserved
    ArrayList<PStr> lsQuery;



    // ---- known options ---- //

    //Str host;
    //Str user_agent;
    //Str connection;
    //Str content_type;

    ArrayList<PStr> cookies;

    int content_length;
    boolean dont_use_content_length;  // disabler

    //String szPayload;
    ByteArrayOutputStream exPayload;
    //Blob szBinaryPayload;
    //size_t BinaryPayloadSize;


    public HttpRequest() {
        options = new ArrayList<>();
    }



    public void parse(byte[] bytes) throws Exception {
        //System.out.println("HttpRequest. parse()");
        if (bytes.length == 0)  throw new Exception("header zero length");

        String data = new String(bytes);

        String[] list = Util.explode("\r\n", data);

        //System.out.println("----------------------------------------------------------------");
        //for (String str : list) { System.out.println("[" + str + "]"); }
        //System.out.println("----------------------------------------------------------------");

        String[] request = Util.explode(' ', list[0]);

        //System.out.println("----------------------------------------------------------------");
        //for (String str : request) { System.out.println("[" + str + "]"); }
        //System.out.println("----------------------------------------------------------------");

        if (request.length != 3)  throw new Exception("request length != 3");

        szMethod = request[0];
        szLocation = request[1];
        szVersion = request[2];

        method = 0;  // todo
        version = 0;  // todo

        PStr path_split = Util.split('?', szLocation);

        szPath = path_split.key;
        lsPath = Util.explode('/', szPath);

        szQuery = path_split.value;
        if (!szQuery.isEmpty()) {
            String[] explode_query = Util.explode('&', szQuery);
            lsQuery = new ArrayList<>();
            for (String q : explode_query) {
                lsQuery.add(Util.split('=', q));
            }
        }


        // ---------------- parse options ---------------- //

        parseOptions(list);

        //System.out.println("----------------------------------------------------------------");
        //for (PStr option : options) { System.out.println("[" + option.key + "] : [" + option.value + "]"); }
        //System.out.println("----------------------------------------------------------------");

    }



}
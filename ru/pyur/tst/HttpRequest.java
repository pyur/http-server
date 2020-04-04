package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class HttpRequest extends HttpHeader {

    String szMethod;
    String szRequest;
    String szVersion;

    int method;
    int version;

    String szPath;  // reserved
    String[] lsPath;

    String szQuery;  // reserved
    ArrayList<PStr> lsQuery;


    ArrayList<PStr> options;

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


    public HttpRequest() {}

    public HttpRequest(byte[] data) { parse(data); }


    public void parse(byte[] bytes) {
        //System.out.println("parsing...");
        if (bytes.length == 0)  return;  // todo: throw

        String data = new String(bytes);

        String[] list = Util.explode("\r\n", data);

        //System.out.println("----------------------------------------------------------------");
        //for (String str : list) { System.out.println("[" + str + "]"); }
        //System.out.println("----------------------------------------------------------------");

        String[] request = Util.explode(' ', list[0]);

        //System.out.println("----------------------------------------------------------------");
        //for (String str : request) { System.out.println("[" + str + "]"); }
        //System.out.println("----------------------------------------------------------------");

        // todo: if request.length != 3, Throw exception

        szMethod = request[0];
        szRequest = request[1];
        szVersion = request[2];

        method = 0;  // todo
        version = 0;  // todo

        szPath = null;  // todo
        lsPath = null;  // todo

        szQuery = null;  // todo
        lsQuery = null;  // todo


        // ---------------- parse remaining lines ---------------- //

        options = new ArrayList<>();

        for (int i = 1; i < list.length; i++) {
            PStr option = Util.split(':', list[i]);
            option.key = option.key.trim();
            option.value = option.value.trim();

            //todo: lower-case keys. maybe not pair, but special struct

            options.add(option);
        }

        //System.out.println("----------------------------------------------------------------");
        //for (PStr option : options) { System.out.println("[" + option.key + "] : [" + option.value + "]"); }
        //System.out.println("----------------------------------------------------------------");

    }



}
package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class HttpRequest extends HttpHeader {

    public String szMethod;
    public String szLocation;
    public String szVersion;

    private int method;
    private int version;

    private String szPath;  // parse, stringify
    public String[] lsPath;  // parse

    private String szQuery;  // reserved
    private ArrayList<PStr> lsQuery;



    // ---- known options ---- //

    //Str host;
    //Str user_agent;
    //Str connection;
    //Str content_type;

    private ArrayList<PStr> cookies;

    private int content_length;
    private boolean dont_use_content_length;  // disabler

    //String szPayload;
    private ByteArrayOutputStream payload;
    //Blob szBinaryPayload;
    //size_t BinaryPayloadSize;


    private final int HTTP_METHOD_Unknown = 0;
    private final int HTTP_METHOD_GET = 1;
    private final int HTTP_METHOD_POST = 2;
    private final int HTTP_METHOD_HEAD = 3;
    private final int HTTP_METHOD_OPTIONS = 4;
    private final int HTTP_METHOD_PUT = 5;
    private final int HTTP_METHOD_DELETE = 6;
    private final int HTTP_METHOD_PATCH = 7;
    private final int HTTP_METHOD_CONNECT= 8;
    private final int HTTP_METHOD_TRACE = 9;


    private String szHttpMethod[] = {
            "Unknown",
            "GET", "POST",
            "HEAD", "OPTIONS",
            "PUT", "DELETE", "PATCH",
            "CONNECT", "TRACE"
    };







    public HttpRequest() {
        //options = new ArrayList<>();
        cookies = new ArrayList<>();
        payload = new ByteArrayOutputStream();
    }


/*
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
*/



    public void defaultGet(String path, String host) {
        method = HTTP_METHOD_GET;
        szPath = path;
        version = HTTP_VERSION_1_1;

        options.add(new PStr("Host", host));
    }




    public byte[] stringify() {
        StringBuilder sb = new StringBuilder();

        // ---------------- first line ---------------- //

        sb.append(szHttpMethod[method]);

        sb.append(" ");

        sb.append(szPath);

        sb.append(" ");

        sb.append(szHttpVersion[version]);

        sb.append("\r\n");


        // ---------------- options ---------------- //

        if (options != null && options.size() > 0) {
            //Str szFlat;

            //for (Ui idx = 0; rs->options[idx]; idx++) {
            for (PStr opt : options) {
                //r szFlat = Pair_Join(rs->options[idx], ": ");
                sb.append(opt.key);
                sb.append(": ");
                sb.append(opt.value);

                //r Expandable_AppendString(exResponse, szFlat);
                //r String_Destroy(szFlat);
                //r Expandable_AppendString(exResponse, "\r\n");
                sb.append("\r\n");
            }
        }



        // ---------------- Content-Length ---------------- //

        if (payload != null && payload.size() > 0) {
            //r Expandable_AppendString(exResponse, "Content-Length: ");
            //r Expandable_AppendString(exResponse, String_FromInt(strlen(rs->szPayload)));
            //r Expandable_AppendString(exResponse, "\r\n");
            sb.append("Content-Length: ");
            sb.append(payload.size());
            sb.append("\r\n");
        }

        //else if (other types of payload) {}

        // Content-Length: 0



        // ---------------- Cookies ---------------- //

        // todo



        // ---------------- header end ---------------- //

        sb.append("\r\n");



        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try { os.write(sb.toString().getBytes()); } catch (Exception e) { e.printStackTrace(); }



        // ---------------- payload ---------------- //

        if (payload != null && payload.size() > 0) {
            try { os.write(payload.toByteArray()); } catch (Exception e) { e.printStackTrace(); }
        }


        // ---------------- inflate ---------------- //

        return os.toByteArray();
    }




    // ---------------- getters, setters ------------------------------------------------

    public ArrayList<PStr> getQuery() {
        return lsQuery;
    }


    public String getPath() { return szPath; }




    // ---------------- base class ------------------------------------------------

    @Override
    public void setFirstLine(String first_line) throws Exception {
        String[] request = Util.explode(' ', first_line);

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

    }


}
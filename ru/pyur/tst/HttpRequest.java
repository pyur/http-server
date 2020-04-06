package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class HttpRequest extends HttpHeader {

    public String szMethod;
    public String szLocation;
    public String szVersion;

    private int method;
    private int version;

    private String szPath;  // reserved
    public String[] lsPath;

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


    private final int HTTP_VERSION_Unknown = 0;
    private final int HTTP_VERSION_0_9 = 1;
    private final int HTTP_VERSION_1_0 = 2;
    private final int HTTP_VERSION_1_1 = 3;


    private String szHttpMethod[] = {
            "Unknown",
            "GET", "POST",
            "HEAD", "OPTIONS",
            "PUT", "DELETE", "PATCH",
            "CONNECT", "TRACE"
    };



    private String szHttpVersion[] = {
            "Unknown",
            "HTTP/0.9",
            "HTTP/1.0",
            "HTTP/1.1"
    };



    class HttpCode_t {
        int code;
        String desc;

        public HttpCode_t(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }


    HttpCode_t[] HttpCode = {
        new HttpCode_t(100, "Continue"),
        new HttpCode_t(101, "Switching Protocols"),
        new HttpCode_t(102, "Processing"),

        new HttpCode_t(200, "OK"),
        new HttpCode_t(201, "Created"),
        new HttpCode_t(202, "Accepted"),
        new HttpCode_t(203, "Non-Authoritative Information"),
        new HttpCode_t(204, "No Content"),
        new HttpCode_t(205, "Reset Content"),
        new HttpCode_t(206, "Partial Content"),
        new HttpCode_t(207, "Multi-Status"),
        new HttpCode_t(208, "Already Reported"),
        new HttpCode_t(226, "IM Used"),

        new HttpCode_t(300, "Multiple Choices"),
        new HttpCode_t(301, "Moved Permanently"),
        //{ 302, "Moved Temporarily" },  // HTTP 1.0
        new HttpCode_t(302, "Found"),  // HTTP 1.1
        new HttpCode_t(303, "See Other"),
        new HttpCode_t(304, "Not Modified"),
        new HttpCode_t(305, "Use Proxy"),
        new HttpCode_t(306, "Switch Proxy"),  // skipped in apache
        new HttpCode_t(307, "Temporary Redirect"),
        new HttpCode_t(308, "Permanent Redirect"),

        new HttpCode_t(400, "Bad Request"),
        new HttpCode_t(401, "Unauthorized"),
        new HttpCode_t(402, "Payment Required"),
        new HttpCode_t(403, "Forbidden"),
        new HttpCode_t(404, "Not Found"),
        new HttpCode_t(405, "Method Not Allowed"),
        new HttpCode_t(406, "Not Acceptable"),
        new HttpCode_t(407, "Proxy Authentication Required"),
        new HttpCode_t(408, "Request Timeout"),
        new HttpCode_t(409, "Conflict"),
        new HttpCode_t(410, "Gone"),
        new HttpCode_t(411, "Length Required"),
        new HttpCode_t(412, "Precondition Failed"),
        new HttpCode_t(413, "Payload Too Large"),  // former "Request Entity Too Large"
        new HttpCode_t(414, "URI Too Long"),  // former "Request-URI Too Long" previously.
        new HttpCode_t(415, "Unsupported Media Type"),
        new HttpCode_t(416, "Range Not Satisfiable"),  // "Requested Range Not Satisfiable"
        new HttpCode_t(417, "Expectation Failed"),
        new HttpCode_t(418, "I'm a teapot"),
        new HttpCode_t(421, "Misdirected Request"),
        new HttpCode_t(422, "Unprocessable Entity"),
        new HttpCode_t(423, "Locked"),
        new HttpCode_t(424, "Failed Dependency"),
        new HttpCode_t(426, "Upgrade Required"),
        new HttpCode_t(428, "Precondition Required"),
        new HttpCode_t(429, "Too Many Requests"),
        new HttpCode_t(431, "Request Header Fields Too Large"),
        new HttpCode_t(451, "Unavailable For Legal Reasons"),

        new HttpCode_t(500, "Internal Server Error"),
        new HttpCode_t(501, "Not Implemented"),
        new HttpCode_t(502, "Bad Gateway"),
        new HttpCode_t(503, "Service Unavailable"),
        new HttpCode_t(504, "Gateway Timeout"),
        new HttpCode_t(505, "HTTP Version Not Supported"),
        new HttpCode_t(506, "Variant Also Negotiates"),
        new HttpCode_t(507, "Insufficient Storage"),
        new HttpCode_t(508, "Loop Detected"),
        new HttpCode_t(510, "Not Extended"),
        new HttpCode_t(511, "Network Authentication Required"),
        };

        // Unofficial codes
        //{ 103, "Checkpoint" },
        //{ 103, "Early Hints" },
        //{ 420, "Method Failure" },
        //{ 420, "Enhance Your Calm" },
        //{ 450, "Blocked by Windows Parental Controls" },
        //{ 498, "Invalid Token" },
        //{ 499, "Token Required" },
        //{ 509, "Bandwidth Limit Exceeded" },
        //{ 530, "Site is frozen" },
        //{ 598, "Network read timeout error" },
        // MS IIS, Internet Information Services
        //{ 440, "Login Time-out" },
        //{ 449, "Retry With" },
        //{ 451, "Redirect" },
        //nginx
        //{ 444, "No Response" },
        //{ 495, "SSL Certificate Error" },
        //{ 496, "SSL Certificate Required" },
        //{ 497, "HTTP Request Sent to HTTPS Port" },
        //{ 499, "Client Closed Request" },
        //Cloudflare
        //{ 520, "Unknown Error" },
        //{ 521, "Web Server Is Down" },
        //{ 522, "Connection Timed Out" },
        //{ 523, "Origin Is Unreachable" },
        //{ 524, "A Timeout Occurred" },
        //{ 525, "SSL Handshake Failed" },
        //{ 526, "Invalid SSL Certificate" },
        //{ 527, "Railgun Error" },
        //{ 0, "Unknown" },




    public HttpRequest() {
        options = new ArrayList<>();
        cookies = new ArrayList<>();
        payload = new ByteArrayOutputStream();
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


}
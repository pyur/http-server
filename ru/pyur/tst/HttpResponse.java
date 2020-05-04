package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class HttpResponse extends HttpHeader {

    private String szVersion;
    private int version;

    public int code;
    public String szDesc;


    private ArrayList<Cookie> set_cookies = new ArrayList<>();

    public int content_length;

    // ---- payload ---- //

    //String szPayload;
    private ByteArrayOutputStream payload;
    //Blob szBinaryPayload;
    //size_t BinaryPayloadSize;



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




    public HttpResponse() {
        //options = new ArrayList<>();
        payload = new ByteArrayOutputStream();
    }



    public void parse(byte[] bytes) throws Exception {
        //System.out.println("HttpResponse. parse()");
        if (bytes.length == 0)  throw new Exception("header zero length");

        String data = new String(bytes);

        String[] list = Util.explode("\r\n", data);

        //System.out.println("---- list ------------------------------------------------------");
        //for (String str : list) { System.out.println("[" + str + "]"); }
        //System.out.println("----------------------------------------------------------------");

        if (list[0].isEmpty())  throw new Exception("first line empty");


        PStr response1 = Util.split(' ', list[0]);

        if (response1.value.isEmpty())  throw new Exception("first line has no spaces");

        //System.out.println("----------------------------------------------------------------");
        //for (String str : request) { System.out.println("[" + str + "]"); }
        //System.out.println("----------------------------------------------------------------");

        szVersion = response1.key;
//todo  version = Integer.parseInt(response[0]);

        PStr response2 = Util.split(' ', response1.value);

        code = Integer.parseInt(response2.key);
        szDesc = response2.value;


        // ---------------- parse options ---------------- //

        parseOptions(list);

        //System.out.println("----------------------------------------------------------------");
        //for (PStr option : options) { System.out.println("[" + option.key + "] : [" + option.value + "]"); }
        //System.out.println("----------------------------------------------------------------");


        // todo ---------------- Parse Cookie(s) ---------------- //

        //'Set-Cookie'
        //rs->cookies = Http_ParseCookies(rs->options);
        //for (PStr opt : options) {
        //  if (opt.key.equals("Set-Cookie")) { set_cookies.add(opt.value); }
        //  }

    }





    public void setVersion(int version) { this.version = version; }


    public void setCode(int code) { this.code = code; }



    public void setConnectionClose() { addOption("Connection", "close"); }



    public void appendPayload(byte[] bytes) {
        if (bytes == null)  return;
        try { payload.write(bytes); } catch (Exception e) { e.printStackTrace(); }
    }


    public void appendPayload(String str) {
        if (str == null)  return;
        byte[] bytes = str.getBytes();
        try { payload.write(bytes); } catch (Exception e) { e.printStackTrace(); }
    }



    public byte[] stringify() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();

        // ---------------- first line ---------------- //

        sb.append(szHttpVersion[version]);
        // if '0', try raw.

        sb.append(" ");

        sb.append(code);

        sb.append(" ");

        szDesc = findCodeDesc(code);
        sb.append(szDesc);

        sb.append("\r\n");


        // ---------------- Cookies ---------------- //

        for (Cookie cookie : set_cookies) {
            //lame implode. rewrite with Util.implode
            StringBuilder cook = new StringBuilder();

            cook.append(cookie.name);
            cook.append("=");
            cook.append(cookie.value);

            cook.append(";");

            cook.append("expires");
            cook.append("=");
            cook.append(cookie.getStringExpires());

            cook.append(";");

            cook.append("path");
            cook.append("=");
            cook.append(cookie.path);

            addOption("Set-Cookie", cook.toString());
        }


        // ---------------- options ---------------- //

        if (options != null && options.size() > 0) {
            for (PStr opt : options) {
                sb.append(opt.key);
                sb.append(": ");
                sb.append(opt.value);

                sb.append("\r\n");
            }
        }



        if (payload != null && payload.size() > 0) {
            sb.append("Content-Length: ");
            sb.append(payload.size());
            sb.append("\r\n");
        }

        // Content-Length: 0



        // ---------------- header end ---------------- //

        sb.append("\r\n");

        try { os.write(sb.toString().getBytes()); } catch (Exception e) { e.printStackTrace(); }



        // ---------------- payload ---------------- //

        if (payload != null && payload.size() > 0) {
            try { os.write(payload.toByteArray()); } catch (Exception e) { e.printStackTrace(); }
        }


        // ---------------- inflate ---------------- //

        return os.toByteArray();
    }



    private String findCodeDesc(int code) {
        for (HttpCode_t hc : HttpCode) {
            if (hc.code == code)  return hc.desc;
        }

        return "Unknown";
    }




    @Override
    public void setFirstLine(String first_line) throws Exception {
        PStr response1 = Util.split(' ', first_line);

        if (response1.value.isEmpty())  throw new Exception("first line has no spaces");

        //System.out.println("----------------------------------------------------------------");
        //for (String str : request) { System.out.println("[" + str + "]"); }
        //System.out.println("----------------------------------------------------------------");

        szVersion = response1.key;
//todo  version = Integer.parseInt(response[0]);

        PStr response2 = Util.split(' ', response1.value);

        code = Integer.parseInt(response2.key);
        szDesc = response2.value;
    }




    // -------- Cookie ----------------------------------------------------------------

    // todo: move to inflater (inflater will take care of duplicates)
    public void setCookie(String name, String value, int expires, String path) {
        set_cookies.add(new Cookie(name, value, expires, path));
    }


}
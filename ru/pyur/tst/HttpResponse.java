package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class HttpResponse extends HttpHeader {

    private String szVersion;
    private int version;  // todo

    public int code;
    public String szDesc;


    private ArrayList<Cookie> set_cookie;

    public int content_length;

    // ---- payload ---- //

    //String szPayload;
    private ByteArrayOutputStream payload;
    //Blob szBinaryPayload;
    //size_t BinaryPayloadSize;



    public HttpResponse() {
        options = new ArrayList<>();
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

    }



    public void setConnectionClose() {
        PStr opt = new PStr("Connection", "close");
        options.add(opt);
    }



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

        //r Expandable_AppendString(exResponse, szHttpVersion[rs->version]);
        sb.append("HTTP/1.1");
        // if '0', try raw.

        //r Expandable_AppendString(exResponse, " ");
        sb.append(" ");

        //r Expandable_AppendString(exResponse, String_FromInt(rs->code));
        sb.append("200");

        //r Expandable_AppendString(exResponse, " ");
        sb.append(" ");

        //r Expandable_AppendString(exResponse, rs->szDesc);
        sb.append("OK");

        //r Expandable_AppendString(exResponse, "\r\n");
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



        // ---------------- header end ---------------- //

        //r Expandable_AppendString(exResponse, "\r\n");
        sb.append("\r\n");

        try { os.write(sb.toString().getBytes()); } catch (Exception e) { e.printStackTrace(); }



        // ---------------- payload ---------------- //

        if (payload != null && payload.size() > 0) {
            //r Expandable_AppendString(exResponse, rs->szPayload);
            try { os.write(payload.toByteArray()); } catch (Exception e) { e.printStackTrace(); }
        }


        // ---------------- inflate ---------------- //

        //r Str szResponse = Expandable_ConvertToString(&exResponse);
        //r return szResponse;
        return os.toByteArray();
    }


}
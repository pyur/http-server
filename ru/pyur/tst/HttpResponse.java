package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class HttpResponse extends HttpHeader {

    private String szVersion;
    private int version;  // todo

    private int code;
    private String szDesc;


    public ArrayList<PStr> options;

    private ArrayList<Cookie> set_cookie;

    public int content_length;

    // ---- payload ---- //

    //String szPayload;
    private ByteArrayOutputStream exPayload;
    //Blob szBinaryPayload;
    //size_t BinaryPayloadSize;



    public HttpResponse() {
        options = new ArrayList<>();
        exPayload = new ByteArrayOutputStream();
    }

    public HttpResponse(byte[] data) { parse(data); }


    public void parse(byte[] bytes) {
        //System.out.println("parsing...");
        if (bytes.length == 0)  return;  // todo: throw

        String data = new String(bytes);

        String[] list = Util.explode("\r\n", data);

        //System.out.println("----------------------------------------------------------------");
        //for (String str : list) { System.out.println("[" + str + "]"); }
        //System.out.println("----------------------------------------------------------------");

        if (list[0].isEmpty()) {
            // todo: Throw exception
            return;
        }


        PStr response1 = Util.split(' ', list[0]);

        if (response1.value.isEmpty()) {
            // todo: Throw exception
            return;
        }

        //System.out.println("----------------------------------------------------------------");
        //for (String str : request) { System.out.println("[" + str + "]"); }
        //System.out.println("----------------------------------------------------------------");

        szVersion = response1.key;
        //version = Integer.parseInt(response[0]);

        PStr response2 = Util.split(' ', response1.value);

        code = Integer.parseInt(response2.key);
        szDesc = response2.value;


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


        // ---------------- Parse Cookie(s) ---------------- //

        //'Set-Cookie'
        //rs->cookies = Http_ParseCookies(rs->options);

    }


    public void setConnectionClose() {
        PStr opt = new PStr("Connection", "close");
        options.add(opt);
    }



    public void appendPayload(String str) {
        byte[] bytes = str.getBytes();
        try { exPayload.write(bytes); } catch (Exception e) { e.printStackTrace(); }
    }



    public byte[] stringify() {
        //r Expandable exResponse = Expandable_Create();
        ByteArrayOutputStream os = new ByteArrayOutputStream();


        // ---------------- first line ---------------- //

        //r Expandable_AppendString(exResponse, szHttpVersion[rs->version]);
        try { os.write("HTTP/1.1".getBytes()); } catch (Exception e) { e.printStackTrace(); }
        // if '0', try raw.

        //r Expandable_AppendString(exResponse, " ");
        try { os.write(" ".getBytes()); } catch (Exception e) { e.printStackTrace(); }

        //r Expandable_AppendString(exResponse, String_FromInt(rs->code));
        try { os.write("200".getBytes()); } catch (Exception e) { e.printStackTrace(); }

        //r Expandable_AppendString(exResponse, " ");
        try { os.write(" ".getBytes()); } catch (Exception e) { e.printStackTrace(); }

        //r Expandable_AppendString(exResponse, rs->szDesc);
        try { os.write("OK".getBytes()); } catch (Exception e) { e.printStackTrace(); }

        //r Expandable_AppendString(exResponse, "\r\n");
        try { os.write("\r\n".getBytes()); } catch (Exception e) { e.printStackTrace(); }


        // ---------------- options ---------------- //

        if (options != null && options.size() > 0) {
            //Str szFlat;

            //for (Ui idx = 0; rs->options[idx]; idx++) {
            for (PStr opt : options) {
                //r szFlat = Pair_Join(rs->options[idx], ": ");
                try { os.write((opt.key + ": " + opt.value).getBytes()); } catch (Exception e) { e.printStackTrace(); }

                //r Expandable_AppendString(exResponse, szFlat);
                //r String_Destroy(szFlat);
                //r Expandable_AppendString(exResponse, "\r\n");
                try { os.write("\r\n".getBytes()); } catch (Exception e) { e.printStackTrace(); }
            }
        }



        if (exPayload != null && exPayload.size() > 0) {
            //r Expandable_AppendString(exResponse, "Content-Length: ");
            //r Expandable_AppendString(exResponse, String_FromInt(strlen(rs->szPayload)));
            //r Expandable_AppendString(exResponse, "\r\n");
            try { os.write("Content-Length: ".getBytes()); } catch (Exception e) { e.printStackTrace(); }
            try { os.write((""+exPayload.size()).getBytes()  ); } catch (Exception e) { e.printStackTrace(); }
            try { os.write("\r\n".getBytes()); } catch (Exception e) { e.printStackTrace(); }
        }

        //else if (other types of payload) {}

        // Content-Length: 0



        // ---------------- header end ---------------- //

        //r Expandable_AppendString(exResponse, "\r\n");
        try { os.write("\r\n".getBytes()); } catch (Exception e) { e.printStackTrace(); }


        // ---------------- payload ---------------- //

        if (exPayload != null && exPayload.size() > 0) {
            //r Expandable_AppendString(exResponse, rs->szPayload);
            try { os.write(exPayload.toByteArray()); } catch (Exception e) { e.printStackTrace(); }
        }


        // ---------------- inflate ---------------- //

        //r Str szResponse = Expandable_ConvertToString(&exResponse);
        //r return szResponse;
        return os.toByteArray();
    }


}
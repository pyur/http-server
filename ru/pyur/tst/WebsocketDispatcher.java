package ru.pyur.tst;

import ru.pyur.tst.json.Json;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;


public abstract class WebsocketDispatcher {

    protected ModularHost session;

//x    private ArrayList<PStr> lsQuery;

//    private InputStream is;
//    private OutputStream os;
    private WebsocketWriter websocket_writer;

    //protected String jact;  // json action



    // ---------------- Database ---------------- //

//    private static final String DB_URL = "jdbc:mariadb://127.0.0.1/";
//    private static final String DB_USER = "root";
//    private static final String DB_PASSWORD = "1";
//
//    protected Connection m_connection;



    // -------- Config -------- //

//    private static final String CONFIG_URL = "jdbc:sqlite:config.db";
//
//    protected Connection m_config;



//    public WebsocketDispatcher() {}
//    public WebsocketDispatcher(WebsocketSession session) {
//        //initHtml(session);
//        initCommon(session);
//    }



//    private void initCommon(WebsocketSession session) {
//        this.session = session;
//x        lsQuery = session.getParam();
//    }

    public void init(ModularHost session) {
        this.session = session;

//        this.is = session.getInputStream();
//        this.os = session.output_stream;
    }


//    public void setStreams(InputStream is, OutputStream os) {
//        this.is = is;
//        this.os = os;
//    }



//    protected abstract void makeHtml();



//    protected String getModule() { return session.module; }

//    protected String getAction() { return session.action; }


    // todo getFilteredQuery for numbers, only_alphabet, etc. for screening malicious data
//    protected String getParam(String key) throws Exception {
//        for (PStr pair : lsQuery) {
//            if (pair.key.equals(key))  return pair.value;
//        }
//
//        throw new Exception("parameter \'" + key + "\' absent.");
//        //return null;
//    }





    public void dispatch() {

        WebsocketReader wr = new WebsocketReader(session.getInputStream());
        websocket_writer = new WebsocketWriter(session.getOutputStream());

        onConnect();

        try {
            receiveLoop(wr);
        } catch (Exception e) { e.printStackTrace(); }

        onDisconnect();
    }

    private void receiveLoop(WebsocketReader wr) throws Exception {
        WebsocketReader.WebsocketPacket packet;

        for(;;) {
            packet = wr.read();
            //if (packet.opcode == -1)  throw new Exception("unexpected stream close.");

            switch (packet.opcode) {
                case 0:
                    // todo: continuation frame
                    break;

                case 1:
                    receivedText(new String(packet.payload));
                    break;

                case 2:
                    receivedBinary(packet.payload);
                    break;

                // 3-7 reserved

                case 8:
                    receivedClose(packet.payload);
                    return;

                case 9:
                    System.out.println("ping");
                    // todo: answer with pong
                    break;

                case 10:
                    System.out.println("pong");
                    // do reset keep-alive timeout
                    break;

                // 11-15 reserved

                default:
                    System.out.println("unknown opcode: " + packet.opcode);
                    //throw new Exception("unknown opcode");
                    break;
            }

        }  // for

    }



    protected void onConnect() {}

    protected void onDisconnect() {}




    protected void receivedText(String text) {
        System.out.println("text: " + text);
        //parse JSON
        //call 'action'
        //if not parsable, call 'malcious request'

        Json json = new Json();  // .parse("[]");
        try {
            json.parse(text);
        } catch (Exception e) { e.printStackTrace(); return; }


        if (json.has("act")) {
            String act = "";
            try {
                act = json.getNode("act").toString();
            } catch (Exception e) { e.printStackTrace(); }
            action(act, json);
        }





        //System.out.println("\n>>>>--------------------------------");
        //jp.dump(json);
        //System.out.println("\n>>>>--------------------------------");
        //jp.dumpNode(json);
        //System.out.println("\n>>>>--------------------------------");

        //System.out.println(">>> " + (json.has("act") ? "yes" : "no") );

        //System.out.println(">>> " + json.stringify() );


//        try {
//            System.out.println("\n---- test --------------------------------");
//            Json json_test = new Json().parse("[\"first\", \"second \\u0041\\u001F\", \"third\"]");
//            json.parse(text);
//            json.dump(json_test);
//            String json_test_str = json_test.stringify();
//            System.out.println("strigified: " + json_test_str);
//            System.out.println("\n---- test end --------------------------------");
//        } catch (Exception e) { e.printStackTrace(); return; }
    }

    protected void receivedBinary(byte[] data) {}


    protected void action(String action, Json other_params) {}

    //protected String getParam(String key) {}



    protected void receivedClose(byte[] data) {
        System.out.println("receivedClose()");
        //TODO!! send back close packet
    }




    protected void sendText(String text) {
        try {
            //wos.write(text);
            websocket_writer.write(text);
        } catch (Exception e) { e.printStackTrace(); }
    }

    protected void sendBinary(byte[] data) {
        //wos.write(data);
    }


    protected void sendAction(Json json) {
        String flat = json.stringify();
        sendText(flat);
    }



    // ---------------- higher level abstract functions ---------------- //

    // parse JSON. call appropriate prototypes.



}
package ru.pyur.tst;

import ru.pyur.tst.db.DbManager;
import ru.pyur.tst.json.Json;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;


public abstract class WebsocketDispatcher {

    protected ModularHost session;

    private WebsocketWriter websocket_writer;

    //protected String jact;  // json action

    private DbManager db_manager;
    private Connection db_connection;
    private Connection db_config;




    public void init(ModularHost session) {
        this.session = session;

        this.db_manager = session.getDbManager();
        db_connection = db_manager.getDb();
        db_config = db_manager.getConfigDb();
    }




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




    // ---- prototypes ------------------------------------------------

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




    protected void receivedClose(byte[] data) {
        System.out.println("receivedClose()");
        //TODO!! send back close packet
    }




    protected void sendText(String text) {
        try {
            websocket_writer.write(text);
        } catch (Exception e) { e.printStackTrace(); }
    }

    protected void sendBinary(byte[] data) {
        try {
            websocket_writer.write(data);
        } catch (Exception e) { e.printStackTrace(); }
    }


    protected void sendAction(Json json) {
        String flat = json.stringify();
        sendText(flat);
    }



    // ---------------- higher level abstract functions ---------------- //

    // parse JSON. call appropriate prototypes.




    // -------------------------------- Database -------------------------------- //

    // todo

}
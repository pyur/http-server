package ru.pyur.tst.sample_host.websocket;

import ru.pyur.tst.WebsocketDispatcher;
import ru.pyur.tst.util.Json;


public class Ws_Chat extends WebsocketDispatcher {


    @Override
    protected void receivedBinary(byte[] data) {}


    //for send use sendText()


    @Override
    protected void action(String act, Json opts) {
        System.out.println("json action: " + act);

        if (act.equals("fun")) {
            //sendText("some text");

            Json aw = new Json();
            try {
                aw.add("act", "msg");
                aw.add("chat", 0);
                aw.add("user", 0);
                aw.add("msg", "Hello from Java server!");
                aw.add("time", (int)(System.currentTimeMillis()/1000));
            } catch (Exception e) { e.printStackTrace(); }

            sendAction(aw);
        }

        else if (act.equals("fch")) {
            //sendText("some text");
            //sendAction();
        }

    }


}
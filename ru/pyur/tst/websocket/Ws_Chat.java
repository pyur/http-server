package ru.pyur.tst.websocket;

import ru.pyur.tst.WebsocketModule;
import ru.pyur.tst.json.Json;


public class Ws_Chat extends WebsocketModule {


    //@Override
    //protected void receivedText(String text) {}


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
                aw.add(new Json("act", "msg"));
                aw.add(new Json("chat", 0));
                aw.add(new Json("user", 0));
                aw.add(new Json("msg", "Hello from Java server!"));
                aw.add(new Json("time", (int)(System.currentTimeMillis()/1000)));
            } catch (Exception e) { e.printStackTrace(); }

            sendAction(aw);
        }

        else if (act.equals("fch")) {
            //sendText("some text");
            //sendAction();
        }

    }


}
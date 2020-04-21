package ru.pyur.tst.websocket;

import ru.pyur.tst.WebsocketModule;


public class Ws_Chat extends WebsocketModule {


    //@Override
    //protected void receivedText(String text) {}


    @Override
    protected void receivedBinary(byte[] data) {}


    //for send use sendText()


    @Override
    protected void action(String action) {
        System.out.println("received action: " + action);

        //if (action.equals("jjj")) {

        //}

    }


}
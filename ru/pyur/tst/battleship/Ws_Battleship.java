package ru.pyur.tst.battleship;

import ru.pyur.tst.WebsocketModule;
import ru.pyur.tst.json.Json;


public class Ws_Battleship extends WebsocketModule {

    private BattleshipManager bs_manager;
    private int user;



    @Override
    protected void onConnect() {
        //System.out.println("Ws_Battleship. onConnect()");

        bs_manager = BattleshipManager.getInstance();

        user = bs_manager.registerParticipant(cb_manager);

        System.out.println("registered as user: " + user);


        Json aw = new Json();
        try {
            aw.add("act", "reg");
            aw.add("user", user);
            //aw.add("time", (int)(System.currentTimeMillis()/1000));
        } catch (Exception e) { e.printStackTrace(); return; }

        sendAction(aw);
    }


//    @Override
//    protected void onDisconnect() {
//        bs_manager.unregisterParticipant(user);
//    }



    // ---- Manager callback ---- //

    private CallbackBattleship cb_manager = new CallbackBattleship() {
        @Override
        public void onFieldChange(int x, int y, int val) {
            Json aw = new Json();
            try {
                aw.add("act", "fch");
                aw.add("user", user);
                aw.add("x", x);
                aw.add("y", y);
                aw.add("val", val);
                //aw.add("time", (int)(System.currentTimeMillis()/1000));
            } catch (Exception e) { e.printStackTrace(); return; }

            sendAction(aw);
        }


//        @Override
//        public void onSecond() {
//
//        }
    };



    //@Override
    //protected void receivedText(String text) {}


    @Override
    protected void receivedBinary(byte[] data) {}


    //for send use sendText()


    @Override
    protected void action(String act, Json params) {
        System.out.println("json action: " + act);

        if (act.equals("tch")) {
            int x;
            int y;
            try {
                x = params.getNode("x").getInt();
                y = params.getNode("y").getInt();
            } catch (Exception e) { e.printStackTrace(); return; }

            System.out.println("act user: " + user);
            bs_manager.touchCell(user, x, y);
        }

        else if (act.equals("fch")) {
            //sendText("some text");
            //sendAction();
        }

    }


}
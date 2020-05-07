package ru.pyur.tst.sample_host.battleship;

import ru.pyur.tst.WebsocketDispatcher;
import ru.pyur.tst.json.Json;


public class Ws_Battleship extends WebsocketDispatcher {

    private BattleshipManager bs_manager;
    //private int user;
    private BattleshipClient bs_client;



    @Override
    protected void onConnect() {
        //System.out.println("Ws_Battleship. onConnect()");

        bs_manager = BattleshipManager.getInstance();

        bs_client = new BattleshipClient(cb_manager);

        bs_manager.registerParticipant(bs_client);

        //System.out.println("registered as user: " + bs_client.user);


        Json aw = new Json();
        try {
            aw.add("act", "reg");
            aw.add("user", bs_client.user);
            //aw.add("time", (int)(System.currentTimeMillis()/1000));
        } catch (Exception e) { e.printStackTrace(); return; }

        sendAction(aw);
    }


    @Override
    protected void onDisconnect() {
        bs_manager.unregisterParticipant(bs_client);
    }



    // ---- Manager callback ---- //

    private CallbackBattleship cb_manager = new CallbackBattleship() {
        @Override
        public void onFieldChange(int x, int y, int val) {
            Json aw = new Json();
            try {
                aw.add("act", "fch");
                //aw.add("user", bs_client.user);
                aw.add("x", x);
                aw.add("y", y);
                aw.add("val", val);
                //aw.add("time", (int)(System.currentTimeMillis()/1000));
            } catch (Exception e) { e.printStackTrace(); return; }

            sendAction(aw);
        }


        @Override
        public void onEnemyFieldChange(int x, int y, int val) {
            Json aw = new Json();
            try {
                aw.add("act", "efc");
                //aw.add("user", bs_client.user);
                aw.add("x", x);
                aw.add("y", y);
                aw.add("val", val);
            } catch (Exception e) { e.printStackTrace(); return; }

            sendAction(aw);
        }


        @Override
        public void onReady(int user) {
            //if (bs_client.user == user)  //self

            Json aw = new Json();
            try {
                aw.add("act", "rdy");
                aw.add("user", user);
                //aw.add("val", val);
            } catch (Exception e) { e.printStackTrace(); return; }

            sendAction(aw);
        }


        @Override
        public void onGame() {
            Json aw = new Json();
            try {
                aw.add("act", "gme");
            } catch (Exception e) { e.printStackTrace(); return; }

            sendAction(aw);
        }

    };



    //@Override
    //protected void receivedText(String text) {}


    @Override
    protected void receivedBinary(byte[] data) {}


    //for send use sendText()


    @Override
    protected void action(String act, Json params) {
        //System.out.println("json action: " + act);

        if (act.equals("tch")) {
            int x;
            int y;
            try {
                //user = params.getInt("user");
                x = params.getInt("x");
                y = params.getInt("y");
            } catch (Exception e) { e.printStackTrace(); return; }

            bs_manager.touchCell(bs_client, x, y);
        }


        else if (act.equals("rdy")) {
            //System.out.println("button ready");
            bs_manager.ready(bs_client);
        }


        else if (act.equals("ten")) {
            int x;
            int y;
            try {
                //user = params.getInt("user");
                x = params.getInt("x");
                y = params.getInt("y");
            } catch (Exception e) { e.printStackTrace(); return; }

            bs_manager.touchEnemy(bs_client, x, y);
        }

    }


}
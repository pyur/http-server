package ru.pyur.tst.battleship;

import java.util.ArrayList;

import static ru.pyur.tst.battleship.BattleshipClient.*;


public class BattleshipManager {

    private static BattleshipManager instance = null;

    public static BattleshipManager getInstance() {
        if (instance == null)  instance = new BattleshipManager();
        return instance;
    }



    private ArrayList<BattleshipClient> clients = new ArrayList<>();
    private int client_count = 100;

    private int players = 0;

    private int game_state;

    private static final int GAME_STATE_PREPARE = 0;
    private static final int GAME_STATE_PLAY = 1;
    private static final int GAME_STATE_OVER = 2;



    public BattleshipManager() {
        game_state = GAME_STATE_PREPARE;
    }



    public BattleshipClient registerParticipant(BattleshipClient client) {
        if (players > 1) {
            System.out.println("registerParticipant. no room.");
            return null;
        }

        client.user = client_count;
        client_count++;
        clients.add(client);

        players++;
        client.player = players;

        return client;
    }


    public void unregisterParticipant(BattleshipClient client) {
        //players--;
        //todo: search and correct remaining player number

        clients.remove(client);
    }



//    public BattleshipClient getUser(int user) throws Exception {
//        for (BattleshipClient client : clients) {
//            if (client.user == user)  return client;
//        }
//
//        throw new Exception("user \'" + user + "\' not found.");
//    }



    public BattleshipClient getEnemy(BattleshipClient own) {
        for (BattleshipClient client : clients) {
            if (client.user != own.user)  return client;
        }

        //throw new Exception("user \'" + user + "\' not found.");
        return null;
    }




    public void touchCell(BattleshipClient client, int x, int y) {
//        BattleshipClient client;
//        try {
//            client = getUser(user);
//        } catch (Exception e) { e.printStackTrace(); return; }

        switch (client.field[x][y]) {
            case FIELD_EMPTY_HIDDEN:
                client.field[x][y] = FIELD_SHIP_HIDDEN;
                break;

            case FIELD_SHIP_HIDDEN:
                client.field[x][y] = FIELD_EMPTY_HIDDEN;
                break;
        }

        client.cb_client.onFieldChange(x, y, client.field[x][y]);
        // to others - no actions
    }



    public void ready(BattleshipClient client) {
        client.ready = true;

        boolean all_ready = true;
        for (BattleshipClient cl : clients) {
            if (!cl.ready)  all_ready = false;
            cl.cb_client.onReady(client.user);
        }


        if (players < 2)  return;
        if (!all_ready)  return;
        game_state = GAME_STATE_PLAY;

        for (BattleshipClient cl : clients) {
            cl.cb_client.onGame();
        }

    }




    public void touchEnemy(BattleshipClient client, int x, int y) {
        BattleshipClient enemy = getEnemy(client);

        switch (enemy.field[x][y]) {
            case FIELD_EMPTY_HIDDEN:
                enemy.field[x][y] = FIELD_EMPTY_VISIBLE;
                break;

            case FIELD_SHIP_HIDDEN:
                enemy.field[x][y] = FIELD_SHIP_HIT;
                break;
        }

        client.cb_client.onEnemyFieldChange(x, y, enemy.field[x][y]);

        enemy.cb_client.onFieldChange(x, y, enemy.field[x][y]);

        //next turn
    }





}
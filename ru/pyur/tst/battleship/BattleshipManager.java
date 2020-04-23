package ru.pyur.tst.battleship;

import java.util.ArrayList;

public class BattleshipManager {

    private static BattleshipManager instance = null;

    public static BattleshipManager getInstance() {
        if (instance == null)  instance = new BattleshipManager();
        return instance;
    }


    //public interface CallbackBattleshipManager


    public BattleshipManager() {}




    //private ArrayList<CallbackBattleship> clients = new ArrayList<>();
    private ArrayList<BattleshipClient> clients = new ArrayList<>();
    private int client_count = 100;


    public int registerParticipant(CallbackBattleship cb_client) {
        BattleshipClient client = new BattleshipClient(client_count, cb_client);
        client_count++;
        clients.add(client);
        return client.user;
    }


    public void unregisterParticipant(int user) {
        try {
            BattleshipClient client = getUser(user);
            clients.remove(client);
        } catch (Exception e) { e.printStackTrace(); }
    }



    public BattleshipClient getUser(int user) throws Exception {
        System.out.println("clients length: " + clients.size());
        for (BattleshipClient client : clients) {
            System.out.println("  user: " + client.user);
            if (client.user == user)  return client;
        }

        throw new Exception("user \'" + user + "\' not found.");
    }



    public void touchCell(int user, int x, int y) {
        BattleshipClient client;
        try {
            client = getUser(user);
        } catch (Exception e) { e.printStackTrace(); return; }

        switch (client.field[x][y]) {
            case 0:
                client.field[x][y] = 1;
                break;

                case 1:
                client.field[x][y] = 0;
                break;
        }

        client.cb_client.onFieldChange(x, y, client.field[x][y]);
        // to others - no actions
    }






}
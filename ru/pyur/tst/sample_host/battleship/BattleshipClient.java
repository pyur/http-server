package ru.pyur.tst.sample_host.battleship;

public class BattleshipClient {

    public int user;
    public int player;
    public CallbackBattleship cb_client;
    public boolean ready;

    public int[][] field;

    public static final int FIELD_EMPTY_HIDDEN = 0;
    public static final int FIELD_EMPTY_VISIBLE = 1;
    public static final int FIELD_SHIP_HIDDEN = 2;
    public static final int FIELD_SHIP_HIT = 3;  // visible


    public BattleshipClient() {}

    public BattleshipClient(CallbackBattleship cb_client) {
        this.cb_client = cb_client;

        user = -1;
        ready = false;

        field = new int[10][10];
    }



}
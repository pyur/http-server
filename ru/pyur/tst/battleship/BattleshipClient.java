package ru.pyur.tst.battleship;

public class BattleshipClient {

    public int user;
    public CallbackBattleship cb_client;

    public int[][] field;


    public BattleshipClient() {}

    public BattleshipClient(int user, CallbackBattleship cb_client) {
        this.user = user;
        this.cb_client = cb_client;

        field = new int[10][10];
    }



}
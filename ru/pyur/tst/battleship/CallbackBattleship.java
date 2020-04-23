package ru.pyur.tst.battleship;

public interface CallbackBattleship {
    void onFieldChange(int x, int y, int val);
    void onEnemyFieldChange(int x, int y, int val);
    void onReady(int user);
    void onGame();

}
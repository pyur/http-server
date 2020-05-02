package ru.pyur.tst;

public class Var {
    public static final int VAR_TYPE_INT = 0;
    public static final int VAR_TYPE_STRING = 1;

    private int type;

    private int value_int;
    private String value_string;

    public Var(int val) {
        type = VAR_TYPE_INT;
        value_int = val;
    }

    public Var(String val) {
        type = VAR_TYPE_STRING;
        value_string = val;
    }

    public int getType() { return type; }

    public int getInt() { return value_int; }

    public String getString() { return value_string; }
}
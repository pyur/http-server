package ru.pyur.tst.db;

import java.sql.PreparedStatement;

public class Var {
    public static final int VAR_TYPE_INT = 0;
    public static final int VAR_TYPE_STRING = 1;
    public static final int VAR_TYPE_BYTE_ARRAY = 2;
    public static final int VAR_TYPE_DATE = 3;
    public static final int VAR_TYPE_TIME = 4;
    public static final int VAR_TYPE_DATETIME = 5;

    private int type;

    private int value_int;
    private String value_string;
    private byte[] value_byte_array;


    public Var(int val) {
        type = VAR_TYPE_INT;
        value_int = val;
    }

    public Var(String val) {
        type = VAR_TYPE_STRING;
        value_string = val;
    }

    public Var(byte[] val) {
        type = VAR_TYPE_BYTE_ARRAY;
        value_byte_array = val;
    }

    public int getType() { return type; }

    public int getInt() { return value_int; }

    public String getString() { return value_string; }

    public byte[] getBytes() { return value_byte_array; }



    public void applyToPreparedStatement(PreparedStatement ps, int idx) throws Exception {
        switch (type) {
            case VAR_TYPE_INT:
                ps.setInt(idx, value_int);
                break;

            case VAR_TYPE_STRING:
                ps.setString(idx, value_string);
                break;

            case VAR_TYPE_BYTE_ARRAY:
                ps.setBytes(idx, value_byte_array);
                break;
        }
    }

}
package ru.pyur.tst.db;

import java.sql.ResultSet;


public abstract class Fetch {

    protected ResultSet result_set;

//?    boolean is_empty;


//    public Fetch(ResultSet result_set) {
//        this.result_set = result_set;
//
//        try {
//            is_empty = result_set.isAfterLast();
//        } catch (Exception e) {}
//
//        try {
//            result_set.next();
//        } catch (Exception e) {}
//    }


//    public boolean isEmpty() throws Exception {
//        return is_empty;
//    }



    public int getInt(int idx) throws Exception {
        return result_set.getInt(idx);
    }


    public int getInt(String name) throws Exception {
        return result_set.getInt(name);
    }



    public String getString(int idx) throws Exception {
        return result_set.getString(idx);
    }


    public String getString(String name) throws Exception {
        return result_set.getString(name);
    }



}
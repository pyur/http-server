package ru.pyur.tst;

import java.sql.ResultSet;


public class Fetch {

    private ResultSet result_set;

    //boolean is_empty;


    public Fetch(ResultSet result_set) {
        this.result_set = result_set;
        //try {
        //    is_empty = result_set.isAfterLast();
        //} catch (Exception e) {}
    }


    //public int count() {
        //move to first
        //iterate
        //move to first
    //    return 0;
    //}


    public boolean isEmpty() throws Exception {
        //return is_empty;
        return result_set.isAfterLast();
    }



    public boolean row() throws Exception {
        return result_set.next();
    }



}
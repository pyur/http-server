package ru.pyur.tst.db;

import java.sql.ResultSet;


public class FetchArray extends Fetch {

//b    private ResultSet result_set;

    //boolean is_empty;


    public FetchArray(ResultSet result_set) {
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


    //override
    public boolean isEmpty() throws Exception {
        //return is_empty;
        return result_set.isAfterLast();
    }



    // moveToNext
    public boolean available() throws Exception {
        return result_set.next();
    }



}
package ru.pyur.tst.db;

import java.sql.ResultSet;


public class FetchSingle extends Fetch {

//b    private ResultSet result_set;

    private boolean is_empty;


    public FetchSingle(ResultSet result_set) {
        this.result_set = result_set;

        try {
            is_empty = result_set.isAfterLast();
        } catch (Exception e) {}

        try {
            result_set.next();
        } catch (Exception e) {}
    }


    public boolean isEmpty() throws Exception {
        return is_empty;
    }


}
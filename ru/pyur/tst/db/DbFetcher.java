package ru.pyur.tst.db;


import ru.pyur.tst.tags.Table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


public abstract class DbFetcher extends DbFetch {

//x    private FetcherCallback fetcher_callback;
//x
//x    protected interface FetcherCallback {
//x        void onEmpty();
//x        void onFetch();
//x        void onRow();
//x        //void onRowWithId(int row_id);
//x        void onColumn(ResultSet rs, int column_num);
//x        //void onColumnString(int column_num, String value);
//x    }



    // ---- prototypes -----

    protected void onEmpty() {}

    protected void onFetch() {}

    protected void onRow() {}

    protected void onColumn(ResultSet rs, int column_num) {}



    // ---- setters, getters ----------------

    //protected void table(String table) { db.table(table); }

    //protected void col(String[] columns) { db.col(columns); }


    //abstract public ArrayList<Tag> make();


//x    protected void setFetcherCallback(FetcherCallback cb_result) { fetcher_callback = cb_result; }


    protected void fetchResults() throws Exception {
        ResultSet rs;
//x        try {
            rs = getResultSet();
//x        } catch (Exception e) {
//x            e.printStackTrace();
            //callback.onError();
//x            return;
//x        }


        boolean is_empty = true;
//x        try {
            is_empty = rs.isAfterLast();
//x        } catch (Exception e) { e.printStackTrace(); }

        if (is_empty) {
//x            fetcher_callback.onEmpty();
            onEmpty();
            return;
        }


//x        fetcher_callback.onFetch();
        onFetch();


//x        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            //String name = rsmd.getColumnName(1);
            int column_count = rsmd.getColumnCount();

            while (rs.next()) {
//x                fetcher_callback.onRow();
                onRow();
                //if (withId)  callback.onRow(row_id);

                for (int i = 1; i <= column_count; i++) {
//x                    fetcher_callback.onColumn(rs, i);
                    onColumn(rs, i);
                    //String value = rs.getString(i);
                    //fetcher_callback.onColumnString(i, value);
                }

            }
            rs.close();
//x        } catch (Exception e) {
//x            e.printStackTrace();
            //callback.onOtherError();
//x            return;
//x        }

    }


}
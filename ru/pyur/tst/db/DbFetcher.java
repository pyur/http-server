package ru.pyur.tst.db;


import ru.pyur.tst.tags.Table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


public abstract class DbFetcher extends DbFetch {

    private FetcherCallback fetcher_callback;

    protected interface FetcherCallback {
        void onEmpty();
        void onFetch();
        void onRow();
        //void onRowWithId(int row_id);
        void onColumn(ResultSet rs, int column_num);
        //void onColumnString(int column_num, String value);
    }


    // ---- setters, getters ----------------

    //protected void table(String table) { db.table(table); }

    //protected void col(String[] columns) { db.col(columns); }


    //abstract public ArrayList<Tag> make();


    protected void setFetcherCallback(FetcherCallback cb_result) {
        fetcher_callback = cb_result;
    }


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
            fetcher_callback.onEmpty();
            return;
        }


        fetcher_callback.onFetch();


//x        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            //String name = rsmd.getColumnName(1);
            int column_count = rsmd.getColumnCount();

            while (rs.next()) {
                fetcher_callback.onRow();
                //if (withId)  callback.onRow(row_id);

                for (int i = 1; i <= column_count; i++) {
                    fetcher_callback.onColumn(rs, i);
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
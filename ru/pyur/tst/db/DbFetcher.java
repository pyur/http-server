package ru.pyur.tst.db;


import ru.pyur.tst.tags.Table;
import ru.pyur.tst.tags.Tag;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

//public abstract class DbFetcher {  // maybe 'extends DbFetch'
public abstract class DbFetcher extends DbFetch {

//    protected DbFetch db;

    private Callback callback;

    protected interface Callback {
        void onEmpty();
        void onFetch();
        void onRow();
        //void onRowWithId(int row_id);
        //void onColumn(ResultSet rs, int column_num);
        void onColumnString(int column_num, String value);
    }


    // ---- setters, getters ----------------

    //protected void table(String table) { db.table(table); }

    //protected void col(String[] columns) { db.col(columns); }


    abstract public Table make();
    //abstract public ArrayList<Tag> make();


    protected void setResultCallback(Callback cb_result) {
        callback = cb_result;
    }


    protected void processResults() {
        ResultSet rs;
        try {
            rs = getResultSet();
        } catch (Exception e) {
            e.printStackTrace();
            //callback.onError();
            return;
        }

        callback.onFetch();

        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            //String name = rsmd.getColumnName(1);
            int column_count = rsmd.getColumnCount();

            while (rs.next()) {
                callback.onRow();
                //if (withId)  callback.onRow(row_id);

                for (int i = 1; i <= column_count; i++) {
                    //callback.onColumn(rs, i);
                    String value = rs.getString(i);
                    callback.onColumnString(i, value);
                }

            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            //callback.onOtherError();
            return;
        }

    }


}
package ru.pyur.tst.db;


import ru.pyur.tst.tags.Table;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


public abstract class DbFetcher extends DbFetch {

    private int row_id;


    // ---- prototypes -----

    protected int getRowId() { return row_id; }


    // ---- prototypes -----

    protected void onEmpty() {}

    protected void onFetch() {}

    protected void onRow() {}

    protected void onColumn(ResultSet rs, int column_num) {}

    protected void onDone() {}



    protected void fetchResults() throws Exception {
        ResultSet rs = getResultSet();


        boolean is_empty = rs.isAfterLast();

        if (is_empty) {
            onEmpty();
            return;
        }


        onFetch();


        ResultSetMetaData rsmd = rs.getMetaData();
        //String name = rsmd.getColumnName(1);
        int column_count = rsmd.getColumnCount();

        while (rs.next()) {
            onRow();
            //if (withId)  callback.onRow(row_id);
            row_id = rs.getInt(1);

            for (int i = 1; i <= column_count; i++) {
                onColumn(rs, i);
            }

        }

        onDone();

        rs.close();
    }


}
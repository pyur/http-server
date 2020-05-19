package ru.pyur.tst.db;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


public abstract class DbFetcher extends DbFetch {

    private String row_id;
    private int id_column_idx = -1;


    // ---- prototypes -----

    protected String getRowId() { return row_id; }


    // ---- prototypes -----

    protected void onEmpty() {}

    protected void onFetched() {}

    protected void onRow(String row_id) {}

    //public void onRowId(String row_id) {}  use getRowId()

    protected void onColumn(ResultSet rs, int column_num) {}

    protected void onDone() {}


    protected void setIdColumn(int idx) { id_column_idx = idx; }


    public void fetchResults() throws Exception {
        ResultSet rs = getResultSet();


        boolean is_empty = rs.isAfterLast();

        if (is_empty) {
            onEmpty();
            return;
        }


        onFetched();


        ResultSetMetaData rsmd = rs.getMetaData();
        //String name = rsmd.getColumnName(1);
        int column_count = rsmd.getColumnCount();

        while (rs.next()) {
            //if (withId)  callback.onRow(row_id);
            //row_id = rs.getString(1);
            if (id_column_idx != -1) {
                row_id = rs.getString(id_column_idx);
                //onRowId(row_id);
            }
            onRow(row_id);

            for (int i = 1; i <= column_count; i++) {
                onColumn(rs, i);
            }

        }

        onDone();

        rs.close();
    }


}
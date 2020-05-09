package ru.pyur.tst.db;

import ru.pyur.tst.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static ru.pyur.tst.db.Var.VAR_TYPE_INT;
import static ru.pyur.tst.db.Var.VAR_TYPE_STRING;

public class DbFetch {

    private Connection connection;

    private ArrayList<String> tables = new ArrayList<>();
    private ArrayList<String> columns = new ArrayList<>();
    private ArrayList<String> where = new ArrayList<>();
    private ArrayList<Var> where_args = new ArrayList<>();



    public DbFetch() {}

    public DbFetch(Connection connection) {
        this.connection = connection;
    }

    public void setConnection(Connection connection) { this.connection = connection; }



//    public DbFetch(Connection connection, String table, String[] columns, String where, String[] args) {
//        this.connection = connection;
//        this.table = table;
//        this.columns = columns;
//        this.where = where;
//        this.args = args;
//    }


    //public DbFetch(String table, String[] columns, String where, String[] args) {}


    public void table(String table) { tables.add("`" + table + "`"); }

    public void table(String[] tables) {
        for (String table : tables) {
            this.tables.add("`" + table + "`");
        }
    }


    public void col(String[] columns) {
        for (String column : columns) {
            this.columns.add("`" + column + "`");
        }
    }

    public void rawCol(String[] columns) {
        for (String column : columns) {
            this.columns.add(column);
        }
    }

    public void col(String column) { columns.add("`" + column + "`"); }

    public void rawCol(String column) { columns.add(column); }


    public void where(String[] where) {
        for (String wh : where) {
            this.where.add(wh);
        }
    }

    public void where(String where) { this.where.add(where); }


    //public void wa(String[] whargs) {
    //    for (String wa : whargs) {
    //        this.where_args.add(wa);
    //    }
    //}

    public void wa(int wa) { this.where_args.add(new Var(wa)); }

    public void wa(String wa) { this.where_args.add(new Var(wa)); }




    protected ResultSet getResultSet() throws Exception {
        if (tables.size() == 0) throw new Exception("table is not specified.");

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        if (columns.size() != 0) {
            sb.append(Util.implode(", ", columns));
        } else {
            sb.append("COUNT(*)");
        }

        sb.append(" FROM ");

        sb.append(Util.implode(", ", tables));

        if (where.size() != 0) {
            sb.append(" WHERE ");
            sb.append(Util.implode(" AND ", where));
        }

        // todo: order, limit, group by

        String query = sb.toString();
        //System.out.println("query: [" + query + "]");


        PreparedStatement ps = connection.prepareStatement(query);

        int i = 1;
        for (Var wa : where_args) {
            switch (wa.getType()) {
                case VAR_TYPE_INT:
                    ps.setInt(i, wa.getInt());
                    break;

                case VAR_TYPE_STRING:
                    ps.setString(i, wa.getString());
                    break;
            }
            i++;
        }

        ResultSet result_set = ps.executeQuery();

        //if (rs.next()) {
        //    value = rs.getString(1);
        //}

        return result_set;
    }



    public FetchArray fetchArray() throws Exception {
        return new FetchArray(getResultSet());
    }




    public FetchSingle fetchSingle() throws Exception {
        return new FetchSingle(getResultSet());
    }

}
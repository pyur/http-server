package ru.pyur.tst.db;

import ru.pyur.tst.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DbFetch {

    private Connection connection;
    private PreparedStatement ps;
    private ResultSet result_set;

    private ArrayList<String> tables = new ArrayList<>();
    private ArrayList<String> columns = new ArrayList<>();
    private ArrayList<String> where = new ArrayList<>();
    private ArrayList<Var> where_args = new ArrayList<>();
    private ArrayList<String> orders = new ArrayList<>();

    private String m_query;



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


    public void order(String[] orders) {
        for (String order : orders) {
            this.orders.add("`" + order + "`");
        }
    }

    public void order(String order) { orders.add("`" + order + "`"); }

    public void rawOrder(String order) { orders.add(order); }


    public void query(String query) { m_query = query; }




    protected ResultSet getResultSet() throws Exception {
        if (tables.size() == 0 && m_query == null) throw new Exception("table is not specified.");

        String query;

        if (m_query == null) {
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

            if (orders.size() != 0) {
                sb.append(" ORDER BY ");
                sb.append(Util.implode(", ", orders));
            }

            // todo: limit, group by

            query = sb.toString();
        }

        else {
            query = m_query;
        }
        System.out.println("query: [" + query + "]");


//        PreparedStatement
        ps = connection.prepareStatement(query);

        int i = 1;
        for (Var wa : where_args) {
            wa.applyToPreparedStatement(ps, i);
            i++;
        }

        ResultSet result_set = ps.executeQuery();

        return result_set;
    }




    public void fetchQuery(String query) throws Exception {
        Statement stmt = connection.createStatement();
        stmt.executeQuery(query);
        stmt.close();
    }




    public FetchedArray fetchArray() throws Exception {
        result_set = getResultSet();
        return new FetchedArray(result_set);
//        return new FetchArray(getResultSet());
    }




    public FetchedSingle fetchSingle() throws Exception {
        result_set = getResultSet();
        return new FetchedSingle(result_set);
//        return new FetchSingle(getResultSet());
    }



    public void finish() {
//        if (result_set != null) {
//            System.out.println("ResultSet closed.");
//            try {
//                result_set.close();
//            } catch (Exception e) { e.printStackTrace(); }
//        }
        if (ps != null) {
            System.out.println("PreparedStatement closed.");
            try {
                ps.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

}
package ru.pyur.tst.db;

import ru.pyur.tst.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import static ru.pyur.tst.db.Var.VAR_TYPE_INT;
import static ru.pyur.tst.db.Var.VAR_TYPE_STRING;

public class DbDelete {

    private Connection connection;

    private String table;
    private ArrayList<PVar> values = new ArrayList<>();
    private ArrayList<String> where = new ArrayList<>();
    private ArrayList<Var> where_args = new ArrayList<>();




    public DbDelete(Connection connection) {
        this.connection = connection;
    }



    public void table(String table) { this.table = table; }


    public void set(String column, int value) {
        this.values.add(new PVar(column, value));
    }

    public void set(String column, String value) {
        this.values.add(new PVar(column, value));
    }


    public void where(String[] where) {
        for (String wh : where) {
            this.where.add(wh);
        }
    }

    public void where(String where) { this.where.add(where); }


    public void wa(int wa) { this.where_args.add(new Var(wa)); }

    public void wa(String wa) { this.where_args.add(new Var(wa)); }




    public int delete() throws Exception {
        if (table == null) throw new Exception("table is not specified.");
        // where strict
        if (where.size() == 0) throw new Exception("strict. where is not specified.");

        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");

        sb.append("`");
        sb.append(table);
        sb.append("`");


        if (where.size() != 0) {
            sb.append(" WHERE ");
            sb.append(Util.implode(" AND ", where));
        }

        String query = sb.toString();


        PreparedStatement ps = connection.prepareStatement(query);

        int i = 1;
        for (Var wa : where_args) {
            switch (wa.getType()) {
                case VAR_TYPE_INT:
                    ps.setInt(i + 1, wa.getInt());
                    break;

                case VAR_TYPE_STRING:
                    ps.setString(i + 1, wa.getString());
                    break;
            }
            i++;
        }


        int delete_result = ps.executeUpdate();

        return delete_result;
    }



    //public int updateSingle(..., ..., ...) throws Exception {
    //}

}
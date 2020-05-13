package ru.pyur.tst.db;

import ru.pyur.tst.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import static ru.pyur.tst.db.Var.VAR_TYPE_BYTE_ARRAY;
import static ru.pyur.tst.db.Var.VAR_TYPE_INT;
import static ru.pyur.tst.db.Var.VAR_TYPE_STRING;

public class DbUpdate {

    private Connection connection;

    private String table;
    private ArrayList<PVar> values = new ArrayList<>();
    private ArrayList<String> where = new ArrayList<>();
    private ArrayList<Var> where_args = new ArrayList<>();




    public DbUpdate(Connection connection) {
        this.connection = connection;
    }



    public void table(String table) { this.table = table; }


    public void set(String column, int value) {
        this.values.add(new PVar(column, value));
    }

    public void set(String column, String value) {
        this.values.add(new PVar(column, value));
    }

    public void set(String column, byte[] value) {
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




    //private ResultSet getResultSet() throws Exception {
    public int update() throws Exception {
        if (table == null) throw new Exception("table is not specified.");
        if (values.size() == 0) throw new Exception("values is not specified.");
        // where strict
        if (where.size() == 0) throw new Exception("strict. where is not specified.");

        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");

        sb.append("`");
        sb.append(table);
        sb.append("`");

        sb.append(" SET ");

        ArrayList<String> vals = new ArrayList<>();
        for (PVar value : values) {
            vals.add(value.key + " = ?");
        }
        sb.append(Util.implode(", ", vals));


        if (where.size() != 0) {
            sb.append(" WHERE ");
            sb.append(Util.implode(" AND ", where));
        }

        String query = sb.toString();


        PreparedStatement ps = connection.prepareStatement(query);

        int i = 1;
        for (PVar value : values) {
            Var val = value.value;
//            switch (val.getType()) {
//                case VAR_TYPE_INT:
//                    ps.setInt(i, val.getInt());
//                    break;
//
//                case VAR_TYPE_STRING:
//                    ps.setString(i, val.getString());
//                    break;
//
//                case VAR_TYPE_BYTE_ARRAY:
//                    ps.setBytes(i, val.getBytes());
//                    break;
//            }
            val.applyToPreparedStatement(ps, i);
            i++;
        }


        for (Var wa : where_args) {
//            switch (wa.getType()) {
//                case VAR_TYPE_INT:
//                    ps.setInt(i, wa.getInt());
//                    break;
//
//                case VAR_TYPE_STRING:
//                    ps.setString(i, wa.getString());
//                    break;
//            }
            wa.applyToPreparedStatement(ps, i);
            i++;
        }


        int update_result = ps.executeUpdate();

        return update_result;
    }



//    public int update() throws Exception {
//        int update_result =
//
//
//    }




    //public int updateSingle(..., ..., ...) throws Exception {
    //}

}
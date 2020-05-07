package ru.pyur.tst.db;

import ru.pyur.tst.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import static ru.pyur.tst.db.Var.VAR_TYPE_INT;
import static ru.pyur.tst.db.Var.VAR_TYPE_STRING;

public class DbInsert {

    private Connection connection;

    private String table;
    private ArrayList<PVar> values = new ArrayList<>();



    public DbInsert(Connection connection) {
        this.connection = connection;
    }



    public void table(String table) { this.table = table; }


    public void set(String column, int value) {
        this.values.add(new PVar(column, value));
    }

    public void set(String column, String value) {
        this.values.add(new PVar(column, value));
    }




    public int insert() throws Exception {
        if (table == null) throw new Exception("table is not specified.");
        if (values.size() == 0) throw new Exception("values is not specified.");

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");

        sb.append("`");
        sb.append(table);
        sb.append("`");

        sb.append(" (");
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> vals = new ArrayList<>();
        for (PVar value : values) {
            names.add(value.key);
            vals.add("?");
        }
        sb.append(Util.implode(", ", names));

        sb.append(") VALUES (");

        sb.append(Util.implode(", ", vals));

        sb.append(")");


        String query = sb.toString();


        PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        int i = 1;
        for (PVar value : values) {
            Var val = value.value;
            switch (val.getType()) {
                case VAR_TYPE_INT:
                    ps.setInt(i, val.getInt());
                    break;

                case VAR_TYPE_STRING:
                    ps.setString(i, val.getString());
                    break;
            }
            i++;
        }


        int insert_result = ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        int last_inserted_id = 0;
        if (rs.next()) {
            last_inserted_id = rs.getInt(1);
        }

        return last_inserted_id;
    }



    //public int insertSingle(..., ..., ...) throws Exception {
    //}

}
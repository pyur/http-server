package ru.pyur.tst.dbedit;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
import ru.pyur.tst.tags.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Md_TableView extends Module {


    public Md_TableView(Session session) {
        this.session = session;
        parseSession();
    }



    public void prepare() {
        connectToDb();

        headerBegin();

        b("Колонки таблицы");


        Table table = new Table();

        table.addColumn("Имя", 150);
        table.addColumn("Тип", 200);
        table.addColumn("NULL", 50);
        table.addColumn("Key", 60);
        table.addColumn("Default", 180);
        table.addColumn("Extra", 250);


        try {
            String db_name = getQuery("db");
            if (db_name == null)  throw new Exception("db absent");

            String table_name = getQuery("tbl");
            if (table_name == null)  throw new Exception("table absent");

            Statement stmt = m_conn.createStatement();

            String query_1 = "USE `" + db_name + "`";

            stmt.executeQuery(query_1);


            // ----

            String query = "SHOW COLUMNS FROM `" + table_name + "`";

            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                String column_name = rs.getString(1);
                String data_type = rs.getString(2);
                String is_nullable = rs.getString(3);
                String keys = rs.getString(4);
                String default_value = rs.getString(5);
                String extra = rs.getString(6);

                Tr tr = new Tr();
                table.add(tr);

                tr.add(new Td(column_name));

                tr.add(new Td(data_type));

                tr.add(new Td(is_nullable));
                tr.add(new Td(keys));
                tr.add(new Td(default_value));
                tr.add(new Td(extra));
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        b(table);


        //Div outer_div = new Div();
//        A outer_div = new A();
//
//        outer_div.setLink("/aaa/bbb/");
//
//        Div inner_div_1 = new Div();
//        inner_div_1.put("first");
//
//        Div inner_div_2 = new Div();
//        inner_div_2.put("second");
//
//        Div inner_div_3 = new Div();
//        inner_div_3.put("third");
//
//        outer_div.addTag(inner_div_1);
//        outer_div.addTag(inner_div_2);
//        outer_div.addTag(inner_div_3);
//
//        b(outer_div);


        headerEnd();

        closeDb();
    }





}
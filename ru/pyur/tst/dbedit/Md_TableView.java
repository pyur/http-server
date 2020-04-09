package ru.pyur.tst.dbedit;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
import ru.pyur.tst.tags.Table;
import ru.pyur.tst.tags.Td;
import ru.pyur.tst.tags.Tr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static ru.pyur.tst.tags.Table.TABLE_COLUMN_ALIGN_LEFT;


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
                String c3 = rs.getString(3);
                String c4 = rs.getString(4);
                String c5 = rs.getString(5);
                String c6 = rs.getString(6);

                Tr tr = new Tr();
                table.addTr(tr);

                tr.addTd(new Td(column_name));

                tr.addTd(new Td(data_type));

                tr.addTd(new Td(c3));
                tr.addTd(new Td(c4));
                tr.addTd(new Td(c5));
                tr.addTd(new Td(c6));
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        b(table.render());



        headerEnd();

        closeDb();
    }





}
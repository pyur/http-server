package ru.pyur.tst.dbedit;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
import ru.pyur.tst.tags.A;
import ru.pyur.tst.tags.Table;
import ru.pyur.tst.tags.Td;
import ru.pyur.tst.tags.Tr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Md_TableList extends Module {



    public Md_TableList(Session session) {
        this.session = session;
        parseSession();
    }


    public void prepare() {
        connectToDb();

        headerBegin();

        b("Таблицы базы данных");


        Table table = new Table();

        table.addColumn("Имя", 200);


        try {
            String db_name = getQuery("db");
            if (db_name == null)  throw new Exception("db absent");

            Statement stmt = m_conn.createStatement();

            String query_1 = "USE `" + db_name + "`";
            //query('SET CHARACTER SET utf8');

            stmt.executeQuery(query_1);


            // ----

            String query = "SHOW TABLES";

            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                String table_name = rs.getString(1);

                Tr tr = new Tr();
                table.add(tr);

                Td td_db_name = new Td();

                A link = new A();
                link.setLink("/" + getModule() + "/tb/?db=" + db_name + "&tbl=" + table_name);
                link.put(table_name);

                td_db_name.add(link);
                tr.add(td_db_name);
            }

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        b(table);


        headerEnd();

        closeDb();
    }





}
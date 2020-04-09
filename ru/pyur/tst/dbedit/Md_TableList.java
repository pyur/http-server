package ru.pyur.tst.dbedit;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
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
                table.addTr(tr);

                tr.addTd(new Td(table_name));
            }

        } catch (SQLException se) {
            //Handle errors for JDBC
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
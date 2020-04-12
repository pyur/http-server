package ru.pyur.tst.resources;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
import ru.pyur.tst.tags.*;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Md_ResList extends Module {

    public Md_ResList(Session session) {
        this.session = session;
        parseSession();
    }



    @Override
    public void prepare() {

        final String DB_URL = "jdbc:sqlite:config.db";
        //final String DB_USER = "root";
        //final String DB_PASSWORD = "1";

        try {
            m_conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }



        b("Таблицы базы данных");

        Table table = new Table();
        b(table);

        table.addColumn("Имя", 200);


        try {
//            String db_name = getQuery(DBEDIT_PARAM_DB);
//            if (db_name == null)  throw new Exception("db absent");

            Statement stmt = m_conn.createStatement();

//            String query_1 = "USE `" + db_name + "`";
//            //query('SET CHARACTER SET utf8');

//            stmt.executeQuery(query_1);


            // ----

            String query = "SELECT `name` FROM `sqlite_master`";

            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                String table_name = rs.getString(1);

                Tr tr = new Tr();
                table.add(tr);

                Td td_db_name = new Td();

                A link = new A();

                Url href = new Url();
                href.setModule(getModule());
//                href.setAction(DBEDIT_ACTION_TABLE);
//                href.addParameter(DBEDIT_PARAM_DB, db_name);
//                href.addParameter(DBEDIT_PARAM_TABLE, table_name);

                link.setHref(href);

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

    }


}
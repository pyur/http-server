package ru.pyur.tst.resources;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;
import ru.pyur.tst.tags.*;

import java.sql.*;


public class Md_ResList extends HtmlContent {

    public Md_ResList(HttpSession session) {
        initHtml(session);
    }



    @Override
    public void makeContent() {

        text("Список таблиц конфига");

        Table table = new Table();
        tag(table);

        table.addColumn("Имя", 200);


        try {
//            String db_name = getQuery(DBEDIT_PARAM_DB);
//            if (db_name == null)  throw new Exception("db absent");

//x            getConfigDb();
            Statement stmt = getConfigStatement();

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
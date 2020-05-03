package ru.pyur.tst.dbedit;

import ru.pyur.tst.HttpSession;
import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.tags.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static ru.pyur.tst.dbedit.Info.DBEDIT_ACTION_TABLE;
import static ru.pyur.tst.dbedit.Info.DBEDIT_PARAM_DB;
import static ru.pyur.tst.dbedit.Info.DBEDIT_PARAM_TABLE;


public class Md_TableList extends HtmlContent {



    public Md_TableList(HttpSession session) {
        initHtml(session);
    }



    @Override
    public void makeContent() {

        text("Таблицы базы данных");


        Table table = new Table();
        tag(table);

        table.addColumn("Имя", 200);


        try {
            String db_name = getQuery(DBEDIT_PARAM_DB);
            //if (db_name == null)  throw new Exception("db absent");

            Statement stmt = null;  // TODO getDb();

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

                Url href = new Url();
                href.setModule(getModule());
                href.setAction(DBEDIT_ACTION_TABLE);
                href.addParameter(DBEDIT_PARAM_DB, db_name);
                href.addParameter(DBEDIT_PARAM_TABLE, table_name);

                //link.setLink("/" + getModule() + "/" + DBEDIT_ACTION_TABLE + "/?" + DBEDIT_PARAM_DB + "=" + db_name + "&" + DBEDIT_PARAM_TABLE + "=" + table_name);
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
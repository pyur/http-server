package ru.pyur.tst.dbedit;

import ru.pyur.tst.HttpModule;
import ru.pyur.tst.HttpSession;
import ru.pyur.tst.tags.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static ru.pyur.tst.dbedit.Info.DBEDIT_ACTION_TABLE_EDIT;
import static ru.pyur.tst.dbedit.Info.DBEDIT_PARAM_DB;
import static ru.pyur.tst.dbedit.Info.DBEDIT_PARAM_TABLE;


public class Md_TableView extends HttpModule {


    public Md_TableView(HttpSession session) {
        initHtml(session);
    }



    @Override
    public void makeContent() {

        b("Колонки таблицы");


        Table table = new Table();
        b(table);

        table.addColumn("Имя", 150);
        table.addColumn("Тип", 50);  // 200
        table.addColumn("NULL", 50);
        table.addColumn("Key", 60);
        table.addColumn("Default", 180);
        table.addColumn("Extra", 250);

        table.addAction("pencil-button");  // edit
        table.addAction("rainbow");  // some action 2
        table.addAction("holly");  // some action 3
        //table.addAction("wheel");  // some action 4



        try {
            String db_name = getQuery(DBEDIT_PARAM_DB);
            //if (db_name == null)  throw new Exception("db absent");

            String table_name = getQuery(DBEDIT_PARAM_TABLE);
            //if (table_name == null)  throw new Exception("table absent");

            Statement stmt = getDb();

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

                Url url = new Url();
                url.setModule(getModule());
                url.setAction(DBEDIT_ACTION_TABLE_EDIT);
                url.addParameter(DBEDIT_PARAM_DB, db_name);  // ?
                url.addParameter(DBEDIT_PARAM_TABLE, table_name);
                url.addParameter("col", column_name);
                tr.addAction(url);

                url = new Url();
                url.setModule(getModule());
                url.setAction("act2");
                url.addParameter(DBEDIT_PARAM_DB, db_name);  // ?
                url.addParameter(DBEDIT_PARAM_TABLE, table_name);
                url.addParameter("col", column_name);
                tr.addAction(url);

                url = new Url();
                url.setModule(getModule());
                url.setAction("act3");
                url.addParameter(DBEDIT_PARAM_DB, db_name);  // ?
                url.addParameter(DBEDIT_PARAM_TABLE, table_name);
                url.addParameter("col", column_name);
                tr.addAction(url);
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
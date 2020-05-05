package ru.pyur.tst.dbedit;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;
import ru.pyur.tst.tags.*;

import java.sql.ResultSet;
import java.sql.Statement;

import static ru.pyur.tst.dbedit.Info.DBEDIT_PARAM_DB;
import static ru.pyur.tst.dbedit.Info.DBEDIT_PARAM_TABLE;


public class Md_TableView extends HtmlContent {


//    public Md_TableView(HttpSession session) {
//        initHtml(session);
//    }



    @Override
    public void makeHtml() throws Exception {
        String table_name = getQuery(DBEDIT_PARAM_TABLE);

        heading("Колонки таблицы – " + table_name);


        Table table = new Table("lmt");  // todo: lst
        tag(table);

        table.addColumn("Имя", 150);
        table.addColumn("Тип", 200);
        table.addColumn("NULL", 50);
        table.addColumn("Key", 60);
        table.addColumn("Default", 180);
        table.addColumn("Extra", 250);

        table.addAbLocation("pencil-button", "Редактировать", getModule(), "edit_table");  // edit
        table.addAbLocation("rainbow", "Радуга", getModule(), "rnbw");  // some action 2
        table.addAbLocation("holly", "Ягодка", getModule(), "hly");  // some action 3


        String db_name = getQuery(DBEDIT_PARAM_DB);
        //if (db_name == null)  throw new Exception("db absent");

//up            String table_name = getQuery(DBEDIT_PARAM_TABLE);
        //if (table_name == null)  throw new Exception("table absent");

        Statement stmt = null;  // TODO getDb();

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

            //Tr tr = new Tr(row_id);
            Tr tr = new Tr(column_name);
//todo                Tr tr = new Tr();
//todo                tr.addData("opt", column_name);
            table.add(tr);

            tr.add(new Td(column_name));

            tr.add(new Td(data_type));

            tr.add(new Td(is_nullable));
            tr.add(new Td(keys));
            tr.add(new Td(default_value));
            tr.add(new Td(extra));

            }

//        } catch (SQLException se) {
//            se.printStackTrace();
//        }

    }

}
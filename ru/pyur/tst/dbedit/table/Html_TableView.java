package ru.pyur.tst.dbedit.table;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.db.DbFetch;
import ru.pyur.tst.dbedit.DbEditCommon;
import ru.pyur.tst.tags.*;

import java.sql.Connection;


public class Html_TableView extends HtmlContent {

    @Override
    public void makeHtml() throws Exception {
//        String table_name = "";
//        String db_name = "";

        String host_id = getCookie("host");
        String db_name = getCookie("db");
        //String table_name = getCookie("db");

        String table_name = getParam("table");  // todo getParamFiltered()
        //if (table_name == null) { table_name = getCookie("db"); }
        //else { setCookie("table", table_name, 2000000000, "/"); }


        if (host_id == null || db_name == null || table_name == null) {
            add("host, db or table not specified.");
            return;
        }


        Connection conn = DbEditCommon.getDatabase(getConfigDb(), host_id);

        {
            ModuleUrl url = new ModuleUrl();
            url.setModule(getModule());
            url.setAction("add_column");
//            url.addParameter(DBEDIT_PARAM_HOST, host_id);
//            url.addParameter(DBEDIT_PARAM_DB, host_id);
            addActionLink("Добавить колонку", url, "plus-button");
        }


        heading("Колонки таблицы – " + table_name);

/*
        Table table = new Table("lmt");  // todo: lst
        add(table);

        table.addColumn("Имя", 150);
        table.addColumn("Тип", 200);
        table.addColumn("NULL", 50);
        table.addColumn("Key", 60);
        table.addColumn("Default", 180);
        table.addColumn("Extra", 250);

        table.addAbLocation("pencil-button", "Редактировать", getModule(), "edit_table");  // edit
        table.addAbLocation("rainbow", "Радуга", getModule(), "rnbw");  // some action 2
        table.addAbLocation("holly", "Ягодка", getModule(), "hly");  // some action 3


//        String db_name = getParam(DBEDIT_PARAM_DB);

        String query = "USE `" + db_name + "`";

//x        //query(query);
        Statement stmt = conn.createStatement();
        stmt.executeQuery(query);
        stmt.close();


        // ----

        query = "SHOW COLUMNS FROM `" + table_name + "`";

//x        ResultSet rs = query(query);
        stmt = conn.createStatement();
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

        stmt.close();
*/

        DbFetch set_db = new DbFetch(conn);
        set_db.fetchQuery("USE `" + db_name + "`");

        TableFetcher columns_table = new ColumnsTable(conn, table_name);
        add(columns_table.make());

    }




    private class ColumnsTable extends TableFetcher {

        private String table_name;

        public ColumnsTable(Connection conn, String table_name) {
            setConnection(conn);
            this.table_name = table_name;
        }


        @Override
        public Tag make() {

            //table("db");
            //col(new String[]{"id", "host", "port", "login"});
            //where("`id` = 2");
            query("SHOW COLUMNS FROM `" + table_name + "`");
            //query("SHOW COLUMNS FROM `?`");
            //wa(table_name);
            //query("SELECT * FROM `fence` WHERE `id` = ?");
            //wa("100");

            setIdColumn(1);

            fetchTable();

            return tag;
        }



        @Override
        protected void onTableFetch() {
            addColumn("Имя", 150);
            addColumn("Тип", 200);
            addColumn("NULL", 50);
            addColumn("Key", 60);
            addColumn("Default", 180);
            addColumn("Extra", 250);

            table.setActionIconsDb(getConfigDb());

            addAbLocation("pencil-button", "Редактировать", getModule(), "edit_table");  // edit
            addAbLocation("rainbow", "Радуга", getModule(), "rnbw");  // some action 2
            addAbLocation("holly", "Ягодка", getModule(), "hly");  // some action 3
        }



//        @Override
//        public Tag onTableColumnTag(int column_num, String value) {
//            if (column_num != 2)  return null;
//
//            A link = new A();
//
//            ModuleUrl href = new ModuleUrl();
//            href.setModule("db");
//            //href.setAction(DBEDIT_ACTION_HOST_LIST);
//            href.addParameter(DBEDIT_PARAM_HOST, getRowId());
//
//            link.setHref(href);
//
//            link.add(value);
//
//            return link;
//        }

    }

}
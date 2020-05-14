package ru.pyur.tst.dbedit.db;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.dbedit.DbEditCommon;
import ru.pyur.tst.tags.*;

import java.sql.*;


public class Html_DbList extends HtmlContent {

    @Override
    public void makeHtml() throws Exception {
        String host_id = getParam("host");  // todo getParamInt()
        if (host_id == null) {
            host_id = getCookie("host");
        }
        else {
            setCookie("host", host_id, 2000000000, "/");
        }

        if (host_id == null) {
            add("host not specified.");
            return;
        }


        Connection conn = DbEditCommon.getDatabase(getConfigDb(), host_id);

        {
            ModuleUrl url = new ModuleUrl();
            url.setModule(getModule());
            url.setAction("edit");
            //url.addParameter("?!?", host_id);  // from cookie
            addActionLink("Добавить базу данных", url, "plus-button");
        }


        heading("Базы данных");

//        TableFetcher host_fetcher = new DatabaseTable(conn);
//        Tag table = host_fetcher.make();
//        add(table);

        Table table = new Table();
        add(table);

        table.addColumn("Имя", 200);


        String query = "SHOW DATABASES";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);


        while(rs.next()) {
            String db_name = rs.getString(1);

            Tr tr = new Tr();
            table.add(tr);

            Td td_database = new Td();
            tr.add(td_database);

            A link = new A();
            td_database.add(link);

            ModuleUrl href = new ModuleUrl();
            href.setModule("table");
            //href.setAction("");
            //href.addParameter(DBEDIT_PARAM_HOST, host_id);
            href.addParameter("db", db_name);

            link.setHref(href);

            link.add(db_name);

        }

    }



/*
    private class DatabaseTable extends TableFetcher {

        public DatabaseTable(Connection conn) {
            setConnection(conn);
        }


        @Override
        public Tag make() {

            table("db");
            col(new String[]{"id", "host", "port", "login"});
            //where("`id` = 2");

            fetchTable();

            return tag;
        }



        @Override
        protected void onTableFetch() {
            addColumn("№", 30);
            addColumn("Хост", 120);
            addColumn("Порт", 60);
            addColumn("Логин", 100);
        }



        @Override
        public Tag onTableColumnTag(int column_num, String value) {
            if (column_num != 2)  return null;

            A link = new A();

            ModuleUrl href = new ModuleUrl();
            href.setModule(getModule());
            href.setAction(DBEDIT_ACTION_DB_LIST);
            href.addParameter(DBEDIT_PARAM_HOST, getRowId());

            link.setHref(href);

            link.add(value);

            return link;
        }

    }
*/

}
package ru.pyur.tst.dbedit.dbedit;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.tags.*;

import java.sql.*;

import static ru.pyur.tst.dbedit.dbedit.Info.DBEDIT_ACTION_DB_LIST;
import static ru.pyur.tst.dbedit.dbedit.Info.DBEDIT_PARAM_DB;


public class Md_DbList extends HtmlContent {

    @Override
    public void makeHtml() throws Exception {
        String host_id = getParam("host");  // todo: getFiltered()
        Connection conn = DbEditCommon.getDatabase(getConfigDb(), host_id);

        {
            ModuleUrl url = new ModuleUrl();
            url.setModule(getModule());
            url.setAction("db_edit");
            url.addParameter("host", host_id);
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
            href.setModule(getModule());
            href.setAction(DBEDIT_ACTION_DB_LIST);
            href.addParameter(DBEDIT_PARAM_DB, db_name);

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
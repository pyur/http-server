package ru.pyur.tst.sample_host.dbedit;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.tags.*;

import java.sql.Connection;

import static ru.pyur.tst.sample_host.dbedit.Info.DBEDIT_ACTION_DB_LIST;
import static ru.pyur.tst.sample_host.dbedit.Info.DBEDIT_PARAM_HOST;


public class Md_HostList extends HtmlContent {

/* version 1
    @Override
    public void makeHtml() throws Exception {

        heading("Хосты баз данных");


        Table table = new Table();
        add(table);

        table.addColumn("№", 30);
        table.addColumn("Хост", 120);
        table.addColumn("Порт", 60);
        table.addColumn("Логин", 100);


        DbFetch db_db = new DbFetch(getModuleDb());
        db_db.table("db");
        db_db.col(new String[]{"id", "host", "port", "login"});

        FetchArray fetch = db_db.fetchArray();

        while(fetch.available()) {
            int id = fetch.getInt("id");
            String host = fetch.getString("host");
            int port = fetch.getInt("port");
            String login = fetch.getString("login");

            Tr tr = new Tr();
            table.add(tr);

            {
                Td r_id = new Td();
                tr.add(r_id);

                r_id.add(id);
            }

            {
                Td r_host = new Td();
                tr.add(r_host);

                A link = new A();
                r_host.add(link);

                ModuleUrl href = new ModuleUrl();
                href.setModule(getModule());
                href.setAction(DBEDIT_ACTION_DB_LIST);
                href.addParameter(DBEDIT_PARAM_HOST, id);

                link.setHref(href);

                link.add(host);
            }

            {
                Td r_port = new Td();
                tr.add(r_port);

                r_port.add(port);
            }

            {
                Td r_login = new Td();
                tr.add(r_login);

                r_login.add(login);
            }

        }

//todo        conn.close();
    }
*/


    // version 2
    @Override
    public void makeHtml() throws Exception {

        heading("Хосты баз данных");

        TableFetcher host_fetcher = new HostTable(getModuleDb());

        //ArrayList<Tag> tags = host_fetcher.make();
        //Table table = host_fetcher.make();
        Tag table = host_fetcher.make();

        add(table);


    }




    private class HostTable extends TableFetcher {

        public HostTable(Connection conn) {
            setConnection(conn);
        }


        @Override
        //public Table make() {
        public Tag make() {
//x            initTable(cb_table);
            initTable();

            table("db");
            col(new String[]{"id", "host", "port", "login"});
            //where("`id` = 2");

            addColumn("№", 30);
            addColumn("Хост", 120);
            addColumn("Порт", 60);
            addColumn("Логин", 100);

            fetchTable();

            return tag;
        }



//x        private TableCallback cb_table = new TableCallback() {

//            @Override
//            public void onFetch() {  // todo: abolish
//                // fetch success. prepare table, etc.
//
//                //todo: column description in 'make'
//                table.addColumn("№", 30);
//                table.addColumn("Хост", 120);
//                table.addColumn("Порт", 60);
//                table.addColumn("Логин", 100);
//            }


//x            @Override
//x            public void onTableRow() {
//x                // add row to table, etc
//x            }


//x            @Override
//x            public String onTableColumnString(int column_num, String value) { return null; }


            @Override
            public Tag onTableColumnTag(int column_num, String value) {
                // add cell to table row

                if (column_num != 2)  return null;

                A link = new A();

                ModuleUrl href = new ModuleUrl();
                href.setModule(getModule());
                href.setAction(DBEDIT_ACTION_DB_LIST);
                href.addParameter(DBEDIT_PARAM_HOST, "todo_id");

                link.setHref(href);

                link.add(value);

                return link;
            }


            //@Override
            //public boolean onColumn(int column_num, String value) { return false; }

//x        };

    }



}
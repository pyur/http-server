package ru.pyur.tst.sample_host.dbedit;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.db.DbFetch;
import ru.pyur.tst.db.DbFetcher;
import ru.pyur.tst.tags.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

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

        TableFetcher host_fetcher = new TableFetcher(getModuleDb());

        //ArrayList<Tag> tags = host_fetcher.make();
        Table table = host_fetcher.make();

        add(table);


    }




//    private class HostFetcher extends TableFetcher {
//    }

    //base for generic table fetcher
    private class TableFetcher extends DbFetcher {

        //private ArrayList<Tag> tags = new ArrayList<>();

        private Table table;
        private Tr tr;


        public TableFetcher(Connection conn) {
            setResultCallback(cb_result);
            //db = new DbFetch(conn);
            setConnection(conn);
        }


        // ---- setters, getters ----------------------------------------------------------------

//        protected void add(String str) { tags.add(new PlainText(str)); }

//        protected void add(int number) { tags.add(new PlainText(number)); }

//        protected void add(Tag tag) { tags.add(tag); }



        @Override
        public Table make() {
        //public ArrayList<Tag> make() {
//            DbFetch db_db = new DbFetch(getModuleDb());
//            db_db.table("db");
//            db_db.col(new String[]{"id", "host", "port", "login"});
            table("db");
            col(new String[]{"id", "host", "port", "login"});

            processResults();

            return table;
            //return tags;
        }



        private DbFetcher.Callback cb_result = new DbFetcher.Callback() {
            @Override
            public void onEmpty() {
                // fetch empty result
            }


            @Override
            public void onFetch() {
                // fetch success. prepare table, etc.

                table = new Table();
                //add(table);

                table.addColumn("№", 30);
                table.addColumn("Хост", 120);
                table.addColumn("Порт", 60);
                table.addColumn("Логин", 100);
            }


            @Override
            public void onRow() {
                // add row to table, etc
                //Tr tr = new Tr();
                tr = new Tr();
                table.add(tr);
            }

//            @Override
//            public void onColumn(ResultSet rs, int column_num) {
//                // add cell to table row
//                Td cell = new Td();
//                try {
//                    switch (column_num) {
//                        case 1:
//                            rs.getInt(column_num);
//                            break;
//
//                        case 2:
//                            rs.getString(column_num);
//                            break;
//
//                        case 3:
//                            rs.getInt(column_num);
//                            break;
//
//                        case 4:
//                            rs.getString(column_num);
//                            break;
//
//                    }
//                } catch (Exception e) { e.printStackTrace(); }
//
//                cell.add(text);
//            }


            @Override
            public void onColumnString(int column_num, String value) {
                // add cell to table row
                //Td cell = new Td(value);
                Td cell = new Td();
                tr.add(cell);

                if (column_num == 2) {
                    A link = new A();
                    cell.add(link);

                    ModuleUrl href = new ModuleUrl();
                    href.setModule(getModule());
                    href.setAction(DBEDIT_ACTION_DB_LIST);
                    href.addParameter(DBEDIT_PARAM_HOST, "todo_id");

                    link.setHref(href);

                    link.add(value);
                }

                else {
                    cell.add(value);
                }

            }
        };

    }



}
package ru.pyur.tst.sample_host.dbedit;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.tags.*;

import java.sql.Connection;

import static ru.pyur.tst.sample_host.dbedit.Info.DBEDIT_ACTION_DB_LIST;
import static ru.pyur.tst.sample_host.dbedit.Info.DBEDIT_PARAM_HOST;


public class Md_HostList extends HtmlContent {


    @Override
    public void makeHtml() throws Exception {

        heading("Хосты баз данных");

        TableFetcher host_fetcher = new HostTable(getModuleDb());
        Tag table = host_fetcher.make();
        add(table);

    }




    private class HostTable extends TableFetcher {

        public HostTable(Connection conn) {
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


}
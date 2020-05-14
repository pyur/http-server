package ru.pyur.tst.dbedit.host;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.tags.*;

import java.sql.Connection;

import static ru.pyur.tst.dbedit.host.Info.DBEDIT_ACTION_HOST_LIST;
import static ru.pyur.tst.dbedit.host.Info.DBEDIT_PARAM_HOST;


public class Html_HostList extends HtmlContent {


    @Override
    public void makeHtml() throws Exception {

        {
            ModuleUrl url = new ModuleUrl();
            url.setModule(getModule());
            url.setAction("add");
            //add params
            addActionLink("Добавить хост", url, "plus-button");
        }


        heading("Хосты баз данных");

        TableFetcher host_fetcher = new HostTable(getConfigDb());
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

            setIdColumn(1); // or in onTableFetch()

            fetchTable();

            return tag;
        }



        @Override
        protected void onTableFetch() {
            addColumn("№", 30);
            addColumn("Хост", 120);
            addColumn("Порт", 60);
            addColumn("Логин", 100);

            //setIdColumn(1);
        }



        @Override
        public String onTableColumn(int column_num, String value) {
            //if (column_num == 1)  return null;  // skip id
            if (column_num != 2)  return value;

            A link = new A();

            ModuleUrl href = new ModuleUrl();
            href.setModule("db");
            //href.setAction(DBEDIT_ACTION_HOST_LIST);
            href.addParameter(DBEDIT_PARAM_HOST, getRowId());

            link.setHref(href);

            link.add(value);

            return link.toString();
        }

    }


}
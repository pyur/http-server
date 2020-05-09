package ru.pyur.tst.sample_host.dbedit;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.tags.*;

import java.sql.*;

import static ru.pyur.tst.sample_host.dbedit.Info.DBEDIT_ACTION_DB_LIST;
import static ru.pyur.tst.sample_host.dbedit.Info.DBEDIT_PARAM_DB;


public class Md_DbList extends HtmlContent {

    @Override
    public void makeHtml() throws Exception {

        heading("Базы данных");


        Table table = new Table();
        add(table);

        table.addColumn("Имя", 200);


        String query = "SHOW DATABASES";

        ResultSet rs = query(query);

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


}
package ru.pyur.tst.dbedit;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.tags.*;

import java.sql.*;

import static ru.pyur.tst.dbedit.Info.DBEDIT_ACTION_DB;
import static ru.pyur.tst.dbedit.Info.DBEDIT_PARAM_DB;


public class Md_DbList extends HtmlContent {

    @Override
    public void makeHtml() throws Exception {

        heading("Базы данных");


        Table table = new Table();
        tag(table);

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

            Url href = new Url();
            href.setModule(getModule());
            href.setAction(DBEDIT_ACTION_DB);
            href.addParameter(DBEDIT_PARAM_DB, db_name);

            link.setHref(href);

            link.text(db_name);

        }

    }


}
package ru.pyur.tst.sample_host.resources;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.tags.*;

import java.sql.*;


public class Md_ResList extends HtmlContent {

    @Override
    public void makeHtml() throws Exception {

        heading("Список таблиц конфига");

        Table table = new Table();
        tag(table);

        table.addColumn("Имя", 200);


        Statement stmt = getConfigStatement();

        String query = "SELECT `name` FROM `sqlite_master`";

        ResultSet rs = stmt.executeQuery(query);

        while(rs.next()) {
            String table_name = rs.getString(1);

            Tr tr = new Tr();
            table.add(tr);

            Td td_db_name = new Td();

            A link = new A();

            Url href = new Url();
            href.setModule(getModule());

            link.setHref(href);

            link.text(table_name);

            td_db_name.add(link);
            tr.add(td_db_name);
        }

    }


}
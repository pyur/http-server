package ru.pyur.tst.dbedit.table;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.dbedit.DbEditCommon;
import ru.pyur.tst.tags.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class Html_TableList extends HtmlContent {

    @Override
    public void makeHtml() throws Exception {
        String host_id = getCookie("host");

        String db_name = getParam("db");  // todo getParamFiltered()
        if (db_name == null) { db_name = getCookie("db"); }
        else { setCookie("db", db_name, 2000000000, "/"); }

        if (host_id == null || db_name == null) {
            add("host or db not specified.");
            return;
        }

//        String host_id = getParam(DBEDIT_PARAM_HOST);
//        String db_id = getParam(DBEDIT_PARAM_DB);
        Connection conn = DbEditCommon.getDatabase(getConfigDb(), host_id);

        {
            ModuleUrl url = new ModuleUrl();
            url.setModule(getModule());
            url.setAction("add");
//            url.addParameter(DBEDIT_PARAM_HOST, host_id);
//            url.addParameter(DBEDIT_PARAM_DB, host_id);
            addActionLink("Добавить таблицу", url, "plus-button");
        }

        heading("Таблицы базы данных");


        Table table = new Table();
        add(table);

        table.addColumn("Имя", 200);


//        String db_name = getParam(DBEDIT_PARAM_DB);

        String query = "USE `" + db_name + "`";
        //query('SET CHARACTER SET utf8');

        //query(query);
        Statement stmt = conn.createStatement();
        stmt.executeQuery(query);


        // ----

        query = "SHOW TABLES";

        //ResultSet rs = query(query);
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while(rs.next()) {
            String table_name = rs.getString(1);

            Tr tr = new Tr();
            table.add(tr);

            Td td_table = new Td();
            tr.add(td_table);

            A link = new A();

            ModuleUrl url = new ModuleUrl();
            url.setModule(getModule());
            url.setAction("view");
//            url.addParameter(DBEDIT_PARAM_DB, db_name);
            url.addParameter("table", table_name);

            link.setHref(url);

            link.add(table_name);

            td_table.add(link);
        }


    }



}
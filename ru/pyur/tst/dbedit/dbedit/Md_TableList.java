package ru.pyur.tst.dbedit.dbedit;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.tags.*;

import java.sql.ResultSet;

import static ru.pyur.tst.dbedit.dbedit.Info.DBEDIT_ACTION_TABLE_LIST;
import static ru.pyur.tst.dbedit.dbedit.Info.DBEDIT_PARAM_DB;
import static ru.pyur.tst.dbedit.dbedit.Info.DBEDIT_PARAM_TABLE;


public class Md_TableList extends HtmlContent {

    @Override
    public void makeHtml() throws Exception {

        heading("Таблицы базы данных");


        Table table = new Table();
        add(table);

        table.addColumn("Имя", 200);


        String db_name = getParam(DBEDIT_PARAM_DB);

        String query = "USE `" + db_name + "`";
        //query('SET CHARACTER SET utf8');

        query(query);


        // ----

        query = "SHOW TABLES";

        ResultSet rs = query(query);

        while(rs.next()) {
            String table_name = rs.getString(1);

            Tr tr = new Tr();
            table.add(tr);

            Td td_table = new Td();
            tr.add(td_table);

            A link = new A();

            ModuleUrl url = new ModuleUrl();
            url.setModule(getModule());
            url.setAction(DBEDIT_ACTION_TABLE_LIST);
            url.addParameter(DBEDIT_PARAM_DB, db_name);
            url.addParameter(DBEDIT_PARAM_TABLE, table_name);

            link.setHref(url);

            link.add(table_name);

            td_table.add(link);
        }


    }



}
package ru.pyur.tst.dbedit;

import ru.pyur.tst.HttpSession;
import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.tags.*;

import java.sql.*;

import static ru.pyur.tst.dbedit.Info.DBEDIT_ACTION_DB;
import static ru.pyur.tst.dbedit.Info.DBEDIT_PARAM_DB;


public class Md_DbList extends HtmlContent {

    public Md_DbList(HttpSession session) {
        initHtml(session);
    }



    @Override
    public void makeHtml() throws Exception {

        heading("Базы данных");


        Table table = new Table();
        tag(table);

        table.addColumn("Имя", 200);


        try {
//x            Statement stmt = null;  // TODO getDb();

            String query = "SHOW DATABASES";

//x            ResultSet rs = stmt.executeQuery(query);
            ResultSet rs = query(query);

            while(rs.next()) {
                String db_name = rs.getString(1);

                Tr tr = new Tr();
                table.add(tr);

                Td td_db_name = new Td();

                A link = new A();

                Url href = new Url();
                href.setModule(getModule());
                href.setAction(DBEDIT_ACTION_DB);
                href.addParameter(DBEDIT_PARAM_DB, db_name);

                //link.setLink("/" + getModule() + "/" + DBEDIT_ACTION_DB + "/?" + DBEDIT_PARAM_DB + "=" + db_name);
                link.setHref(href);

                link.text(db_name);

                td_db_name.add(link);
                tr.add(td_db_name);
            }

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }

    }


}
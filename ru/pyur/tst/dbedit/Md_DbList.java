package ru.pyur.tst.dbedit;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
import ru.pyur.tst.tags.*;

import java.sql.*;

import static ru.pyur.tst.dbedit.Info.DBEDIT_ACTION_DB;
import static ru.pyur.tst.dbedit.Info.DBEDIT_PARAM_DB;


public class Md_DbList extends Module {

    public Md_DbList(Session session) {
        initHtml(session);
    }



    @Override
    public void makeContent() {

        b("Базы данных");


        Table table = new Table();
        b(table);

        table.addColumn("Имя", 200);


        try {
            Statement stmt = m_conn.createStatement();

            String query = "SHOW DATABASES";

            ResultSet rs = stmt.executeQuery(query);

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

                link.put(db_name);

                td_db_name.add(link);
                tr.add(td_db_name);
            }

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }

    }


}
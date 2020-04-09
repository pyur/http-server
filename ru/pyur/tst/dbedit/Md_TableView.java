package ru.pyur.tst.dbedit;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
import ru.pyur.tst.tags.Table;
import ru.pyur.tst.tags.Td;
import ru.pyur.tst.tags.Tr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Md_TableView extends Module {



    public Md_TableView(Session session) {
        this.session = session;
    }


    public void prepare() {
        connectToDb();

        headerBegin();

        b("Базы данных");


//        String table_name = getQuery("tbl");
//        if (table_name == null)  throw new Exception("tbl absent");

        Table table = new Table();

        try {
            Statement stmt = m_conn.createStatement();

            String sql = "SHOW DATABASES";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                String db_name = rs.getString(1);
                //String tmp = rs.getString(2);

                //System.out.println(id + "  " + name + "  " + cat + "  " + login + "  " + dtx + "  " + idx);
                Tr tr = new Tr();
                table.append(tr);

                tr.append(new Td(db_name));

                //tr.append(new Td(tmp));
            }

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }

        b(table.render());



        headerEnd();

        closeDb();
    }





}
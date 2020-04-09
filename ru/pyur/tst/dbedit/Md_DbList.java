package ru.pyur.tst.dbedit;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
import ru.pyur.tst.tags.Table;
import ru.pyur.tst.tags.Td;
import ru.pyur.tst.tags.Tr;

import java.sql.*;


public class Md_DbList extends Module {



    public Md_DbList(Session session) {
        this.session = session;
    }


    public void prepare() {
        connectToDb();

        headerBegin();

        b("Базы данных");

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
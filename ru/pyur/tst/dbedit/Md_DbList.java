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

        //table.addCol("Таблица");


        try {
            Statement stmt = m_conn.createStatement();

            String query = "SHOW DATABASES";

            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                String db_name = rs.getString(1);

                Tr tr = new Tr();
                table.addTr(tr);

                tr.addTd(new Td(db_name));
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
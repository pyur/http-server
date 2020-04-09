package ru.pyur.tst.dbedit;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
import ru.pyur.tst.tags.Table;
import ru.pyur.tst.tags.Td;
import ru.pyur.tst.tags.Tr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Md_TableList extends Module {



    public Md_TableList(Session session) {
        this.session = session;
        parseSession();
    }


    public void prepare() {
        connectToDb();

        headerBegin();

        b("Таблицы базы данных");

        Table table = new Table();

        try {
            Statement stmt = m_conn.createStatement();

            String db_name = getQuery("db");
            if (db_name == null)  throw new Exception("db absent");

            String sql1 = "USE `" + db_name + "`";
            //query('SET CHARACTER SET utf8');

            stmt.executeQuery(sql1);


            // ----

            String sql = "SHOW TABLES";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                String table_name = rs.getString(1);
                //String tmp = rs.getString(2);

                //System.out.println(id + "  " + name + "  " + cat + "  " + login + "  " + dtx + "  " + idx);
                Tr tr = new Tr();
                table.append(tr);

                tr.append(new Td(table_name));

                //tr.append(new Td(tmp));
            }

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        b(table.render());



        headerEnd();

        closeDb();
    }





}
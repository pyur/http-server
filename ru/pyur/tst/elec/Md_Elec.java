package ru.pyur.tst.elec;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
import ru.pyur.tst.tags.Div;
import ru.pyur.tst.tags.Table;
import ru.pyur.tst.tags.Td;
import ru.pyur.tst.tags.Tr;

import java.sql.*;


public class Md_Elec extends Module {


    public Md_Elec(Session session) {
        initHtml(session);
    }



    public void makeContent() {

        Div div = new Div();
        b(div);
        div.put("Электричество");
        div.width(200);
        div.height(50);
        div.border("1px solid #f99");


        Table table = new Table();
        b(table);


        try {
            Statement stmt = getDb();

            String sql = "SELECT `id`, `name`, `cat`, `login`, `dtx`, `idx` FROM `user`";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int cat = rs.getInt(3);
                String login = rs.getString(4);
                String dtx = rs.getString(5);
                int idx = rs.getInt(6);

                //System.out.println(id + "  " + name + "  " + cat + "  " + login + "  " + dtx + "  " + idx);
                Tr tr = new Tr();
                table.add(tr);

                tr.add(new Td(id));

                tr.add(new Td(name));

                tr.add(new Td(cat));

                tr.add(new Td(login));

                tr.add(new Td(dtx));

                tr.add(new Td(idx));
            }

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }


    }


}
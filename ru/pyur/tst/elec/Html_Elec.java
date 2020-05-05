package ru.pyur.tst.elec;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;
import ru.pyur.tst.tags.Div;
import ru.pyur.tst.tags.Table;
import ru.pyur.tst.tags.Td;
import ru.pyur.tst.tags.Tr;

import java.sql.*;


public class Html_Elec extends HtmlContent {

    @Override
    public void makeHtml() throws Exception {

        heading("Электричество");

        Table table = new Table();
        tag(table);


        String query = "SELECT `id`, `name`, `cat`, `login`, `dtx`, `idx` FROM `user`";

        ResultSet rs = query(query);

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


    }


}
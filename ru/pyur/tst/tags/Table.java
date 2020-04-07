package ru.pyur.tst.tags;

import java.util.ArrayList;

public class Table extends Tag {

    private ArrayList<Tr> trs = new ArrayList<>();


    public Table() {
        tag = "table";
        hasNested = true;
    }


    public void append(Tr tr) {
        trs.add(tr);
    }


    public String render_nested() {
        StringBuilder trs_str = new StringBuilder();

        for (Tr tr : trs) {
            trs_str.append(tr.render());
        }

        return trs_str.toString();
    }


}
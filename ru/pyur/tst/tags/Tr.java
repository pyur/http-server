package ru.pyur.tst.tags;

import java.util.ArrayList;


public class Tr extends Tag {

//    private ArrayList<Td> tds = new ArrayList<>();


    public Tr() {
        tag_name = "tr";
        closing = false;
        //hasNested = true;
    }


//    public void addTd(Td td) {
//        tds.add(td);
//    }


//    public String renderNested() {
//        StringBuilder tds_str = new StringBuilder();
//
//        for (Td td : tds) {
//            tds_str.append(td.render());
//        }
//
//        return tds_str.toString();
//    }

}
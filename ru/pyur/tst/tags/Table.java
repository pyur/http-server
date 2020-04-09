package ru.pyur.tst.tags;

import java.util.ArrayList;

public class Table extends Tag {

    private ArrayList<Tr> trs = new ArrayList<>();


    private boolean hasHeader = false;

    private ArrayList<TableColumn> columns = new ArrayList<>();

    public static final int TABLE_COLUMN_ALIGN_DEFAULT = 0;
    public static final int TABLE_COLUMN_ALIGN_LEFT = 1;
    public static final int TABLE_COLUMN_ALIGN_CENTER = 2;
    public static final int TABLE_COLUMN_ALIGN_RIGHT = 3;

    private class TableColumn {
        public String description;
        public int width;
        public int align;

        public TableColumn(String description, int width, int align) {
            this.description = description;
            this.width = width;
            this.align = align;
        }
    }



    private class ActionButton {
        public String icon;
        public String description;
        public String location;

        public ActionButton() {

        }
    }



    public Table() {
        tag = "table";
        hasNested = true;
    }


    public void addTr(Tr tr) {
        trs.add(tr);
    }




    public void addColumn(String description, int width) {
        if (!hasPre) {
            hasPre = true;
            classes.add("lst");
        }
        addColumn(description, width, TABLE_COLUMN_ALIGN_DEFAULT);
    }



    public void addColumn(String description, int width, int align) {
        if (description != null) { hasHeader = true; }
        columns.add(new TableColumn(description, width, align));
    }




    public String renderPre() {
        if (columns.size() == 0) return "";

        StringBuilder pre = new StringBuilder();

        pre.append("\r\n<style>\r\n");

        int i = 1;
        for (TableColumn tc : columns) {
            pre.append("table.lst td:nth-child(");
            //todo: exclude optional head row
            pre.append(i);
            pre.append(") {");

            pre.append("width: ");
            pre.append(tc.width);
            pre.append("px;");

            if (tc.align == TABLE_COLUMN_ALIGN_LEFT) { pre.append("text-align: left; padding: 0 0 0 2px;"); }
            else if (tc.align == TABLE_COLUMN_ALIGN_CENTER) { pre.append("text-align: center; padding: 0;"); }
            else if (tc.align == TABLE_COLUMN_ALIGN_RIGHT) { pre.append("text-align: right; padding: 0 2px 0 0;"); }
            pre.append("}\r\n");
            i++;
        }

        pre.append("</style>\r\n");

        return pre.toString();
    }



    public String renderNested() {
        StringBuilder trs_str = new StringBuilder();

        if (columns.size() != 0) {
            Tr head_tr = new Tr();
            for (TableColumn tc : columns) {
                head_tr.addTd(new Td(tc.description));
            }

            trs_str.append(head_tr.render());
        }


        for (Tr tr : trs) {
            trs_str.append(tr.render());
        }

        return trs_str.toString();
    }


}
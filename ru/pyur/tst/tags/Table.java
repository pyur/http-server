package ru.pyur.tst.tags;

import ru.pyur.tst.Coordinate;

import java.util.ArrayList;

public class Table extends Tag {

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


//x    private boolean hasActions = false;

    private ArrayList<ActionButton> actions = new ArrayList<>();

    private class ActionButton {
        public String icon;
        //public String description;
        //public String module;
        public String action;

        //public ActionButton(String icon) {
        //    this(icon, "");
        //}

        public ActionButton(String icon, String action) {
            this.icon = icon;
            //this.description = description;
            this.action = action;

        }
    }



    public Table() {
        tag_name = "table";
    }


    public Table(String id) {
        tag_name = "table";
        setId(id);
    }



    public void addColumn(String description, int width) {
        addColumn(description, width, TABLE_COLUMN_ALIGN_DEFAULT);
    }



    public void addColumn(String description, int width, int align) {
        if (description != null) { hasHeader = true; }

        if (columns.size() == 0) {
            addClass(id);
        }

        columns.add(new TableColumn(description, width, align));
    }



    public void addAction(String icon, String action) {
//x        if (!hasActions) {
//x            hasActions = true;
//x        }

        actions.add(new ActionButton(icon, action));
    }



    @Override
    public String renderBeforeTag() {

        // ---- append actions column ---- //

        if (actions.size() != 0) {
            String actions_desc = null;
            if (hasHeader) {
                switch(actions.size()) {
                    case 1:
                        actions_desc = "Д";
                        break;
                    case 2:
                        actions_desc = "Д.";
                        break;
                    case 3:
                        actions_desc = "Дейст";
                        break;
                    case 4:
                        actions_desc = "Действ";
                        break;
                    default:
                        actions_desc = "Действия";
                        break;
                }
            }
            addColumn(actions_desc, actions.size() * 18);
        }


        // ---- columns, actions style ---- //

        StringBuilder sb = new StringBuilder();

        if (columns.size() != 0) {
            sb.append("\r\n<style>\r\n");

            sb.append("table.");
            sb.append(id);
            sb.append(" tr:first-child td {font-weight: bold; background-color: #ddd; text-align: center; padding: 0;}\r\n");
            sb.append("table.");
            sb.append(id);
            sb.append(" tr:nth-child(odd) {background-color: #eee;}\r\n");

            int i = 1;
            for (TableColumn tc : columns) {
                sb.append("table.");
                sb.append(id);
                sb.append(" td:nth-child(");
                //todo: exclude optional head row. tr:nth-child(n+1)
                sb.append(i);
                sb.append(") {");

                sb.append("width: ");
                sb.append(tc.width);
                sb.append("px;");

                if (tc.align == TABLE_COLUMN_ALIGN_LEFT) {
                    sb.append("text-align: left; padding: 0 0 0 2px;");
                } else if (tc.align == TABLE_COLUMN_ALIGN_CENTER) {
                    sb.append("text-align: center; padding: 0;");
                } else if (tc.align == TABLE_COLUMN_ALIGN_RIGHT) {
                    sb.append("text-align: right; padding: 0 2px 0 0;");
                }

                sb.append("}\r\n");
                i++;
            }


            // ---- actions style ---- //

            if (actions.size() != 0) {
                //table.lst td:nth-child(7) > a:nth-child(1) {background-position: -288px -64px;}

                sb.append("table.");
                sb.append(id);
                sb.append(" td:nth-child(");
                sb.append(columns.size());
                sb.append(") > hr {");
                sb.append("cursor: pointer;");
                sb.append("}\r\n");


                int j = 1;
                for (ActionButton but : actions) {
                    sb.append("table.");
                    sb.append(id);
                    sb.append(" td:nth-child(");
                    sb.append(columns.size());
                    //sb.append(") > a:nth-child(");
                    sb.append(") > hr:nth-child(");
                    sb.append(j);
                    sb.append(")");

                    Coordinate icon_coord = getActionCoord(but.icon);

                    sb.append(" {background-position: ");

                    if (icon_coord.x == 0) {
                        sb.append(0);
                    } else {
                        sb.append("-");
                        sb.append(icon_coord.x);
                        sb.append("px ");
                    }

                    if (icon_coord.y == 0) {
                        sb.append(0);
                    } else {
                        sb.append("-");
                        sb.append(icon_coord.y);
                        sb.append("px ");
                    }

                    sb.append(";");

                    sb.append("}\r\n");
                    j++;
                }
            }

            sb.append("</style>\r\n");
        }


        return sb.toString();
    }




    @Override
    public String renderAfterTag() {
        StringBuilder sb = new StringBuilder();


        // ---- actions script ---- //

        if (actions.size() != 0) {
            sb.append("\r\n<script>\r\n");
            //sb.append("var callback_function\r\n");
            sb.append("TableActions('");
            sb.append(id);
            sb.append("', [");

            // -- callback functions -- //
            boolean first = true;
            for (ActionButton but : actions) {
                if (!first)  sb.append(", ");
                sb.append("function (row_id) { alert(\"func " + but.icon + " : \" + row_id);}");
                if (first)  first = false;
            }

            sb.append("]);\r\n");
            sb.append("</script>\r\n");
        }

        return sb.toString();
    }




    @Override
    public String renderNestedPre() {
        if (!hasHeader)  return "";

        StringBuilder sb = new StringBuilder();

        if (columns.size() != 0) {
            Tr head_tr = new Tr();
            for (TableColumn tc : columns) {
                //System.out.println(tc.description);
                head_tr.add(new Td(tc.description));
            }

            sb.append(head_tr.toString());
        }

        return sb.toString();
    }



    @Override
    public String renderPostTag() {
        StringBuilder sb = new StringBuilder();

        if (actions.size() != 0) {
            Td actions_td = new Td();
            for (ActionButton ta : actions) {
                Hr act_button = new Hr();
                actions_td.add(act_button);
                //act.addClass("i"+);
                //act.addClass("s");
                //act.setHref(ta.url);
            }

            sb.append(actions_td.toString());
        }

        return sb.toString();
    }


}
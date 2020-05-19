package ru.pyur.tst;

import ru.pyur.tst.db.DbFetch;
import ru.pyur.tst.db.DbFetcher;
import ru.pyur.tst.db.Var;
import ru.pyur.tst.tags.Div;
import ru.pyur.tst.tags.Form;
import ru.pyur.tst.tags.InputText;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;


public abstract class HtmlEditContent extends HtmlContent {

    protected Connection edit_conn;

    protected String edit_table;
    protected String id_param;
//    protected String edit_id;
    protected int edit_id;

    protected String name_a;
    protected String name_b;

    protected ArrayList<EditColumn> edit_columns;

    private boolean mode_edit;  // = false;


    protected static final int EDIT_COLUMN_TYPE_INT = 1;
    protected static final int EDIT_COLUMN_TYPE_STRING = 2;
    protected static final int EDIT_COLUMN_TYPE_DATE = 3;
    protected static final int EDIT_COLUMN_TYPE_TIME = 4;
    protected static final int EDIT_COLUMN_TYPE_DATETIME = 5;



//x    protected void editDb(Connection conn) { edit_conn = conn; }

//x    protected void editTable(String table_name) { edit_table = table_name; }

//x    protected void editIdParam(String id_param) { this.id_param = id_param; }

//x    protected void editString(String name_a, String name_b) {
//x        this.name_a = name_a;
//x        this.name_b = name_b;
//x    }



    protected void addEditColumn(EditColumn ec) {
        edit_columns.add(ec);
    }


    protected void addEditColumn(String column, int default_value) {
        edit_columns.add(new EditColumn(column, default_value));
    }


    protected void addEditColumn(String column, String default_value) {
        edit_columns.add(new EditColumn(column, default_value));
    }


    protected void addEditColumn(String column, int type, String default_value) {
        edit_columns.add(new EditColumn(column, type, default_value));
    }




    abstract protected void initEdit() throws Exception;

//    abstract protected void editColumn();



    @Override
    public void makeHtml() throws Exception {
        edit_columns = new ArrayList<>();

        initEdit();


        String edit_id_a = getOptionalParam(id_param);

        mode_edit = (edit_id_a != null);


        // ---- initialize values ---- //

        if (mode_edit) {
            edit_id = Integer.parseInt(edit_id_a);
            EditColumnsFetcher db = new EditColumnsFetcher();
            boolean fr = db.fetchResults();
            if (!fr)  throw new Exception("row with id " + edit_id + " not exist.");
        }


        // ---- actions bar ---- //

        //addActionLink("Удалить", new ModuleUrl(), "holly");


        // ---- caption ---- //

        if (!mode_edit) {
            heading("Добавление " + name_b);
        } else {
            heading("Редактирование " + name_b);
        }


        // ---- form ---- //

        Form form = new Form("edit");
        form.addClass("edit");
        form.setUrl("/a/" + getModule() + "/edit/");
        add(form);

        for (EditColumn ec : edit_columns) {
            Div row_container = new Div();
            add(row_container);

            Div desc = new Div();
            row_container.add(desc);
            desc.add(ec.column);

            Div input_container = new Div();
            row_container.add(input_container);

            switch (ec.type) {
                case EDIT_COLUMN_TYPE_INT: {
                    InputText it = new InputText();
                    it.setName(ec.column);
                    it.setValue(ec.value.getString());  // "" + getInt()
                    input_container.add(it);
                    }
                    break;

                case EDIT_COLUMN_TYPE_STRING: {
                    InputText it = new InputText();
                    it.setName(ec.column);
                    it.setValue(ec.value.getString());
                    input_container.add(it);
                    }
                    break;

                case EDIT_COLUMN_TYPE_DATE:
                    //input_container.add();
                    break;
            }
        }


    }




    //

    private class EditColumnsFetcher extends DbFetch {

        public EditColumnsFetcher() {
            setConnection(edit_conn);

            table(edit_table);

            for (EditColumn ec : edit_columns) {
                System.out.println("add column: " + ec.column);
                col(ec.column);
            }

            where("`id` = ?");
            System.out.println("id_value: " + edit_id);
            wa(edit_id);
        }



        public boolean fetchResults() throws Exception {
            ResultSet rs = getResultSet();


            boolean is_empty = rs.isAfterLast();

//            if (is_empty) {
//?                onEmpty();
//                return false;
//            }


//x            onFetched();


//            ResultSetMetaData rsmd = rs.getMetaData();
            //String name = rsmd.getColumnName(1);
//            int column_count = rsmd.getColumnCount();

            if (rs.next()) {
//x                if (id_column_idx != -1) {
//x                    row_id = rs.getString(id_column_idx);
//x                    //onRowId(row_id);
//x                }
//x                onRow(row_id);

//x                for (int i = 1; i <= column_count; i++) {
//x                    onColumn(rs, i);
//x                }

                int i = 1;
                for (EditColumn ec : edit_columns) {
                    String val = rs.getString(i);
                    ec.setValue(val);
                    i++;
                }

            }

            else {
                return false;
            }

//x            onDone();

            rs.close();

            return true;
        }

    }




    // ---- nested class ----------------------------------------------------------------

    protected class EditColumn {

        public String column;
        public int type;
        //private int default_value_int;
        //private String default_value_string;
        public Var value;


        public EditColumn() {}


        public EditColumn(String column, int default_value) {
            this.column = column;
            this.type = EDIT_COLUMN_TYPE_INT;
            this.value = new Var(default_value);
        }


        public EditColumn(String column, String default_value) {
            this.column = column;
            this.type = EDIT_COLUMN_TYPE_STRING;
            this.value = new Var(default_value);
        }


        public EditColumn(String column, int type, String default_value) {
            this.column = column;
            this.type = type;
            this.value = new Var(default_value);
        }


        public void setValue(String value) {
            //?? conevert type
            this.value = new Var(value);
        }

    }


}
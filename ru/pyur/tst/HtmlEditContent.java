package ru.pyur.tst;

import ru.pyur.tst.db.DbFetch;
import ru.pyur.tst.tags.*;
import ru.pyur.tst.util.DateTime;
import ru.pyur.tst.util.SqlDate;
import ru.pyur.tst.util.SqlTime;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import static ru.pyur.tst.EditColumn.*;


public abstract class HtmlEditContent extends HtmlContent {

    protected Connection edit_conn;

    protected String edit_table;
    protected String id_param;
//    protected String edit_id;
    protected int edit_id;

    protected String name_a;
    protected String name_b;

    protected ArrayList<EditColumn> edit_columns;

    private boolean mode_edit;






//    protected void addEditColumn(EditColumn ec) {
//        edit_columns.add(ec);
//    }


    protected void addEditColumn(String column, int number, String desc) {
        EditColumn ec = new EditColumn(column, desc);
        ec.setNumber(number);
        edit_columns.add(ec);
    }


    protected void addEditColumn(String column, String text, String desc) {
        EditColumn ec = new EditColumn(column, desc);
        ec.setText(text);
        edit_columns.add(ec);
    }


    //protected void addEditColumn(String column, String default_value, boolean large) {
    //    edit_columns.add(new EditColumn(column, default_value, large));
    //}


    protected void addEditColumn(String column, SqlDate date, String desc) {
        EditColumn ec = new EditColumn(column, desc);
        ec.setDate(date);
        edit_columns.add(ec);
    }


    protected void addEditColumn(String column, SqlTime time, String desc) {
        EditColumn ec = new EditColumn(column, desc);
        ec.setTime(time);
        edit_columns.add(ec);
    }


    protected void addEditColumn(String column, DateTime dt, String desc) {
        EditColumn ec = new EditColumn(column, desc);
        ec.setDateTime(dt);
        edit_columns.add(ec);
    }




    abstract protected void initEdit() throws Exception;



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
        form.setUrl("/a/" + getModule() + "/update/");
        form.setOnSubmit("return FormSend(this," + (mode_edit?"\'update\'":"\'insert\'") + ");");
        add(form);

        if (mode_edit) {
            InputHidden ihid = new InputHidden();
            form.add(ihid);
            ihid.setName("id");
            ihid.setValue("" + edit_id);
        }

        for (EditColumn ec : edit_columns) {
            Div row_container = new Div();
            form.add(row_container);

            Div desc = new Div();
            row_container.add(desc);
            desc.add(ec.desc);

            Div input_container = new Div();
            row_container.add(input_container);

            switch (ec.type) {
                case EDIT_COLUMN_TYPE_NUMERIC: {
                    InputNumber inum = new InputNumber();
                    inum.setName(ec.column);
                    inum.setValue(ec.toString());
                    input_container.add(inum);
                    }
                    break;

                case EDIT_COLUMN_TYPE_TEXT: {
                    InputText itxt = new InputText();
                    itxt.setName(ec.column);
                    itxt.setValue(ec.toString());
                    input_container.add(itxt);
                    }
                    break;

                case EDIT_COLUMN_TYPE_DATE: {
                    InputDate idate = new InputDate();
                    idate.setName(ec.column);
                    idate.setValue(ec.toString());
                    input_container.add(idate);
                    }
                    break;

                case EDIT_COLUMN_TYPE_TIME: {
                    InputTime itime = new InputTime();
                    itime.setName(ec.column);
                    itime.setValue(ec.toString());
                    input_container.add(itime);
                }
                break;

                case EDIT_COLUMN_TYPE_DATETIME: {
                    InputDateTime idt = new InputDateTime();
                    idt.setName(ec.column);
                    idt.setValue(ec.toString());
                    input_container.add(idt);
                }
                break;
            }
        }


        SubmitButton but_submit = new SubmitButton();
        but_submit.setName("submit");
        but_submit.setValue("Сохранить");
        //but_submit.addStyle("display", "block");
        //but_submit.addStyle("margin", "20px auto 0");
        form.add(but_submit);

    }




    // ---- db fetcher ----------------------------------------------------------------

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

            if (!rs.next())  return false;

            int i = 1;
            for (EditColumn ec : edit_columns) {
                //String val = rs.getString(i);
                ec.setFromResultSet(rs, i);
                i++;
            }

            rs.close();

            return true;
        }

    }


}
package ru.pyur.tst;

import ru.pyur.tst.db.DbDelete;
import ru.pyur.tst.db.DbInsert;
import ru.pyur.tst.db.DbUpdate;
import ru.pyur.tst.util.Json;

import java.sql.Connection;
import java.util.ArrayList;


public abstract class ApiUpdateContent extends ApiContent {

    protected Connection update_conn;

    protected String update_table;

    protected String id_field;
//    protected int update_id;

    protected ArrayList<EditColumn> insert_columns;
    protected ArrayList<EditColumn> update_columns;

//    private boolean has_id;
//    private boolean no_values = false;



    protected void addUpdateColumnNum(String column) {
        EditColumn ec = new EditColumn(column, null);
//        ec.setNumber(number);
        update_columns.add(ec);
    }


    protected void addUpdateColumnText(String column) {
        EditColumn ec = new EditColumn(column, null);
//        ec.setText(text);
        update_columns.add(ec);
    }




    protected abstract void initInsert() throws Exception;

    protected abstract void initUpdate() throws Exception;

    protected abstract void initDelete() throws Exception;


    protected boolean grantInsertPermission() { return true; }

    protected boolean grantUpdatePermission() { return true; }

    protected boolean grantDeletePermission() { return true; }



    @Override
    public void makeJson() throws Exception {
        //todo: don't forget about permissions

        insert_columns = new ArrayList<>();
        update_columns = new ArrayList<>();


        if (has("insert")) {
            if (grantInsertPermission()) {
                doInsert();
            }
        }

        else if (has("update")) {
            if (grantUpdatePermission()) {
                doUpdate();
            }
        }

        else if (has("delete")) {
            if (grantDeletePermission()) {
                doDelete();
            }
        }




//        has_id = has(update_field);
//
//        if (has_id) {
//            update_id = getInt(update_field);
//        }
//
//
//        // ---- insert ---- //
//        if (!has_id) {
//            System.out.println("inserting new row");
//        }
//
//        // ---- update ---- //
//        else {
//            System.out.println("updating row: " + update_id);
//        }



        // ---- delete ---- //
//        if (has("delete")) {
//        }
//
//        //return relocate url
//        put("result", "wip");
    }



    private void doInsert() throws Exception {
        initInsert();

        Json json_insert = getObject("insert");

        DbInsert ins = new DbInsert(update_conn);

        for (EditColumn ec : update_columns) {
            String got = json_insert.getString(ec.column);
            ins.set(ec.column, got);
        }

        int ins_id = ins.insert();

        put("result", "inserted");
        put("id", ins_id);
    }




    private void doUpdate() throws Exception {
        initUpdate();

        Json json_update = getObject("update");

        DbUpdate upd = new DbUpdate(update_conn);

        for (EditColumn ec : update_columns) {
            String got = json_update.getString(ec.column);
            upd.set(ec.column, got);
        }

        upd.where("`id` = ?");
        upd.wa(json_update.getString(id_field));

        int upd_count = upd.update();

        put("result", "updated");
        put("count", upd_count);
    }




    private void doDelete() throws Exception {
        initDelete();

        Json json_delete = getObject("delete");

        DbDelete del = new DbDelete(update_conn);

        del.where("`id` = ?");
        del.wa(json_delete.getString(id_field));

        int del_count = del.delete();

        put("result", "deleted");
        put("count", del_count);
    }


    }
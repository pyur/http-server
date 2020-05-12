package ru.pyur.tst.dbedit.dbedit;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.ModuleInfo;

import java.sql.Connection;
import java.sql.DriverManager;


public class Info extends ModuleInfo {

    private static final String NAME = "db";
//    public String getModuleName() { return NAME; }


                                                   // default   // host_list
    public static final String DBEDIT_ACTION_DB_LIST = "host";  // db_list
    //public static final String DBEDIT_ACTION_DB_VIEW = "db";
    public static final String DBEDIT_ACTION_TABLE_LIST = "db";  // table_list
    public static final String DBEDIT_ACTION_TABLE_VIEW = "table";  // comlumn_list
    public static final String DBEDIT_ACTION_TABLE_EDIT = "table_edit";

    public static final String DBEDIT_PARAM_HOST = "host";
    public static final String DBEDIT_PARAM_DB = "db";
    public static final String DBEDIT_PARAM_TABLE = "table";


    private static final String DBEDIT_URL = "jdbc:sqlite:dbedit.db";



    //@Override
    protected Connection getModuleDb() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DBEDIT_URL);
        } catch (Exception e) { e.printStackTrace(); }
        return conn;
    }



    @Override
    public HtmlContent getHtml(String action) {
        HtmlContent html_content = null;

        if (action.isEmpty()) {
            html_content = new Md_HostList();
        }

        else if (action.equals(DBEDIT_ACTION_DB_LIST)) {
            html_content = new Md_DbList();
        }

        //else if (action.equals(DBEDIT_ACTION_DB_VIEW)) {
        //    md = new Md_DbView();
        //}

        else if (action.equals(DBEDIT_ACTION_TABLE_LIST)) {
            html_content = new Md_TableList();
        }

        else if (action.equals(DBEDIT_ACTION_TABLE_VIEW)) {
            html_content = new Md_TableView();
        }


        if (html_content != null) {
            html_content.setModuleDb(getModuleDb());
        }

        return html_content;
    }

}
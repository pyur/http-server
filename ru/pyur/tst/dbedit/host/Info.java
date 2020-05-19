package ru.pyur.tst.dbedit.host;

import ru.pyur.tst.ApiContent;
import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.ModuleInfo;


public class Info extends ModuleInfo {

    private static final String NAME = "db";
//    public String getModuleName() { return NAME; }


                                                   // default   // host_list
    public static final String DBEDIT_ACTION_HOST_LIST = "host";  // db_list
    public static final String DBEDIT_ACTION_HOST_EDIT = "host_edit";
    //public static final String DBEDIT_ACTION_DB_VIEW = "db";
    public static final String DBEDIT_ACTION_TABLE_LIST = "db";  // table_list
    public static final String DBEDIT_ACTION_DB_EDIT = "db_edit";
    public static final String DBEDIT_ACTION_TABLE_VIEW = "table";  // comlumn_list
    public static final String DBEDIT_ACTION_TABLE_EDIT = "table_edit";

    public static final String DBEDIT_PARAM_HOST = "host";
    public static final String DBEDIT_PARAM_DB = "db";
    public static final String DBEDIT_PARAM_TABLE = "table";


//    private static final String DBEDIT_URL = "jdbc:sqlite:dbedit.db";



    //@Override
//    protected Connection getModuleDb() {
//        Connection conn = null;
//        try {
//            conn = DriverManager.getConnection(DBEDIT_URL);
//        } catch (Exception e) { e.printStackTrace(); }
//        return conn;
//    }



    @Override
    public HtmlContent getHtml(String action) {
        HtmlContent html_content = null;

        if (action.isEmpty()) {
            html_content = new Html_HostList();
        }

        else if (action.equals("edit")) {
            html_content = new HtmlEdit_Host();
        }

        //else if (action.equals(DBEDIT_ACTION_DB_VIEW)) {
        //    md = new Md_DbView();
        //}

//        else if (action.equals(DBEDIT_ACTION_TABLE_LIST)) {
//            html_content = new Md_TableList();
//        }
//
//        else if (action.equals(DBEDIT_ACTION_TABLE_VIEW)) {
//            html_content = new Md_TableView();
//        }


        if (html_content != null) {
//            html_content.setModuleDb(getModuleDb());
        }

        return html_content;
    }




    @Override
    public ApiContent getApi(String action) {
        ApiContent api_content = null;

//        if (action.isEmpty()) {
//            api_content = new ApiEdit_Host();
//        }

        if (action.equals("update")) {
            api_content = new ApiUpdate_Host();
        }

        return api_content;
    }

}
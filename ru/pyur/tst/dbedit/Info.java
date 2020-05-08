package ru.pyur.tst.dbedit;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.ModuleInfo;


public class Info extends ModuleInfo {

    private static final String NAME = "db";

    public static final String DBEDIT_ACTION_DB = "db";
    public static final String DBEDIT_ACTION_TABLE = "table";
    public static final String DBEDIT_ACTION_TABLE_EDIT = "table_edit";

    public static final String DBEDIT_PARAM_DB = "db";
    public static final String DBEDIT_PARAM_TABLE = "table";


//    public String ModuleName() { return NAME; }


    @Override
    public HtmlContent getHtml(String action) {
        HtmlContent md = null;

        if (action.isEmpty()) {
            md = new Md_DbList();
        }

        else if (action.equals(DBEDIT_ACTION_DB)) {
            md = new Md_TableList();
        }

        else if (action.equals(DBEDIT_ACTION_TABLE)) {
            md = new Md_TableView();
        }


        return md;
    }

}
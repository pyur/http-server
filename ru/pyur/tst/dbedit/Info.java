package ru.pyur.tst.dbedit;

import ru.pyur.tst.Module;
import ru.pyur.tst.ModuleInfo;
import ru.pyur.tst.Session;


public class Info extends ModuleInfo {

    private static final String NAME = "db";
    //private static final int ICON = R.mipmap.elec;
    //private static final Fragment UI = "Fm_Elec";

    public static final String DBEDIT_ACTION_DB = "db";
    public static final String DBEDIT_ACTION_TABLE = "table";
    public static final String DBEDIT_ACTION_TABLE_EDIT = "table_edit";

    public static final String DBEDIT_PARAM_DB = "db";
    public static final String DBEDIT_PARAM_TABLE = "table";


    public Info(Session session) {
        //System.out.println("Info(Session)");
        setSession(session);
    }


    public String ModuleName() { return NAME; }
    //public int ModuleIcon() { return ICON; }


    public Module dispatch() {
        Module md = null;

        if (action.isEmpty()) {
            md = new Md_DbList(session);
        }

        else if (action.equals(DBEDIT_ACTION_DB)) {
            md = new Md_TableList(session);
        }

        else if (action.equals(DBEDIT_ACTION_TABLE)) {
            md = new Md_TableView(session);
        }


        return md;
    }

}
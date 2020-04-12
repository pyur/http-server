package ru.pyur.tst.resources;

import ru.pyur.tst.Module;
import ru.pyur.tst.ModuleInfo;
import ru.pyur.tst.Session;


public class Info extends ModuleInfo {

    private static final String NAME = "res";

    public static final String RESOURCES_ACTION_GEN = "gen";

    public static final String RESOURCES_PARAM_t = "t";


    public Info(Session session) {
        //System.out.println("Info(Session)");
        this.session = session;
        parseSession();
    }


    public String ModuleName() { return NAME; }


    public Module dispatch() {
        Module md = null;

        if (action.isEmpty()) {
            md = new Md_ResList(session);
        }

//        else if (action.equals(DBEDIT_ACTION_DB)) {
//            md = new Md_TableList(session);
//        }

//        else if (action.equals(DBEDIT_ACTION_TABLE)) {
//            md = new Md_TableView(session);
//        }


        return md;
    }


}
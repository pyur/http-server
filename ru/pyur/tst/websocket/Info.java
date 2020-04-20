package ru.pyur.tst.websocket;

import ru.pyur.tst.Module;
import ru.pyur.tst.ModuleInfo;
import ru.pyur.tst.Session;
import ru.pyur.tst.resources.*;


public class Info extends ModuleInfo {

    private static final String NAME = "ws";

    //public static final String WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION = "gsa";
    //public static final String WEBSOCKET_ACTION_GET_SPRITE_ACTION = "sa";

    //public static final String RESOURCES_PARAM_t = "t";


    public Info(Session session) {
        //System.out.println("Info(Session)");
        setSession(session);
    }


    public String ModuleName() { return NAME; }


    public Module dispatch() {
        Module md = null;

        if (action.isEmpty()) {
            md = new Md_Chat(session);
        }

        //else if (action.equals(WEBSOCKET_ACTION_GENERATE_SPRITE_ACTION)) {
        //    md = new Md_MakeSpriteActions(session);
        //}




        return md;
    }


}
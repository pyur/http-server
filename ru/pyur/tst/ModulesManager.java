package ru.pyur.tst;

public class ModulesManager {

    public static ModuleInfo getModuleInfo(String module) {
        ModuleInfo module_info = null;

        if (module.isEmpty()) {
            //todo: default page
            module_info = new ru.pyur.tst.default_module.Info();
        }

        else if (module.equals("auth")) {
            module_info = new ru.pyur.tst.auth.Info();
        }

        else if (module.equals("elec")) {
            //mi = new ru.pyur.tst.elec.Info();
            //html_content = new ru.pyur.tst.elec.Md_Elec(session);  // rewrite
        }

        else if (module.equals("water")) {
            //mi = new ru.pyur.tst.water.Info();
            //html_content = new ru.pyur.tst.water.Md_Water(session);  // rewrite
        }

        else if (module.equals("db")) {
            module_info = new ru.pyur.tst.dbedit.Info();
        }

        else if (module.equals("res")) {
            module_info = new ru.pyur.tst.resources.Info();
        }

        else if (module.equals("ws")) {
            module_info = new ru.pyur.tst.websocket.Info();
        }

        else if (module.equals("battleship")) {
            module_info = new ru.pyur.tst.battleship.Info();
        }

        //else if (module.equals("ext")) {
        //    module_info = new ru.pyur.tst.extsample.ExtMod();
        //}

        return module_info;
    }




/*
    if (module.isEmpty()) {
        module_info = new ru.pyur.tst.websocket.Info(this);
        //ws_mod_cb = info.setGetWebsocketCallback();
        WebsocketDispatcher wsm = module_info.getWs();
        // todo: info.getWs(is, os).getHtml();
        //info.setWebsocketSession(this);
        wsm.setStreams(is, os);
        wsm.dispatch();
    }

//        if (module.equals("ws")) {
//        }

    else if (module.equals("battleship")) {
        module_info = new ru.pyur.tst.battleship.Info(this);
        //ws_mod_cb = info.setGetWebsocketCallback();
        WebsocketDispatcher wsm = module_info.getWs();
        // todo: info.getWs(is, os).getHtml();
        //info.setWebsocketSession(this);
        wsm.setStreams(is, os);
        wsm.dispatch();
    }
*/

}
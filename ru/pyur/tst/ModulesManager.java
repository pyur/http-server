package ru.pyur.tst;

public class ModulesManager {

    public static ModuleInfo getModuleInfo(String module_name) {
        ModuleInfo module_info = null;

        if (module_name.isEmpty()) {
            //todo: default page
            module_info = new ru.pyur.tst.default_module.Info();
        }

        else if (module_name.equals("auth")) {
            module_info = new ru.pyur.tst.auth.Info();
        }

        else if (module_name.equals("elec")) {
            module_info = new ru.pyur.tst.elec.Info();
        }

        else if (module_name.equals("water")) {
            //module_info = new ru.pyur.tst.water.Info();
        }

        else if (module_name.equals("db")) {
            module_info = new ru.pyur.tst.dbedit.Info();
        }

        else if (module_name.equals("res")) {
            module_info = new ru.pyur.tst.resources.Info();
        }

        else if (module_name.equals("ws")) {
            module_info = new ru.pyur.tst.websocket.Info();
        }

        else if (module_name.equals("battleship")) {
            module_info = new ru.pyur.tst.battleship.Info();
        }

        // todo: 'setup' for create 'config.db' and create default admin

        //else if (module.equals("ext")) {
        //    module_info = new ru.pyur.tst.extsample.ExtMod();
        //}

        return module_info;
    }

}
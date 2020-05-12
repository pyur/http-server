package ru.pyur.tst.sample_host;


// can be placed in separate package, for easy update

import ru.pyur.tst.ModuleInfo;

public class ModulesManager {

    public static ModuleInfo getModuleInfo(String module_name) {
        ModuleInfo module_info = null;

        if (module_name.isEmpty()) {
            module_info = new ru.pyur.tst.sample_host.default_module.Info();
        }

        else if (module_name.equals("auth")) {
            module_info = new ru.pyur.tst.sample_host.auth.Info();
        }

        else if (module_name.equals("elec")) {
            module_info = new ru.pyur.tst.sample_host.elec.Info();
        }

        else if (module_name.equals("water")) {
            module_info = new ru.pyur.tst.sample_host.water.Info();
        }

        else if (module_name.equals("db")) {
            module_info = new ru.pyur.tst.dbedit.dbedit.Info();
        }

        else if (module_name.equals("res")) {
            module_info = new ru.pyur.tst.sample_host.resources.Info();
        }

        else if (module_name.equals("ws")) {
            module_info = new ru.pyur.tst.sample_host.websocket.Info();
        }

        else if (module_name.equals("battleship")) {
            module_info = new ru.pyur.tst.sample_host.battleship.Info();
        }

        // todo: 'setup' for create 'config.db' and create default admin

        //else if (module.equals("ext")) {
        //    module_info = new ru.pyur.tst.extsample.ExtMod();
        //}

        return module_info;
    }

}
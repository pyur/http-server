package ru.pyur.tst.dbedit;

import ru.pyur.tst.*;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;


public class DbAdminHost extends ModularHost {

    private final String DOC_ROOT = "dbedit/files";

    // -------- Main DB -------- //

    //private static final String DB_URL = "jdbc:mariadb://127.0.0.1/";
//    private static final String DB_URL = "jdbc:mariadb://127.0.0.1/";
//    private static final String DB_USER = "root";
//    private static final String DB_PASSWORD = "1";



    // -------- Config DB -------- //

    private static final String DB_CONFIG_URL = "jdbc:sqlite:dbedit/config.db";



    @Override
    protected String getDocRoot() { return DOC_ROOT; }



//    @Override
//    protected Connection getHostDb() {
//        Connection conn = null;
//
//        try {
//            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//            //todo: use modern 'DataSource' class
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return conn;
//    }


    @Override
    protected Connection getHostConfig() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_CONFIG_URL);
            //todo: use modern 'DataSource' class
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }




    @Override
    protected ModuleInfo getModuleInfo(String module_name) {
        ModuleInfo module_info = null;

        if (module_name.isEmpty()) {
            module_info = new ru.pyur.tst.dbedit.default_module.Info();
        }

        else if (module_name.equals("auth")) {
            module_info = new ru.pyur.tst.dbedit.auth.Info();
        }

//        else if (module_name.equals("elec")) {
//            module_info = new ru.pyur.tst.sample_host.elec.Info();
//        }
//
//        else if (module_name.equals("water")) {
//            module_info = new ru.pyur.tst.sample_host.water.Info();
//        }

        else if (module_name.equals("db")) {
            module_info = new ru.pyur.tst.dbedit.dbedit.Info();
        }

//        else if (module_name.equals("res")) {
//            module_info = new ru.pyur.tst.dbedit.resources.Info();
//        }

        // todo: 'setup' for create 'config.db' and create default admin

        //else if (module.equals("ext")) {
        //    module_info = new ru.pyur.tst.extsample.ExtMod();
        //}

        return module_info;
    }



}
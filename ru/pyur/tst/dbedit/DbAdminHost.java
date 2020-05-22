package ru.pyur.tst.dbedit;

import ru.pyur.tst.*;

import java.sql.Connection;
import java.sql.DriverManager;


public class DbAdminHost extends ModularHost {

    private final String HOST_DIR = "dbedit";

    private final String DOC_ROOT = "dbedit/files";

    // -------- Main DB -------- //

    //private static final String DB_URL = "jdbc:mariadb://127.0.0.1/";
//    private static final String DB_URL = "jdbc:mariadb://127.0.0.1/";
//    private static final String DB_USER = "root";
//    private static final String DB_PASSWORD = "1";



    // -------- Config DB -------- //

    private static final String DB_CONFIG_URL = "jdbc:sqlite:dbedit/config.db";

    private static final String DB_SESS_URL = "jdbc:sqlite:dbedit/config.db";

    private static final String DB_USER_URL = "jdbc:sqlite:dbedit/config.db";



    @Override
    protected String getHostDir() { return HOST_DIR; }

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
    protected Connection connectHostConfig() {  // todo: throws Exception
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
    protected Connection connectSessDb() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_SESS_URL);
        } catch (Exception e) { e.printStackTrace(); }

        return conn;
    }


    @Override
    protected Connection connectUserDb() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(DB_USER_URL);
        } catch (Exception e) { e.printStackTrace(); }

        return conn;
    }


//    @Override
//    protected Connection getAdmUserDb() { return null; }




    @Override
    protected ModuleInfo getModuleInfo(String module_name) {
        ModuleInfo module_info = null;

        if (module_name.isEmpty()) {
            module_info = new ru.pyur.tst.dbedit.default_module.Info();
        }

        else if (module_name.equals("auth")) {
            module_info = new ru.pyur.tst.dbedit.auth.Info();
        }

        else if (module_name.equals("res")) {
            module_info = new ru.pyur.tst.dbedit.resources.Info();
        }

        // ----------------------------------------------------------------

        else if (module_name.equals("host")) {
            module_info = new ru.pyur.tst.dbedit.host.Info();
        }

        else if (module_name.equals("db")) {
            module_info = new ru.pyur.tst.dbedit.db.Info();
        }

        else if (module_name.equals("table")) {
            module_info = new ru.pyur.tst.dbedit.table.Info();
        }

        else if (module_name.equals("col")) {
//            module_info = new ru.pyur.tst.dbedit.col.Info();
        }

        // todo: 'setup' for create 'config.db' and create default admin

        //else if (module.equals("ext")) {
        //    module_info = new ru.pyur.tst.extsample.ExtMod();
        //}

        return module_info;
    }



}
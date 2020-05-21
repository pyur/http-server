package ru.pyur.tst;

import ru.pyur.tst.util.PStr;

import java.sql.*;
import java.util.ArrayList;


public abstract class ContentBase {

    protected ModularHost host_session;

    private String host_dir;

//    private DbManager db_manager;
//    private Connection db_connection;  // host-wide main database (data)
//    private Connection db_config_1;      // server-wide config db (list of hosts)
//    private Connection db_config;  // host-wide config db (lists of icons)

//    private Connection module_db;



    // ---- constructor ----------------

    protected void initCommon(ModularHost host) {
        host_session = host;

        host_dir = host.getHostDir();

//        try {
//            db_connection = host_session.getDb();
//            db_config = host_session.getConfigDb();
//        } catch (Exception e) { e.printStackTrace(); }
    }



    // ---- prototypes ------------------------------------------------

    public abstract byte[] makeContent();




    // ---- setters, getters ------------------------------------------------

    protected Permissions getPermissions() { return host_session.getPermissions(); }

    protected boolean perm(String permission_name) { return host_session.hasPermission(permission_name); }

    protected boolean perm(String module_name, String permission_name) { return host_session.hasPermission(module_name, permission_name); }

    protected String getHostDir() { return host_dir; }

//    protected DbManager getDbManager() { return session.getDbManager(); }  // db_manager
    protected Connection getHostDb() { return host_session.getDb(); }

    protected Connection getConfigDb() { return host_session.getConfigDb(); }

    protected Connection getSessDb() { return host_session.getSessDb(); }

    protected Connection getUserDb() { return host_session.getUserDb(); }

    protected Connection getAdmUserDb() { return host_session.getAdmUserDb(); }


//    protected Connection getModuleDb() { return module_db; }
//    public void setModuleDb(Connection module_db) { this.module_db = module_db; }


    protected String getModule() { return host_session.getModule(); }

    protected String getAction() { return host_session.getAction(); }



    // -------- get parameter --------------------------------

    // todo getFilteredQuery for numbers, only_alphabet, etc. for screening malicious data
    protected String getOptionalParam(String key) {
        ArrayList<PStr> lsQuery = host_session.getQuery();

        for (PStr pair : lsQuery) {
            if (pair.key.equals(key))  return pair.value;
        }

        return null;
    }



    protected String getParam(String key) throws Exception {
        ArrayList<PStr> lsQuery = host_session.getQuery();

        for (PStr pair : lsQuery) {
            if (pair.key.equals(key))  return pair.value;
        }

        throw new Exception("parameter \'" + key + "\' absent.");
    }



//    protected int getParamInt(String key) throws Exception {
//        String param = getParam(key);
//        return Integer.parseInt(param);
//    }




    protected void setCode(int code) {
        host_session.setCode(code);
    }

    protected void setContentType(String value) { host_session.addOption("Content-Type", value); }

    protected void setCookie(String name, String value, int expires, String path) { host_session.setCookie(name, value, expires, path); }

    protected String getCookie(String name) { return host_session.getCookie(name); }


    // ---- response option ---- //
    protected void addOption(String name, String value) { host_session.addOption(name, value); }


    // ---- response options ---- //
//    public ArrayList<PStr> getOptions() { return response_options; }







    // -------------------------------------------------------------------------- //
    // -------------------------------- Database -------------------------------- //
    // -------------------------------------------------------------------------- //

    protected ResultSet query(String query) throws Exception {
        Statement stmt = getHostDb().createStatement();
        return stmt.executeQuery(query);
    }




    // -------------------------------- Config -------------------------------- //

    protected Statement getConfigStatement() {
        Statement stmt = null;

        try {
            stmt = getConfigDb().createStatement();
        } catch (Exception e) { e.printStackTrace(); }

        return stmt;
    }



    protected PreparedStatement getConfigStatement(String query) {
        PreparedStatement ps = null;

        try {
            ps = getConfigDb().prepareStatement(query);
        } catch (Exception e) { e.printStackTrace(); }

        return ps;
    }



//    protected ResultSet runQuery(Statement stmt, String query) {
//        ResultSet rs = null;
//        try {
//            rs = stmt.executeQuery(query);
//        } catch (Exception e) { e.printStackTrace(); }
//
//        return rs;
//    }



    protected void configSet(String key, int value) { configSet(key, "" + value); }


    protected void configSet(String key, String value) {
        String query = "UPDATE `config` SET `value` = ? WHERE `key` = ?";

        int update_result = 0;
        try {
            PreparedStatement ps = getConfigStatement(query);
            ps.setString(1, value);
            ps.setString(2, key);
            update_result = ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }

        if (update_result == 0) {
            query = "INSERT INTO `config` (`key`, `value`) VALUES (?, ?)";
            try {
                PreparedStatement ps = getConfigStatement(query);
                ps.setString(1, key);
                ps.setString(2, value);
                int insert_result = ps.executeUpdate();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }



    protected int configGeti(String key) {
        //String value = configGet(key);
        //return Integer.parseInt(value);

//x        connectConfigDb();

        int value = 0;

        String query = "SELECT `value` FROM `config` WHERE `key` = ?";

        try {
            PreparedStatement ps = getConfigStatement(query);
            ps.setString(1, key);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                value = rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }

        return value;
    }



    protected String configGet(String key) {

        String value = "";

        String query = "SELECT `value` FROM `config` WHERE `key` = ?";

        try {
            PreparedStatement ps = getConfigStatement(query);
            ps.setString(1, key);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                value = rs.getString(1);
            }
        } catch (Exception e) { e.printStackTrace(); }

        return value;
    }




    protected void configUpdateOrInsert(String table, String key, String value) {
        String query = "UPDATE `" + table + "` SET `ts` = ? WHERE `name` = ?";

        int update_result = 0;
        try {
            PreparedStatement ps = getConfigStatement(query);
            ps.setString(1, value);
            ps.setString(2, key);
            update_result = ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }

        if (update_result == 0) {
            query = "INSERT INTO `config` (`key`, `value`) VALUES (?, ?)";
            try {
                PreparedStatement ps = getConfigStatement(query);
                ps.setString(1, key);
                ps.setString(2, value);
                int insert_result = ps.executeUpdate();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }


}
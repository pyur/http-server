package ru.pyur.tst;

import ru.pyur.tst.db.DbManager;
import ru.pyur.tst.util.PStr;

import java.sql.*;
import java.util.ArrayList;


public abstract class ContentBase {

    protected ModularHost session;

    private DbManager db_manager;
    private Connection db_connection;
    private Connection db_config;



    // ---- constructor ----------------

    protected void initCommon(ModularHost session) {
        this.session = session;

        this.db_manager = session.getDbManager();
        db_connection = db_manager.getDb();
        db_config = db_manager.getConfigDb();
    }



    // ---- prototypes ------------------------------------------------

    public abstract byte[] makeContent();




    // ---- setters, getters ------------------------------------------------

//    protected DbManager getDbManager() { return session.getDbManager(); }  // db_manager

    protected String getModule() { return session.getModule(); }

    protected String getAction() { return session.getAction(); }


    // todo getFilteredQuery for numbers, only_alphabet, etc. for screening malicious data
    protected String getParam(String key) throws Exception {
        ArrayList<PStr> lsQuery = session.getQuery();

        for (PStr pair : lsQuery) {
            if (pair.key.equals(key))  return pair.value;
        }

        throw new Exception("parameter \'" + key + "\' absent.");
    }




    protected void setCode(int code) {
        session.setCode(code);
    }

    protected void setContentType(String value) { session.addOption("Content-Type", value); }

    protected void setCookie(String name, String value, int expires, String path) { session.setCookie(name, value, expires, path); }


    // ---- response option ---- //
    protected void addOption(String name, String value) { session.addOption(name, value); }


    // ---- response options ---- //
//    public ArrayList<PStr> getOptions() { return response_options; }







    // -------------------------------------------------------------------------- //
    // -------------------------------- Database -------------------------------- //
    // -------------------------------------------------------------------------- //

    protected ResultSet query(String query) throws Exception {
        Statement stmt = db_connection.createStatement();
        return stmt.executeQuery(query);
    }




    // -------------------------------- Config -------------------------------- //

    protected Statement getConfigStatement() {
        Statement stmt = null;

        try {
            stmt = db_manager.getConfigDb().createStatement();
        } catch (Exception e) { e.printStackTrace(); }

        return stmt;
    }



    protected PreparedStatement getConfigStatement(String query) {
        PreparedStatement ps = null;

        try {
            ps = db_manager.getConfigDb().prepareStatement(query);
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
 //x       connectConfigDb();

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


}
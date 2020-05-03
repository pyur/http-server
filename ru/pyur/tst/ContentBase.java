package ru.pyur.tst;

import java.sql.*;
import java.util.ArrayList;


public abstract class ContentBase {

    protected HttpSession session;

    private ArrayList<PStr> lsQuery;

    //https://developer.mozilla.org/ru/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
    private ArrayList<PStr> response_options = new ArrayList<>();




    // ---------------- Database ---------------- //

    private DbManager db_manager;
/*
    private static final String DB_URL = "jdbc:mariadb://127.0.0.1/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1";

    protected Connection m_connection;



    // -------- Config -------- //

    private static final String CONFIG_URL = "jdbc:sqlite:config.db";

    protected Connection m_config;
*/


    //constructor

    protected void initCommon(HttpSession session) {
        this.session = session;
        this.db_manager = session.getDbManager();
        lsQuery = session.getQuery();
    }




    protected String getModule() { return session.getModule(); }

    protected String getAction() { return session.getAction(); }


    // todo getFilteredQuery for numbers, only_alphabet, etc. for screening malicious data
    protected String getQuery(String key) throws Exception {
        for (PStr pair : lsQuery) {
            if (pair.key.equals(key))  return pair.value;
        }

        throw new Exception("parameter \'" + key + "\' absent.");
        //return null;
    }



    public ArrayList<PStr> getOptions() {
        return response_options;
    }


    protected void addOption(String name, String value) {
        response_options.add(new PStr(name, value));
    }


    protected void setContentType(String value) {
        response_options.add(new PStr("Content-Type", value));
    }





    // -------------------------------------------------------------------------- //
    // -------------------------------- Database -------------------------------- //
    // -------------------------------------------------------------------------- //

//    protected void closeDb() {
//        if (m_connection != null) {
//            try {
//                m_connection.close();
//            } catch (Exception e) { e.printStackTrace(); }
//        }
//    }



//    protected Statement getDb() {
//        if (m_connection == null) {
//            try {
//                m_connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//                //todo: use 'DataSource' class
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        Statement stmt = null;
//
//        try {
//            stmt = m_connection.createStatement();
//        } catch (Exception e) { e.printStackTrace(); }
//
//        return stmt;
//    }







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
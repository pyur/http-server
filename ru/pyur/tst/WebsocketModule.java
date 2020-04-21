package ru.pyur.tst;

import ru.pyur.tst.tags.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import static ru.pyur.tst.resources.Md_MakeSpriteActions.CONFIG_ACTION_ICON_UPD;
import static ru.pyur.tst.resources.Md_MakeSpriteModules.CONFIG_MODULE_ICON_UPD;


public abstract class WebsocketModule {

    protected WebsocketSession session;

    private ArrayList<PStr> lsQuery;

    private InputStream is;
    private OutputStream os;
    private WebsocketWriter websocket_writer;



    // ---------------- Database ---------------- //

    private static final String DB_URL = "jdbc:mariadb://127.0.0.1/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1";

    protected Connection m_connection;



    // -------- Config -------- //

    private static final String CONFIG_URL = "jdbc:sqlite:config.db";

    protected Connection m_config;



//    public WebsocketModule() {}
//    public WebsocketModule(Session session) {
//        //initHtml(session);
//        initCommon(session);
//    }



    private void initCommon(WebsocketSession session) {
        this.session = session;
        lsQuery = session.getQuery();
    }


    public void setStreams(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }



//    protected abstract void makeContent();



//    protected String getModule() { return session.module; }

//    protected String getAction() { return session.action; }


    // todo getFilteredQuery for numbers, only_alphabet, etc. for screening malicious data
//    protected String getQuery(String key) throws Exception {
//        for (PStr pair : lsQuery) {
//            if (pair.key.equals(key))  return pair.value;
//        }
//
//        throw new Exception("parameter \'" + key + "\' absent.");
//        //return null;
//    }





    public void dispatch() throws Exception {

        WebsocketReader wr = new WebsocketReader(is);
        websocket_writer = new WebsocketWriter(os);
        WebsocketReader.WebsocketPacket packet;


        for(;;) {
            packet = wr.read();

            //if (ws_mod_cb != null)  ws_mod_cb.cb();

            switch (packet.opcode) {
                case 0:
                    // continuation frame
                    break;

                case 1:
                    receivedText(new String(packet.payload));
                    break;

                case 2:
                    receivedBinary(packet.payload);
                    break;

                // 3-7 reserved

                case 8:
                    // close
                    break;

                case 9:
                    // ping
                    // todo: answer with pong
                    break;

                case 10:
                    // pong
                    // do reset keep-alive timeout
                    break;

                // 11-15 reserved

                default:
                    System.out.println("unknown opcode: " + packet.opcode);
                    //throw new Exception("unknown opcode");
                    break;
            }

        }  // for

    }


    protected void receivedText(String text) {
        System.out.println("text: " + text);
        //parse JSON
        //call 'action'
        //if not parsable, call 'malcious request'
    }

    protected void receivedBinary(byte[] data) {}


    protected void action(String action) {}



    protected void sendText(String text) {
        //try {
        //    wos.write(text);
        //} catch (Exception e) { e.printStackTrace(); }
    }

    protected void sendBinary(byte[] data) {
        //wos.write(data);
    }



    // ---------------- higher level abstract functions ---------------- //

    // parse JSON. call appropriate prototypes.




    // -------------------------------------------------------------------------- //
    // -------------------------------- Database -------------------------------- //
    // -------------------------------------------------------------------------- //

/*
    private void closeDb() {
        if (m_connection != null) {
            try {
                m_connection.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }



    protected Statement getDb() {
        if (m_connection == null) {
            try {
                m_connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                //todo: use 'DataSource' class
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Statement stmt = null;

        try {
            stmt = m_connection.createStatement();
        } catch (Exception e) { e.printStackTrace(); }

        return stmt;
    }
*/




    // -------------------------------- Config -------------------------------- //

/*
    protected void getConfigDb() {
        if (m_config == null) {
            try {
                m_config = DriverManager.getConnection(CONFIG_URL);
                //todo: use 'DataSource' class
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    private void closeConfig() {
        if (m_config != null) {
            try {
                m_config.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }



    protected Statement getConfigStatement() {
        Statement stmt = null;

        try {
            stmt = m_config.createStatement();
        } catch (Exception e) { e.printStackTrace(); }

        return stmt;
    }



    protected PreparedStatement getConfigStatement(String query) {
        PreparedStatement ps = null;

        try {
            ps = m_config.prepareStatement(query);
        } catch (Exception e) { e.printStackTrace(); }

        return ps;
    }



    protected ResultSet runQuery(Statement stmt, String query) {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(query);
        } catch (Exception e) { e.printStackTrace(); }

        return rs;
    }



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

        getConfigDb();

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
        getConfigDb();

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
*/



}
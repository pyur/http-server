package ru.pyur.tst;

import ru.pyur.tst.json.Json;
import ru.pyur.tst.tags.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;

import static ru.pyur.tst.resources.Md_MakeSpriteActions.CONFIG_ACTION_ICON_UPD;
import static ru.pyur.tst.resources.Md_MakeSpriteModules.CONFIG_MODULE_ICON_UPD;


public abstract class ApiContent {

    protected HttpSession session;

    private ArrayList<PStr> lsQuery;

    //https://developer.mozilla.org/ru/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
    private ArrayList<PStr> response_options = new ArrayList<>();


    // ---------------- Html ---------------- //
//x    private ArrayList<Tag> head = new ArrayList<>();
//x    private ArrayList<Tag> body = new ArrayList<>();
//x    private String title;

    // ---------------- Api ---------------- //
//x    private StringBuilder json_temp = new StringBuilder();
//x    private boolean json_temp_first = true;

    private Json request;
    private Json answer;  // response




    // ---------------- Database ---------------- //
    //todo: common 'ModuleContent'

    private static final String DB_URL = "jdbc:mariadb://127.0.0.1/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1";

    protected Connection m_connection;



    // -------- Config -------- //
    //todo: common 'ModuleContent'

    private static final String CONFIG_URL = "jdbc:sqlite:config.db";

    protected Connection m_config;



    private void initCommon(HttpSession session) {
        this.session = session;
        lsQuery = session.getQuery();
    }



    protected abstract void makeContent() throws Exception;



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




    public byte[] getContent() {
        request = new Json();
        answer = new Json();

        byte[] content = makeJson();  // todo: move function here, inline

        closeDb();
        closeConfig();

        return content;
    }



    public ArrayList<PStr> getOptions() {
        return response_options;
    }


    protected void addOption(String name, String value) {
        response_options.add(new PStr(name, value));
    }


    private void setContentType(String value) {
        response_options.add(new PStr("Content-Type", value));
    }




    // -------------------------------- Html -------------------------------- //

    protected void init(HttpSession session) {
        initCommon(session);
        //setContentType("application/json; charset=utf-8");  // utf-8 redundant
        setContentType("application/json");
        //setContentType("application/javascript");  // for JSON-P
    }



    // ---- append to head: string, int, tags ---- //

//    protected void h(String text) {
//        head.add(new PlainText(text));
//    }
//
//    protected void h(int number) {
//        head.add(new PlainText("" + number));
//    }
//
//    protected void h(Tag tag) {
//        head.add(tag);
//    }



    // ---- append to body: string, int, tags ---- //

//    protected void b(String text) {
//        body.add(new PlainText(text));
//    }
//
//    protected void b(int number) {
//        body.add(new PlainText("" + number));
//    }
//
//    protected void b(Tag tag) {
//        body.add(tag);
//    }




    // -------------------------------- Api -------------------------------- //

    public byte[] makeJson() {
        byte[] in_payload = session.getPayload();

        try {
            request.parse(new String(in_payload));
        } catch (Exception e) {
            e.printStackTrace();
            return ("{\"error\":\"request parse failed\"}").getBytes();
        }

        try {
            makeContent();
        } catch (Exception e) {
            e.printStackTrace();
            //todo: append to 'answer' e.toString());
        }

        return answer.stringify().getBytes();
    }



    protected void add(String key, String value) throws Exception {
        answer.add(key, value);
    }






    // -------------------------------------------------------------------------- //
    // -------------------------------- Database -------------------------------- //
    // todo: common
    // -------------------------------------------------------------------------- //

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




    // -------------------------------- Config -------------------------------- //

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



}
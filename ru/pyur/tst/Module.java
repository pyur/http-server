package ru.pyur.tst;

import ru.pyur.tst.tags.PlainText;
import ru.pyur.tst.tags.Tag;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public abstract class Module {

    protected Session session;

    private ArrayList<PStr> lsQuery;

    //https://developer.mozilla.org/ru/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
    private ArrayList<PStr> outOptions = new ArrayList<>();


    public static final int MODULE_TYPE_HTML = 0;
    public static final int MODULE_TYPE_JSON = 1;
    public static final int MODULE_TYPE_BINARY = 2;
    public static final int MODULE_TYPE_IMAGE_PNG = 3;
    public static final int MODULE_TYPE_IMAGE_JPG = 4;
    //public static final int MODULE_TYPE_AUDIO_MP3 = ;
    //public static final int MODULE_TYPE_VIDEO_MP4 = ;


    private int module_type;

    // ---------------- Html ---------------- //
    private ArrayList<Tag> head = new ArrayList<>();
    private ArrayList<Tag> body = new ArrayList<>();
    private String title;

    // ---------------- Json ---------------- //
    private StringBuilder json_temp = new StringBuilder();
    private boolean json_temp_first = true;

    // ---------------- Binary, Image ---------------- //
    private ByteArrayOutputStream binary_data = new ByteArrayOutputStream();



    // ---------------- Database ---------------- //

    private static final String DB_URL = "jdbc:mariadb://127.0.0.1/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1";

    private Connection m_connection;



    // -------- Config -------- //

    private static final String CONFIG_URL = "jdbc:sqlite:config.db";

    private Connection m_config;





    private void initCommon(Session session) {
        this.session = session;
        lsQuery = session.getQuery();
    }



    protected abstract void makeContent();



    protected String getModule() { return session.module; }

    protected String getAction() { return session.action; }


    // todo getFilteredQuery for numbers, only_alphabet, etc. for screening malicious data
    protected String getQuery(String key) throws Exception {
        for (PStr pair : lsQuery) {
            if (pair.key.equals(key))  return pair.value;
        }

        throw new Exception("parameter \'" + key + "\' absent.");
        //return null;
    }




    public byte[] getContents() {
        byte[] content = null;

        switch (module_type) {
            case MODULE_TYPE_HTML:
                content = makeHtml();
                break;

            case MODULE_TYPE_JSON:
                content = makeJson();
                break;

            case MODULE_TYPE_BINARY:
                content = makeBinary();
                break;

            case MODULE_TYPE_IMAGE_PNG:
                //return makeImagePng();
                break;

            case MODULE_TYPE_IMAGE_JPG:
                //return makeImageJpg();
                break;
        }

        closeDb();
        closeConfig();

        return content;
    }



    public ArrayList<PStr> getOptions() {
        return outOptions;
    }


    protected void addOption(String name, String value) {
        outOptions.add(new PStr(name, value));
    }


    private void setContentType(String value) {
        outOptions.add(new PStr("Content-Type", value));
    }




    // -------------------------------- Html -------------------------------- //

    protected void initHtml(Session session) {
        initCommon(session);
        module_type = MODULE_TYPE_HTML;
        setContentType("text/html; charset=utf-8");
        //setContentType("text/plain");
    }



    // ---- string, int, tags ---- //
    protected void b(String text) {
        body.add(new PlainText(text));
    }

    protected void b(int number) {
        body.add(new PlainText("" + number));
    }

    protected void b(Tag tag) {
        body.add(tag);
    }


    // ---- string, int, tags ---- //
    protected void h(String text) {
        head.add(new PlainText(text));
    }

    protected void h(int number) {
        head.add(new PlainText("" + number));
    }

    protected void h(Tag tag) {
        head.add(tag);
    }



    private byte[] makeHtml() {

        makeContent();

        makeHtmlHeader();


        StringBuilder html = new StringBuilder();

        for (Tag tag : head) {
            html.append(tag.toString());
        }

        for (Tag tag : body) {
            html.append(tag.toString());
        }

        html.append("</body></html>");

        return html.toString().getBytes();
    }

    // ---- temporary ---- //

    protected void makeHtmlHeader() {
        h("<!DOCTYPE html>\r\n<html><head>");
        if (title != null) {
            h("<title>");
            h(title);
            h("</title>");
        }
        h("<meta charset=\"UTF-8\">");

        h("\r\n<style>\r\n");

        File style_file = new File("inline_style.css");
        try {
            FileInputStream fis = new FileInputStream(style_file);
            byte[] style = new byte[fis.available()];
            int readed = fis.read(style);
            h(new String(style));
        } catch (Exception e) { e.printStackTrace(); }

        h("\r\n</style>\r\n");


        h("\r\n<script>\r\n");
        h("var tsSpriteActions = ");
        h(100);  // todo!!!
        h(";\r\n");

        File script_file = new File("inline_script.js");
        try {
            FileInputStream fis = new FileInputStream(script_file);
            byte[] script = new byte[fis.available()];
            int readed = fis.read(script);
            h(new String(script));
        } catch (Exception e) { e.printStackTrace(); }

        h("\r\n</script>\r\n");


        h("</head><body>");
    }




    // -------------------------------- Json -------------------------------- //

    protected void initJson(Session session) {
        initCommon(session);
        module_type = MODULE_TYPE_JSON;
        setContentType("application/json");
        //setContentType("application/javascript");  // for JSON-P
    }


    public byte[] makeJson() {
        makeContent();

        //todo: json inflater
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(json_temp);
        sb.append("}");
        return sb.toString().getBytes();
    }


    protected void appendJson(String key, String value) {
        //todo: json.append
        if (!json_temp_first)  json_temp.append(", ");

        json_temp.append("\"");
        json_temp.append(key);
        json_temp.append("\"");
        json_temp.append(": ");
        json_temp.append("\"");
        json_temp.append(value);
        json_temp.append("\"");

        if (json_temp_first)  json_temp_first = false;
    }




    // -------------------------------- Binary -------------------------------- //

    protected void initBinary(Session session) {
        initCommon(session);
        module_type = MODULE_TYPE_BINARY;
        setContentType("application/octet-stream");
    }


    public byte[] makeBinary() {
        return binary_data.toByteArray();
    }


    protected void appendBinary(byte[] bytes) {
        binary_data.write(bytes, 0, bytes.length);
    }









    // -------------------------------------------------------------------------- //
    // -------------------------------- Database -------------------------------- //
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

    private void closeConfig() {
        if (m_config != null) {
            try {
                m_config.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }



    protected Statement getConfig() {
        if (m_config == null) {
            try {
                m_config = DriverManager.getConnection(CONFIG_URL);
                //todo: use 'DataSource' class
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Statement stmt = null;

        try {
            stmt = m_config.createStatement();
        } catch (Exception e) { e.printStackTrace(); }

        return stmt;
    }



    protected ResultSet runQuery(Statement stmt, String query) {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(query);
        } catch (Exception e) { e.printStackTrace(); }

        return rs;
    }







}
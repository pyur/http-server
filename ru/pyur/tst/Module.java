package ru.pyur.tst;

import ru.pyur.tst.tags.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;

import static ru.pyur.tst.resources.Md_MakeSpriteActions.CONFIG_ACTION_ICON_UPD;
import static ru.pyur.tst.resources.Md_MakeSpriteModules.CONFIG_MODULE_ICON_UPD;


public abstract class Module {

    protected Session session;

    private ArrayList<PStr> lsQuery;

    //https://developer.mozilla.org/ru/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
    private ArrayList<PStr> out_options = new ArrayList<>();


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

    protected Connection m_connection;



    // -------- Config -------- //

    private static final String CONFIG_URL = "jdbc:sqlite:config.db";

    protected Connection m_config;



    // -------- Sprite -------- //

    public static final int SPRITE_ACTION_WIDTH = 1024;
    public static final int SPRITE_ACTION_COUNT = 64;
    public static final int SPRITE_ACTION_ICON_SIZE = 16;

    public static final int SPRITE_MODULE_WIDTH = 1024;
    public static final int SPRITE_MODULE_COUNT = 32;
    public static final int SPRITE_MODULE_ICON_SIZE = 32;

    public static final int SPRITE_MODULE2_WIDTH = 1024;
    public static final int SPRITE_MODULE2_COUNT = 16;
    public static final int SPRITE_MODULE2_ICON_SIZE = 64;



//    public Module() {}
//    public Module(Session session) {
//        //initHtml(session);
//        initCommon(session);
//    }



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
        return out_options;
    }


    protected void addOption(String name, String value) {
        out_options.add(new PStr(name, value));
    }


    private void setContentType(String value) {
        out_options.add(new PStr("Content-Type", value));
    }




    // -------------------------------- Html -------------------------------- //

    protected void initHtml(Session session) {
//z    protected void initHtml() {
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

        makeModulesBar();

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




    private void makeHtmlHeader() {
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


        int tsSpriteActions = configGeti(CONFIG_ACTION_ICON_UPD);
        int tsSpriteModules = configGeti(CONFIG_MODULE_ICON_UPD);


        h("\r\n<script>\r\n");
        h("var tsSpriteActions = ");
        h(tsSpriteActions);
        h(";\r\n");
        h("var tsSpriteModules = ");
        h(tsSpriteModules);
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




    private void makeModulesBar() {
        Div div_modules_bar = new Div();
        b(div_modules_bar);
        div_modules_bar.addClass("modules_bar");

        // ---- todo: menu = auth->get_menu()
        ModuleBar module_bar = new ModuleBar();

        for (ModuleBarItem mbi : module_bar.getModules()) {
            A mod = new A();
            div_modules_bar.add(mod);
            Url link = new Url();
            link.setModule(mbi.name);
            mod.setHref(link);

            Div div_icon = new Div();
            mod.add(div_icon);
            int x = ((mbi.id - 1) % SPRITE_MODULE_COUNT) * SPRITE_MODULE_ICON_SIZE;
            int y = ((mbi.id - 1) / SPRITE_MODULE_COUNT) * SPRITE_MODULE_ICON_SIZE;

            div_icon.addStyle("background-position", ((x == 0) ? "0" : "-" + x + "px") + " " + ((y == 0) ? "0" : "-" + y + "px") );
            div_icon.setUnselectable();


            Div div_desc = new Div();
            mod.add(div_desc);
            String desc = mbi.descb.isEmpty() ? mbi.desc : mbi.descb;
            div_desc.put(desc);
        }

    }


    private class ModuleBarItem {
        public int id;
        public String name;
        public String perm;
        public String desc;
        public String descb;
        public int pos;
        public int noauth;
        public int auth;

        public ModuleBarItem(int id, String name, String perm, String desc, String descb, int pos, int noauth, int auth) {
            this.id = id;
            this.name = name;
            this.perm = perm;
            this.desc = desc;
            this.descb = descb;
            this.pos = pos;
            this.noauth = noauth;
            this.auth = auth;
        }
    }


    private class ModuleBar {
        public ArrayList<ModuleBarItem> modules = new ArrayList<>();

        public ModuleBar() {
            getConfigDb();
            Statement stmt = getConfigStatement();

            String query = "SELECT `id`, `name`, `perm`, `desc`, `descb`, `pos`, `noauth`, `auth` FROM `module` ORDER BY `pos`, `desc`";

            try {
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    int id = rs.getInt(1);
                    String name = rs.getString(2);
                    String perm = rs.getString(3);
                    String desc = rs.getString(4);
                    String descb = rs.getString(5);
                    int pos = rs.getInt(6);
                    int noauth = rs.getInt(7);
                    int auth = rs.getInt(8);

                    modules.add(new ModuleBarItem(id, name, perm, desc, descb, pos, noauth, auth));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        public ArrayList<ModuleBarItem> getModules() { return modules; }
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
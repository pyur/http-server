package ru.pyur.tst;

import ru.pyur.tst.tags.PlainText;
import ru.pyur.tst.tags.Tag;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;


public abstract class Module {

    protected Session session;

    private ArrayList<PStr> lsQuery;


    public static final int MODULE_TYPE_HTML = 0;
    public static final int MODULE_TYPE_BINARY = 1;
    public static final int MODULE_TYPE_JSON = 2;

    private int module_type = MODULE_TYPE_HTML;

    private ArrayList<Tag> body = new ArrayList<>();

    private ByteArrayOutputStream binary_data = new ByteArrayOutputStream();

    private StringBuilder json_temp = new StringBuilder();
    private boolean json_temp_first = true;


    // ---- temporary ---- //

    private static final String DB_URL = "jdbc:mariadb://127.0.0.1/";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1";

    protected Connection m_conn;


    //public Module() {
    //    body = new StringBuilder();
    //}


    //public Module(Session session) {
    //    this.session = session;
    //}


    protected void prepare() {}


    public void prepareHtml() {
        connectToDb();

        headerBegin();

        prepare();

        headerEnd();

        closeDb();
    }



    // ---- string, int ---- //
    protected void b(String text) {
        body.add(new PlainText(text));
    }

    protected void b(int number) {
        body.add(new PlainText("" + number));
    }


    // ---- tags ---- //
    protected void b(Tag tag) {
        body.add(tag);
    }



    @Override
    public String toString() {
        StringBuilder t = new StringBuilder();

        for (Tag tag : body) {
            t.append(tag.toString());
        }

        return t.toString();
    }


    // ---- temporary ---- //

    protected void headerBegin() {
        b("<!DOCTYPE html>\r\n<html><head>");
        b("<title>");
        b("Заголовок");
        b("</title>");
        b("<meta charset=\"UTF-8\">");

        b("\n<style>\n");

        b("body {\n" +
                "\tfont-family: 'Roboto', 'Tahoma', 'Arial', sans-serif;\n" +
                "\tfont-size: 6pt;\n" +
                "\tbackground-color: #FFF;\n" +
                "\t}\n");

        b("table {\n" +
                "\tfont-size: 12pt;\n" +
                "\ttext-align: center;\n" +
                "\tborder-collapse: collapse;\n" +
                "\tborder-spacing: 0;\n" +
                "\t}\n");

        b("td {\n" +
                "\tborder: 1px solid #ccc;\n" +
                "\ttext-align: left;\n" +
                "\tpadding: 0 0 0 2px;\n" +
                "\t}\n");

        b("table.lst tr:first-child td {font-weight: bold; background-color: #ddd; text-align: center; padding: 0;}\n" +
                "table.lst tr:nth-child(odd) {background-color: #eee;}\n"
                //"table.lst td {text-align: left; padding: 0 0 0 2px; vertical-align: top; height: 30px;}\n"
        );

        b("a\t{\n" +
                "\tcolor:#006600;\n" +
                "\ttext-decoration:none;\n" +
                "\t}\n");

        b("a:hover {text-decoration: none;}\n");

        b("a:focus {outline: none;}\n");

        b("a.k\t{color: #000;}\n");

        b("a.s\t{\n" +
                "\tdisplay: inline-block;\n" +
                "\twidth: 16px;\n" +
                "\theight: 16px;\n" +
                "\tmargin: 0 2px 0 0;\n" +
                "\tvertical-align: bottom;\n" +
                "\tcursor: pointer;\n" +
                //"\tborder: 1px solid red;\n" +  // temporary
                //"\t/*background-image: url('/c/s.png');*/\n" +
                "\t}\n");

        b("</style>\n");


//        b("<script>\n");
//        b("var style = document.createElement(\"STYLE\");\n");
//        b("style.innerHTML = 'a {border: 2px dashed cyan;}'\n");
//        b("document.head.appendChild(style);\n");
//        b("</script>\n");


        b("\n<script>\n");
        b("var tsSpriteActions = ");
        b(100);
        b(";\n");

        File script_file = new File("inline_script.js");
        try {
            FileInputStream fis = new FileInputStream(script_file);
            byte[] script = new byte[fis.available()];
            int readed = fis.read(script);
            b(new String(script));
        } catch (Exception e) { e.printStackTrace(); }
        b("\n</script>\n");


        b("</head><body>");
    }


    protected void headerEnd() {
        b("</body></html>");
    }



    protected void connectToDb() {

        try {
            // -- Open a connection
            //System.out.println("Connecting to a selected database...");
            m_conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            //todo: use 'DataSource' class
            //System.out.println("Connected database successfully...");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    protected void closeDb() {
        if (m_conn != null) {
            try {
                m_conn.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }




    protected void setSession(Session session) {
        this.session = session;
        lsQuery = session.getQuery();
    }


    //protected void parseSession() {
    //    lsQuery = session.getQuery();
    //}


    protected String getModule() { return session.module; }

    protected String getAction() { return session.action; }


    // todo getFilteredQuery for numbers, only_alphabet, etc. for screening malicious data
    protected String getQuery(String key) {
        for (PStr pair : lsQuery) {
            if (pair.key.equals(key))  return pair.value;
        }

        return null;
    }




    // -------------------------------- binary -------------------------------- //

    public int getType() {
        return module_type;
    }


    protected void setType(int type) {
        module_type = type;
    }



    protected void prepareBin() {}

    public void prepareBinary() {
        prepareBin();
    }


    public byte[] getBinary() {
        return binary_data.toByteArray();
    }


    protected void appendBinary(byte[] bytes) {
        binary_data.write(bytes, 0, bytes.length);
    }




    // -------------------------------- json -------------------------------- //

    protected void prepareJsonData() {}

    public void prepareJson() {
        prepareJsonData();
    }


    public byte[] getJson() {
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


}
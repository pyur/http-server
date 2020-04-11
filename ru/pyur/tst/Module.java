package ru.pyur.tst;

import ru.pyur.tst.tags.PlainText;
import ru.pyur.tst.tags.Tag;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class Module {

    protected Session session;

    private ArrayList<PStr> lsQuery;


    //private StringBuilder body;
    private ArrayList<Tag> body = new ArrayList<>();



    // ---- temporary ---- //

    private static final String DB_URL = "jdbc:mariadb://127.0.0.1/skdev";
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


    // ---- string, int ---- //
    protected void b(String text) {
        //body.append(s);
        body.add(new PlainText(text));
    }

    protected void b(int number) {
        //body.append(i);
        body.add(new PlainText("" + number));
    }


    // ---- tags ---- //
    protected void b(Tag tag) {
        //body.append(tag.render());
        body.add(tag);
    }


    public String render() {
        //System.out.println(body.toString());
        //return body.toString();
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

        b("<style>");

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
                "table.lst tr:nth-child(odd) {background-color: #eee;}\n" +
                "table.lst td {text-align: left; padding: 0 0 0 2px;}\n");

        b("a\t{\n" +
                "\tcolor:#006600;\n" +
                "\ttext-decoration:none;\n" +
                "\t}\n");

        b("a:hover {text-decoration: none;}");

        b("a:focus {outline: none;}");

        b("a.k\t{color: #000;}");

        b("</style>");

        b("</head><body>");
    }


    protected void headerEnd() {
        b("</body></html>");
    }



    protected void connectToDb() {

        try {
            // -- Open a connection
            System.out.println("Connecting to a selected database...");
            m_conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            //todo: from DataSource
            System.out.println("Connected database successfully...");
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




    protected void parseSession() {
        lsQuery = session.getQuery();
    }


    protected String getModule() { return session.module; }

    protected String getAction() { return session.action; }


    // todo getFilteredQuery for numbers, only_alphabet, etc. for screening malicious data
    protected String getQuery(String key) {
        for (PStr pair : lsQuery) {
            if (pair.key.equals(key))  return pair.value;
        }

        return null;
    }


}
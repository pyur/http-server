package ru.pyur.tst;


// https://habr.com/ru/post/330676/

import ru.pyur.tst.client.HtmlClient;
import ru.pyur.tst.tags.ModuleUrl;
import ru.pyur.tst.test.SampleHtmlClient;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {

    public static void main(String[] args) {

        // -- Register JDBC driver
//        try {
//            Class.forName("org.mariadb.jdbc.Driver");
//        } catch (Exception e) { e.printStackTrace(); }

//        try {
//            Class.forName("org.sqlite.jdbc.Driver");
//        } catch (Exception e) { e.printStackTrace(); }
//        try {
//            Connection conn = DriverManager.getConnection("jdbc:sqlite:dbedit/config.db");
//            System.out.println("connected.");
//        } catch (Exception e) { e.printStackTrace(); }

//        TestClient client = new TestClient();
//        client.run();

        // mode 1 todo
        //HtmlClient client = new HtmlClient(new Url("https://anglesharp.azurewebsites.net/Chunked"));
        //byte[] client_payload = client.fetch();

        // mode 2
//        SampleHtmlClient client = new SampleHtmlClient();
//        try {
//            client.fetch();
//            byte[] client_payload = client.getPayload();
//            System.out.println("---- Main. payload ----------------");
//            System.out.println(new String(client_payload));
//        } catch (Exception e) { e.printStackTrace(); }


        //ServerTcp server = new ServerTcp(80);
        ServerSsl server = new ServerSsl(443);
        server.run();

    }

}

/*
<VirtualHost _default_:443>
    ServerName example.com

    SSLProxyEngine on
    ProxyPreserveHost on

    ProxyPass "/" "https://127.0.0.1:8443/"

    ProxyAddHeaders off

    SSLEngine on
    SSLCertificateFile "example.com.fullchain.pem"
    SSLCertificateKeyFile "example.com.key.pem"
</VirtualHost>
*/
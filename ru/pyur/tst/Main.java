package ru.pyur.tst;


// https://habr.com/ru/post/330676/

public class Main {

    public static void main(String[] args) {

        // -- Register JDBC driver
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (Exception e) { e.printStackTrace(); }

//        try {
//            Class.forName("org.sqlite.jdbc.Driver");
//        } catch (Exception e) { e.printStackTrace(); }


        //TestClient client = new TestClient();
        //client.run();


        ServerTcp server = new ServerTcp(80);
        server.run();

    }

}

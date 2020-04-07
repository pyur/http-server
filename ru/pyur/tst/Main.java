package ru.pyur.tst;


//import static sun.misc.Version.println;
//import static java.lang.System.out;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// https://habr.com/ru/post/330676/

public class Main {

    public static void main(String[] args) {

        // -- Register JDBC driver
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (Exception e) { e.printStackTrace(); }


        //TestClient client = new TestClient();
        //client.run();


        ServerTcp server = new ServerTcp(80);
        server.run();

    }

}

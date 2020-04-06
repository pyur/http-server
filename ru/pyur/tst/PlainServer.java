package ru.pyur.tst;


//import static sun.misc.Version.println;
//import static java.lang.System.out;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// https://habr.com/ru/post/330676/

public class PlainServer {

    private static ExecutorService service;


    public static void main(String[] args) {
        service = Executors.newFixedThreadPool(16);

        System.out.println("Server started.");

        try {
            ServerSocket server = new ServerSocket(80);

            for(;;) {
                System.out.println("waiting...");
                Socket client = server.accept();  // thread locking
                //System.out.println("Connection accepted.");

                //System.out.println("starting thread...");
                service.execute(new TransportTcp(client));
                //System.out.println("starting thread done.");
            }

        } catch (Exception e) { e.printStackTrace(); }


        service.shutdown();
    }

}

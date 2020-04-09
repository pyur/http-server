package ru.pyur.tst;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTcp {

    private ExecutorService service;

    private int port;


    public ServerTcp(int port) {
        this.port = port;
    }



    public void run() {
        service = Executors.newFixedThreadPool(16);

        System.out.println("Server started.");

        try {
            ServerSocket server = new ServerSocket(port);

            for(;;) {
                //System.out.println("waiting...");
                Socket client = server.accept();  // thread locking
                //System.out.println("Connection accepted.");

                Session session = new Session();
                service.execute(new TransportTcp(client, session));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        service.shutdown();
    }





}
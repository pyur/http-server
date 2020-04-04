package ru.pyur.tst;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


// todo: Transport
public class Session implements Runnable {

    private Socket client;

    private Http http;



    public Session(Socket client){
        this.client = client;
        http = new Http(this);
    }



    @Override
    public void run() {
        //System.out.println("client thread started.");

        try {
            //DataOutputStream out = new DataOutputStream(client.getOutputStream());
            //DataInputStream in = new DataInputStream(client.getInputStream());
            //ByteArrayOutputStream os = new ByteArrayOutputStream();
            //OutputStream os = new ByteArrayOutputStream();
            InputStream is = client.getInputStream();
            OutputStream os = client.getOutputStream();

            //ByteArrayOutputStream input_data = new ByteArrayOutputStream();


            //while (!client.isClosed()) {
                //String entry = in.readUTF();
                //System.out.println("[" + entry + "]");

                //out.writeUTF("200 OK");
                //out.flush();

                byte[] buf = new byte[8192];
                int received_size;

                for (;;) {
                    received_size = is.read(buf);

                    if (received_size == -1) {
                        // connection failed
                        break;
                    }

                    else if (received_size == 0) {
                        // remote host closed connection
                        System.out.println("received_size is 0");
                        //break;
                        continue;
                    }

                    System.out.println("received " + received_size + " bytes.");
                    //input_data.write(buf, 0, len);

                    //String str = new String(buf);
                    //System.out.println(str);


                    //String answer = "HTTP/1.1 200 OK\r\nContent-length: 16\r\n\r\nHello from Java!";
                    //os.write(answer.getBytes());
                    //os.flush();

                    //System.out.println("answer sent.");

                    http.append(buf, received_size);


                    int result = http.processStream();

                    //todo: what about process payload RIGHT after header?

                    if (result < 0) {
                        // set: unexpected protocol error
                        break;
                    }

                    else if (result > 0) {
                        System.out.println("Force close stream. Protocol signalled, that all payload is received.");
                        //return_code = SESSION_LISTEN_OK;
                        break;
                    }

                }  // for

                //if (input_data.size() > 0) {
                //    //answer = os.toString();
                //    System.out.println("[" + input_data.toString() + "]");
                //}

                //break;
            //}

            System.out.println("Client disconnected.");

            is.close();
            //os.close();

            client.close();
        } catch (Exception e) { e.printStackTrace(); }

    }




    public void send() {

    }


}
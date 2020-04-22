package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;


public class WebsocketWriter {

    private OutputStream os;


    public WebsocketWriter(OutputStream os) {
        this.os = os;
    }


    public void write(String text) throws Exception {
        write(1, text.getBytes());
    }


    public void write(byte[] data) throws Exception {
        write(2, data);
    }


    public void write(int opcode, byte[] data) throws Exception {
        ByteArrayOutputStream sos = new ByteArrayOutputStream();

        boolean fin = true;
        boolean masked = false;
        byte[] b = new byte[2];

        if (data.length > 125)  throw new Exception("packets longer than 125 bytes not supported.");
        int len_8 = data.length;

        int p1 = opcode & 0xF;
        p1 |= fin ? 0x80 : 0;

        int p2 = len_8 & 0x7F;
        p2 |= masked ? 0x80 : 0;

        b[0] = (byte)p1;
        b[1] = (byte)p2;
        sos.write(b);


        sos.write(data);


//        byte[] mask = new byte[4];
//        if (masked) {
//            //mask = new byte[4];
//            int readed = is.read(mask);
//            if (readed != 4)  throw new Exception("mask not 4 bytes");
//            //new Dump().dumpBinary(mask);
//        }


//        if (masked) {
//            for (int i = 0; i < payload_length; i++) {
//                payload[i] ^= mask[i % 4];
//            }
//        }


        byte[] packet = sos.toByteArray();

        //new Dump().dumpBinary(packet);

        os.write(packet);
    }





    //@Override
    //public void close() {}


}
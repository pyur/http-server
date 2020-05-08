package ru.pyur.tst;

import java.io.InputStream;


public class WebsocketReader extends ExpandableByteArray {

    //todo: read fragmented streams

    InputStream is;

    public class WebsocketPacket {
        int opcode;
        byte[] payload;

        public WebsocketPacket(int opcode, byte[] payload) {
            this.opcode = opcode;
            this.payload = payload;
        }
    }


    public WebsocketReader(InputStream is) {
        this.is = is;
    }


    public WebsocketPacket read() throws Exception {

        //for(;;) {  todo: read multiple fragments

        int p1 = is.read();
        if (p1 == -1)  System.out.println("p1. stream end.");
        int opcode = p1 & 0xF;
        boolean fin = (p1 & 0x80) != 0;

        int p2 = is.read();
        if (p2 == -1)  System.out.println("p2. stream end.");
        int len_8 = p2 & 0x7F;
        boolean masked = (p2 & 0x80) != 0;

        int payload_length = 0;

        if (len_8 == 126) {
            int p3 = is.read();
            int p4 = is.read();
            int len_16 = (p3 << 8) | p4;
            payload_length = len_16;
        }

        else if (len_8 == 127) {
            int p3 = is.read();
            int p4 = is.read();
            int p5 = is.read();
            int p6 = is.read();
            int len_32 = (p3 << 24) | (p4 << 16) | (p5 << 8) | p6;
            payload_length = len_32;
        }

        else {
            payload_length = len_8;
        }

        byte[] mask = new byte[4];
        if (masked) {
            //mask = new byte[4];
            int readed = is.read(mask);
            if (readed == -1)  System.out.println("mask. stream end.");
            if (readed != 4)  throw new Exception("mask not 4 bytes");
            //new Dump().dumpBinary(mask);
        }


        byte[] payload = new byte[payload_length];

//x        int off = 0;
//x        for(;;) {
//x            int readed = is.read(payload, off, payload_length - off);
//x            off += readed;
//x            if (off == payload_length)  break;
//x        }
        int readed = is.read(payload);
        if (readed < payload_length)  throw new Exception("unexpected stream end.");

        //new Dump().dumpBinary(payload);

        if (masked) {
            for (int i = 0; i < payload_length; i++) {
                payload[i] ^= mask[i % 4];
            }
        }

        //new Dump().dumpBinary(payload);

        //?opcode

        return new WebsocketPacket(opcode, payload);
    }


}
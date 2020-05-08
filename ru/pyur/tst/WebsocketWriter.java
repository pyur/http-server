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

        boolean fin = false;

        int off = 0;
        int block_len = 131072;

        for(;;) {
            int fragment_len = block_len;
            if (data.length <= off + block_len) {
                fin = true;
                fragment_len = data.length - off;
            }

            writeFragment(opcode, data, off, fragment_len, fin);

            off += block_len;
            if (data.length <= off)  break;
        }

    }



    private void writeFragment(int opcode, byte[] data, int off, int block_len, boolean fin) throws Exception {
        ByteArrayOutputStream sos = new ByteArrayOutputStream();

        boolean masked = false;

        byte[] b = new byte[10];

        int p1 = opcode & 0xF;
        p1 |= fin ? 0x80 : 0;
        b[0] = (byte)p1;

        if (block_len < 126) {
            int p2 = block_len & 0x7F;
            p2 |= masked ? 0x80 : 0;

            b[1] = (byte)p2;

            sos.write(b, 0, 2);
        }

        else if (block_len < 65536) {
            int p2 = 126;
            p2 |= masked ? 0x80 : 0;
            b[1] = (byte)p2;

            int p3 = (block_len >> 8) & 0xFF;
            int p4 = block_len & 0xFF;
            b[2] = (byte)p3;
            b[3] = (byte)p4;

            sos.write(b, 0, 4);
        }

        else {
            int p2 = 127;
            p2 |= masked ? 0x80 : 0;
            b[1] = (byte)p2;

            int p3 = 0;  // (block_len >> 56) & 0xFF;
            int p4 = 0;  // (block_len >> 48) & 0xFF;
            int p5 = 0;  // (block_len >> 40) & 0xFF;
            int p6 = 0;  // (block_len >> 32) & 0xFF;
            int p7 = (block_len >> 24) & 0xFF;
            int p8 = (block_len >> 16) & 0xFF;
            int p9 = (block_len >> 8) & 0xFF;
            int p10 = block_len & 0xFF;
            b[2] = (byte)p3;
            b[3] = (byte)p4;
            b[4] = (byte)p5;
            b[5] = (byte)p6;
            b[6] = (byte)p7;
            b[7] = (byte)p8;
            b[8] = (byte)p9;
            b[9] = (byte)p10;

            sos.write(b, 0, 10);
        }



//        if (masked) {
//            byte[] mask = new byte[4];
//            mask[0] = 0xAA;
//            mask[0] = 0xBB;
//            mask[0] = 0xCC;
//            mask[0] = 0xDD;
//            sos.write(mask);
//
//            for (int i = 0; i < block_len; i++) {
//                data[i+off] ^= mask[i % 4];
//            }
//        }


        //System.out.println("off: " + off + ", block_len: " + block_len);
        sos.write(data, off, block_len);


        byte[] packet = sos.toByteArray();

        //new Dump().dumpBinary(packet);

        os.write(packet);
    }


}
package ru.pyur.tst;

import java.io.InputStream;


public class NewlineReader extends ExpandableByteArray {

    private InputStream input_stream;



    public NewlineReader(InputStream is) {
        input_stream = is;
    }




    public byte[] read() throws Exception {
        reset();

        int received_size;
        byte[] recv = new byte[1];

        for (;;) {
            received_size = input_stream.read(recv);

            if (received_size == -1) {
                throw new Exception("newline reader. stream unexpectedly ends.");
            }

            else if (received_size == 0) {
                //System.out.println("received_size is 0");
                continue;
            }


            append(recv);


            if (count > 1) {
                if (buf[count - 2] == '\r' && buf[count - 1] == '\n') {
                    truncate(count - 2);
                    return buf;
                }
            }

        }  // for

    }




}